/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.groups.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.GroupNamesToOrgs;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.schoolapi.error.WebApplicationException;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto.MiddleGroupResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils.updateClientRegistryVersion;

@Component
class DeleteMiddleGroupCommand extends BaseMiddleGroupCommand{

    private final Logger logger = LoggerFactory.getLogger(DeleteMiddleGroupCommand.class);
    private final RuntimeContext runtimeContext;
    private static final int GROUP_NOT_FOUND = 101, GROUP_NOT_CORRECT_STATE = 102;

    @Autowired
    public DeleteMiddleGroupCommand(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    public MiddleGroupResponse deleteGroup(Long idOfMiddleGroup) {
        MiddleGroupResponse response;
        Session session = null;
        Transaction transaction = null;
        try {
            session = runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();

            GroupNamesToOrgs middleGroupItem = foundMiddleGroupById(session, idOfMiddleGroup);
            if (middleGroupItem == null) {
                throw WebApplicationException.notFound(GROUP_NOT_FOUND,
                        String.format("Middle group with  id='%d' not found", idOfMiddleGroup));
            }
            if (middleGroupItem.getIsMiddleGroup() == null || !middleGroupItem.getIsMiddleGroup()) {
                throw WebApplicationException.internalServerError(GROUP_NOT_CORRECT_STATE,
                        String.format("Middle group with  id='%d' is not correct state", idOfMiddleGroup));
            }
            resetMiddleGroupForClients(session, middleGroupItem);
            session.delete(middleGroupItem);
            response = MiddleGroupResponse.from(middleGroupItem);
            session.flush();
            transaction.commit();
            transaction = null;
            return response;
        } catch (WebApplicationException wex) {
            throw wex;
        } catch (Exception e) {
            logger.error("Error in delete middle group, ", e);
            throw new WebApplicationException(
                    String.format("Error in delete middle group with id='%d'", idOfMiddleGroup), e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private void resetMiddleGroupForClients(Session session, GroupNamesToOrgs middleGroup) throws Exception {
        List<Client> clientsInMiddleGroups = getClientListWithMiddleGroup(session, middleGroup);
        if (clientsInMiddleGroups != null && !clientsInMiddleGroups.isEmpty()) {
            long version = updateClientRegistryVersion(null);
            for (Client client : clientsInMiddleGroups) {
                resetMiddleGroupForClient(session, client, version);
            }
        }
    }



}
