/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.groupmanagers.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.schoolapi.error.WebApplicationException;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groupmanagers.dto.ClientGroupManagerDTO;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
class AttachedGroupCommand {

    private final Logger logger = LoggerFactory.getLogger(AttachedGroupCommand.class);
    private final RuntimeContext runtimeContext;
    private final static int CLIENT_NOT_FOUND = 101;
    private final static int CLIENT_GROUP_NOT_FOUND = 102;
    private final static int ORG_GROUP_IS_NOT_FRIENDLY = 103;
    private final static int GROUP_MANAGER_NOT_FOUND = 104;

    @Autowired
    public AttachedGroupCommand(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    public ClientGroupManager attachedGroup(ClientGroupManagerDTO groupManager) {
        Session session = null;
        Transaction transaction = null;
        ClientGroupManager result;
        try {
            session = runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();
            checkClientOrRaiseError(session, groupManager);
            checkGroupOrRaiseError(session, groupManager);
            Long version = DAOUtils.nextVersionByClientgroupManager(session);
            ClientGroupManager newClientGroupManager = new ClientGroupManager(groupManager.getIdOfClient(),
                    groupManager.getGroupName(), groupManager.getIdOfOrg());
            ClientGroupManager foundSimilarClientGroupManager = findSimilarClientGroupManager(session,
                    newClientGroupManager);
            if (foundSimilarClientGroupManager == null) {
                newClientGroupManager.setVersion(version);
                newClientGroupManager.setDeleted(false);
                session.save(newClientGroupManager);
                result = newClientGroupManager;
            } else {
                foundSimilarClientGroupManager.setVersion(version);
                session.save(foundSimilarClientGroupManager);
                result = foundSimilarClientGroupManager;
            }
            session.flush();
            transaction.commit();
            return result;
        } catch (WebApplicationException wex) {
            throw wex;
        } catch (Exception e) {
            logger.error("Error in attach group to manager groups, ", e);
            throw new WebApplicationException(
                    String.format("Ошибка при добавлении руководителя группы id='%d' к группе name='%s'",
                            groupManager.getIdOfClient()), e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public void dettachedGroup(Long idOfClientGroupManager) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();

            ClientGroupManager foundClientGroupManager = (ClientGroupManager) session
                    .load(ClientGroupManager.class, idOfClientGroupManager);
            if (foundClientGroupManager != null) {
                Long version = DAOUtils.nextVersionByClientgroupManager(session);
                foundClientGroupManager.setVersion(version);
                foundClientGroupManager.setDeleted(true);
                session.save(foundClientGroupManager);
            } else {
                throw new WebApplicationException(GROUP_MANAGER_NOT_FOUND,
                        String.format("Group manager with idOfClientGroupManager='%d' not found",
                                idOfClientGroupManager));
            }
            session.flush();
            transaction.commit();
        } catch (WebApplicationException wex) {
            throw wex;
        } catch (Exception e) {
            logger.error("Error in dettach group, ", e);
            throw new WebApplicationException(
                    String.format("Ошибка при удалении руководителя группы id='%d'", idOfClientGroupManager), e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private void checkClientOrRaiseError(Session session, ClientGroupManagerDTO clientGroupManager) {
        Client client = (Client) session.load(Client.class, clientGroupManager.getIdOfClient());
        if (client == null) {
            throw new WebApplicationException(CLIENT_NOT_FOUND,
                    String.format("Client with id='%d' not found", clientGroupManager.getIdOfClient()));
        }
        Org clientOrg = client.getOrg();
        checkOrgOrRaiseError(clientOrg.getFriendlyOrg(), clientOrg.getIdOfOrg(), clientGroupManager.getIdOfOrg());
    }

    private void checkGroupOrRaiseError(Session session, ClientGroupManagerDTO clientGroupManager) {
        CompositeIdOfClientGroup idOfClientGroup = new CompositeIdOfClientGroup(clientGroupManager.getIdOfOrg(),
                clientGroupManager.getIdOfClientGroup());
        ClientGroup clientGroup = (ClientGroup) session.get(ClientGroup.class, idOfClientGroup);
        if (clientGroup == null) {
            throw new WebApplicationException(CLIENT_GROUP_NOT_FOUND,
                    String.format("Client group with idOfClientGroup='%d' not found",
                            clientGroupManager.getIdOfClientGroup()));
        }
    }

    private void checkOrgOrRaiseError(Set<Org> friendlyOrgs, Long clientOrgId, Long checkedOrgId) {
        boolean found = false;
        for (Org friendOrg : friendlyOrgs) {
            if (friendOrg.getIdOfOrg().equals(checkedOrgId)) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new WebApplicationException(ORG_GROUP_IS_NOT_FRIENDLY, String.format(
                    "Organization of group with idOfOrg='%d' is not friendly for client organization with idOfOrg='%d',",
                    checkedOrgId, clientOrgId));
        }
    }

    private ClientGroupManager findSimilarClientGroupManager(Session session, ClientGroupManager clientGroupManager) {
        Criteria criteria = session.createCriteria(ClientGroupManager.class);
        criteria.add(Restrictions.eq("clientGroupName", clientGroupManager.getClientGroupName()));
        criteria.add(Restrictions.eq("idOfClient", clientGroupManager.getIdOfClient()));
        criteria.add(Restrictions.eq("orgOwner", clientGroupManager.getOrgOwner()));
        criteria.add(Restrictions.eq("managerType", clientGroupManager.getManagerType()));
        List items = criteria.list();
        if (items != null && items.size() != 0) {
            return filterClientGroupManagerWithMaxVersion(items);
        } else {
            return null;
        }
    }

    private ClientGroupManager filterClientGroupManagerWithMaxVersion(List<? extends ClientGroupManager> items) {
        ClientGroupManager itemWithMaxVersion = items.get(0);
        for (int i = 1, itemsSize = items.size(); i < itemsSize; i++) {
            ClientGroupManager manager = items.get(i);
            if (manager.getVersion() > itemWithMaxVersion.getVersion()) {
                itemWithMaxVersion = manager;
            }
        }
        return itemWithMaxVersion;
    }


}
