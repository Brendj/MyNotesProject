/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.groupmanagers.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientGroupManager;
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

@Component
class AttachedGroupCommand {
    private final Logger logger = LoggerFactory.getLogger(AttachedGroupCommand.class);
    private final RuntimeContext runtimeContext;

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

            Long version = DAOUtils.nextVersionByClientgroupManager(session);
            ClientGroupManager foundClientGroupManager = (ClientGroupManager) session.load(ClientGroupManager.class, idOfClientGroupManager);
            if (foundClientGroupManager != null) {
                foundClientGroupManager.setVersion(version);
                foundClientGroupManager.setDeleted(true);
                session.save(foundClientGroupManager);
            } else {
                throw new WebApplicationException(404,
                        String.format("Руководитель группы idOfClientGroupManager='%d' не найден",idOfClientGroupManager));
            }
            session.flush();
            transaction.commit();
        } catch (WebApplicationException wex) {
            throw wex;
        } catch (Exception e) {
            logger.error("Error in dettach group, ", e);
            throw new WebApplicationException(
                    String.format("Ошибка при удалении руководителя группы id='%d'",idOfClientGroupManager), e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
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
            return getClientGroupManagerWithMaxVersion(items);
        } else {
            return null;
        }
    }

    private ClientGroupManager getClientGroupManagerWithMaxVersion(List<? extends ClientGroupManager> items) {
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
