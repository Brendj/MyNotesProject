/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.clients.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.ClientUpdateItem;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.ClientUpdateResult;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.ClientsUpdateResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.error.WebApplicationException;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils.updateClientRegistryVersion;

@Service
class MoveClientsCommand {

    private Logger logger = LoggerFactory.getLogger(MoveClientsCommand.class);
    private final RuntimeContext runtimeContext;

    @Autowired
    public MoveClientsCommand(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    public ClientsUpdateResponse moveClients(Collection<ClientUpdateItem> moveClientToGroups)
            throws WebApplicationException {
        ClientsUpdateResponse result = new ClientsUpdateResponse();
        if (moveClientToGroups.size() == 0) {
            return result;
        }
        try {
            long version = updateClientRegistryVersion(null);
            for (ClientUpdateItem moveClientToGroup : moveClientToGroups) {
                ClientUpdateResult moveClientResult = moveClientToOtherGroup(moveClientToGroup, version);
                result.getClients().add(moveClientResult);
            }
        } catch (Exception e) {
            throw new WebApplicationException("error in execute moveClients, ", e);
        }
        return result;
    }

    private ClientUpdateResult moveClientToOtherGroup(ClientUpdateItem movedClient, long version) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;

        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Client client = (Client) persistenceSession.get(Client.class, movedClient.getIdOfClient());
            if (client == null) {
                return ClientUpdateResult.error(movedClient.getIdOfClient(),
                        String.format("Client with ID='%d' not found", movedClient.getIdOfClient()));
            }

            String error = updateClientGroupOrGetError(movedClient.getIdOfClientGroup(), movedClient.getIdOfOrg(), persistenceSession, client);
            if (!StringUtils.isEmpty(error)) {
                return ClientUpdateResult.error(movedClient.getIdOfClient(), error);
            }

            error = updateMiddleGroupOrGetError(movedClient.getIdOfMiddleGroup(), movedClient.getIdOfMiddleGroup(), persistenceSession, client);
            if (!StringUtils.isEmpty(error)) {
                return ClientUpdateResult.error(movedClient.getIdOfClient(), error);
            }
            client.setClientRegistryVersion(version);

            persistenceSession.update(client);
            persistenceSession.flush();
            persistenceTransaction.commit();

            return ClientUpdateResult.success(movedClient.getIdOfClient());
        } catch (Exception e) {
            logger.error("Error in moved client to other group, ", e);
            return ClientUpdateResult.error(movedClient.getIdOfClient(), e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    String updateMiddleGroupOrGetError(Long idOfClientGroup, Long idOfMiddleGroup, Session session, Client client) {
        if (idOfMiddleGroup != null) {
            // перемещение в дочернюю группу
            GroupNamesToOrgs middleGroupEntity = (GroupNamesToOrgs) session
                    .get(GroupNamesToOrgs.class, idOfMiddleGroup);
            if (middleGroupEntity == null || !middleGroupEntity.getIsMiddleGroup()) {
                return String.format("Middle group with ID='%d' not found or is not the middle group ", idOfClientGroup);
            }
            // если дочерние группы различаются
            if (client.getMiddleGroup() == null || !client.getMiddleGroup().equals(middleGroupEntity.getGroupName())) {
                client.setMiddleGroup(middleGroupEntity.getGroupName());
            }
        } else {
            client.setMiddleGroup(null);
        }
        return StringUtils.EMPTY;
    }

    String updateClientGroupOrGetError(Long idOfClientGroup, Long idOfOrg, Session session, Client client) {

        if (!isChangeGroupClient(client, idOfClientGroup, idOfOrg)) {
            return StringUtils.EMPTY;
        }

        ClientGroup clientGroup = (ClientGroup) session
                .get(ClientGroup.class, new CompositeIdOfClientGroup(idOfOrg, idOfClientGroup));
        if (clientGroup == null) {
            return String.format("Group of client with ID='%d' not found", idOfClientGroup);
        }
        if (client.getClientGroup() == null || !clientGroup.equals(client.getClientGroup())) {
            ClientManager.createClientGroupMigrationHistory(session, client, client.getOrg(),
                    clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup(), clientGroup.getGroupName(),
                    ClientGroupMigrationHistory.MODIFY_IN_ARM.concat(String.format(" (ид. ОО=%s)", idOfOrg)));

            client.setClientGroup(clientGroup);
            client.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
        }
        return StringUtils.EMPTY;
    }

    private boolean isChangeGroupClient(Client client, Long idOfClientGroup, Long idOfOrg) {
        return client.getClientGroup() == null || !client.getIdOfClientGroup().equals(idOfClientGroup) || !client
                .getClientGroup().getCompositeIdOfClientGroup().getIdOfOrg().equals(idOfOrg);
    }
}
