/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.clientgroup.managers;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroupManager;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.persistence.utils.OrgUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractGroupProcessor;
import ru.axetta.ecafe.processor.core.sync.ResultOperation;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

/**
 * User: akmukov
 * Date: 04.04.2016
 */
public class ClientgroupManagersProcessor extends AbstractGroupProcessor<ResClientgroupManagers> {
    private static final Logger logger = LoggerFactory.getLogger(ClientgroupManagersProcessor.class);
    private final ClientGroupManagerRequest clientGroupManagerRequest;
    protected final SessionFactory sessionFactorySlave;

    public ClientgroupManagersProcessor(SessionFactory sessionFactory, SessionFactory sessionFactorySlave, ClientGroupManagerRequest clientGroupManagerRequest) {
        super(sessionFactory);
        this.sessionFactorySlave = sessionFactorySlave;
        this.clientGroupManagerRequest = clientGroupManagerRequest;
    }

    public ResClientgroupManagers process() {
        ResClientgroupManagers resClientgroupManagers = new ResClientgroupManagers();
        if (clientGroupManagerRequest.getItems().size() == 0) return resClientgroupManagers;
        Long nextVersion = generateNextVersion();
        for (ClientgroupManagerItem item : clientGroupManagerRequest.getItems()) {
            if (!item.wrongItem()) {
                addResClientgroupManagerItem(resClientgroupManagers, nextVersion, item);
            }
        }
        return resClientgroupManagers;
    }

    private void addResClientgroupManagerItem(ResClientgroupManagers resClientgroupManagers, Long nextVersion,
            ClientgroupManagerItem item) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ClientGroupManager searchClientgroupManager = new ClientGroupManager(item.getIdOfClient(), item.getClientGroupName(),
                item.getOrgOwner());
        boolean isDeleted = item.getDeleteState() > 0;
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            ClientGroupManager foundClientgroupManager = findSimilarClientGroupManager(persistenceSession,
                    searchClientgroupManager);
            if (foundClientgroupManager == null) {
                if (!isDeleted) {
                    searchClientgroupManager.setVersion(nextVersion);
                    searchClientgroupManager = (ClientGroupManager) persistenceSession.merge(searchClientgroupManager);
                }
            } else {
                foundClientgroupManager.setVersion(nextVersion);
                foundClientgroupManager.setDeleted(isDeleted);
                searchClientgroupManager = (ClientGroupManager) persistenceSession.merge(foundClientgroupManager);
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
            if (foundClientgroupManager == null) {
                if (!isDeleted) {
                    resClientgroupManagers.addItem(searchClientgroupManager, 0, null);
                }
            } else {
                resClientgroupManagers.addItem(foundClientgroupManager, 0, "Clientgroup manager exist");
            }
        } catch (Exception ex) {
            String message = String
                    .format("Save clientgroup manager to database error, clientGroupName == %s, idOfClient == %s",
                            searchClientgroupManager.getClientGroupName(), searchClientgroupManager.getIdOfClient());
            logger.error(message, ex);
            resClientgroupManagers.addItem(searchClientgroupManager, 100, ex.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private ClientGroupManager findSimilarClientGroupManager(Session persistenceSession,
            ClientGroupManager clientGroupManager) {
        Criteria criteria = persistenceSession.createCriteria(ClientGroupManager.class);
        criteria.add(Restrictions.eq("clientGroupName",clientGroupManager.getClientGroupName()));
        criteria.add(Restrictions.eq("idOfClient",clientGroupManager.getIdOfClient()));
        criteria.add(Restrictions.eq("orgOwner",clientGroupManager.getOrgOwner()));
        criteria.add(Restrictions.eq("managerType",clientGroupManager.getManagerType()));
        List items = criteria.list();
        if (items == null || items.size()==0) return null;
        return getClientGroupManagerWithMaxVersion(items);
    }

    private ClientGroupManager getClientGroupManagerWithMaxVersion(List<? extends ClientGroupManager> items) {
        ClientGroupManager managerWithMaxVersion = null;
        for (ClientGroupManager manager : items) {
            if (managerWithMaxVersion == null) {
                managerWithMaxVersion = manager;
            }
            if (manager.getVersion() > managerWithMaxVersion.getVersion()) {
                managerWithMaxVersion = manager;
            }
        }
        return managerWithMaxVersion;
    }

    private Long generateNextVersion() {
        Long version = 0L;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            version = DAOUtils.nextVersionByClientgroupManager(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception ex) {
            logger.error("Failed get max clientgroup manager version, ", ex);
            version = 0L;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return version;
    }

    public ClientgroupManagerData processData(long idOfOrg) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ClientgroupManagerData clientgroupManagerData = null;
        try {
            persistenceSession = sessionFactorySlave.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientgroupManagerData = executeProcess(persistenceSession,idOfOrg);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception ex) {
            String message = String.format("Load clientgroup managers to database error, IdOfOrg == %s :", idOfOrg);
            logger.error(message, ex);
            clientgroupManagerData = new ClientgroupManagerData(new ResultOperation(100, ex.getMessage()));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return clientgroupManagerData;
    }

    private ClientgroupManagerData executeProcess(Session session,long idOfOrg) {
        DetachedCriteria idOfClient = DetachedCriteria.forClass(Client.class);
        idOfClient.createAlias("org", "o");
        idOfClient.add(Restrictions.in("o.idOfOrg", getFriendlyOrgsId(session, idOfOrg)));
        idOfClient.setProjection(Property.forName("idOfClient"));
        Criteria subCriteria = idOfClient.getExecutableCriteria(session);
        Integer countResult = subCriteria.list().size();
        ClientgroupManagerData clientGuardianData;
        if (countResult > 0) {
            Criteria criteria = session.createCriteria(ClientGroupManager.class);
            final Criterion idOfClientManager = Property.forName("idOfClient").in(idOfClient);
            criteria.add(idOfClientManager);
            criteria.add(Restrictions.gt("version", clientGroupManagerRequest.getMaxVersion()));
            List<ClientGroupManager> list = criteria.list();
            clientGuardianData = new ClientgroupManagerData(new ResultOperation(0, null));
            for (ClientGroupManager clientgroupManager : list) {
                clientGuardianData.addItem(clientgroupManager);
            }
        } else {
            clientGuardianData = new ClientgroupManagerData(new ResultOperation(400, "Client not found by this org"));
        }
        return clientGuardianData;
    }

    private Collection<Long> getFriendlyOrgsId(Session session,Long idOfOrg) {
        Org org = (Org) session.load(Org.class, idOfOrg);
        return OrgUtils.getFriendlyOrgIds(org);
    }
}
