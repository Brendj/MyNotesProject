/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.clients.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.MoveClientResult;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.MoveClientToGroup;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.MoveClientsResponse;
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

    public MoveClientsResponse moveClients(Collection<MoveClientToGroup> moveClientToGroups)
            throws WebApplicationException {
        MoveClientsResponse result = new MoveClientsResponse();
        if (moveClientToGroups.size() == 0) {
            return result;
        }
        try {
            long version = updateClientRegistryVersion(null);
            for (MoveClientToGroup moveClientToGroup : moveClientToGroups) {
                MoveClientResult moveClientResult = moveClientToOtherGroup(moveClientToGroup, version);
                result.getClients().add(moveClientResult);
            }
        } catch (Exception e) {
            throw new WebApplicationException("error in execute moveClients, ", e);
        }
        return result;
    }

    private MoveClientResult moveClientToOtherGroup(MoveClientToGroup movedClient, long version) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;

        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Client client = (Client) persistenceSession.get(Client.class, movedClient.getIdOfClient());
            if (client == null) {
                return MoveClientResult.error(movedClient.getIdOfClient(),
                        String.format("Client with ID='%d' not found", movedClient.getIdOfClient()));
            }

            String error = updateClientGroupOrGetError(movedClient, persistenceSession, client);
            if (!StringUtils.isEmpty(error)) {
                return MoveClientResult.error(movedClient.getIdOfClient(), error);
            }

            error = updateMiddleGroupOrGetError(movedClient, persistenceSession, client);
            if (!StringUtils.isEmpty(error)) {
                return MoveClientResult.error(movedClient.getIdOfClient(), error);
            }
            client.setClientRegistryVersion(version);

            persistenceSession.update(client);
            persistenceSession.flush();
            persistenceTransaction.commit();

            return MoveClientResult.success(movedClient.getIdOfClient());
        } catch (Exception e) {
            logger.error("Error in moved client to other group, ", e);
            return MoveClientResult.error(movedClient.getIdOfClient(), e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private String updateMiddleGroupOrGetError(MoveClientToGroup movedClient, Session persistenceSession,
            Client client) {
        if (movedClient.getIdOfMiddleGroup() != null) {
            // перемещение в дочернюю группу
            GroupNamesToOrgs middleGroupEntity = (GroupNamesToOrgs) persistenceSession
                    .get(GroupNamesToOrgs.class, movedClient.getIdOfMiddleGroup());
            if (middleGroupEntity == null || !middleGroupEntity.getIsMiddleGroup()) {
                return String.format("Middle group with ID='%d' not found or is not the middle group ",
                        movedClient.getIdOfClientGroup());
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

    private String updateClientGroupOrGetError(MoveClientToGroup movedClient, Session persistenceSession,
            Client client) {

        if (!isChangeGroupClient(client, movedClient)) {
            return StringUtils.EMPTY;
        }

        ClientGroup clientGroup = (ClientGroup) persistenceSession.get(ClientGroup.class,
                new CompositeIdOfClientGroup(movedClient.getIdOfOrg(), movedClient.getIdOfClientGroup()));
        if (clientGroup == null) {
            return String.format("Group of client with ID='%d' not found", movedClient.getIdOfClientGroup());
        }
        if (client.getClientGroup() == null || !clientGroup.equals(client.getClientGroup())) {
            ClientManager.createClientGroupMigrationHistory(persistenceSession, client, client.getOrg(),
                    clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup(), clientGroup.getGroupName(),
                    ClientGroupMigrationHistory.MODIFY_IN_ARM
                            .concat(String.format(" (ид. ОО=%s)", movedClient.getIdOfOrg())));

            client.setClientGroup(clientGroup);
            client.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
        }
        return StringUtils.EMPTY;
    }

    private boolean isChangeGroupClient(Client client, MoveClientToGroup movedClient) {
        return client.getClientGroup() == null || !client.getIdOfClientGroup().equals(movedClient.getIdOfClientGroup())
                || !client.getClientGroup().getCompositeIdOfClientGroup().getIdOfOrg().equals(movedClient.getIdOfOrg());
    }

}
