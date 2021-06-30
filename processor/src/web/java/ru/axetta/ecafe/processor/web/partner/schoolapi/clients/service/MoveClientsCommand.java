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
import java.util.Date;

import static ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils.updateClientRegistryVersion;

@Service
public class MoveClientsCommand {

    private final Logger logger = LoggerFactory.getLogger(MoveClientsCommand.class);
    private final RuntimeContext runtimeContext;

    @Autowired
    public MoveClientsCommand(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    public ClientsUpdateResponse moveClients(Collection<ClientUpdateItem> moveClientToGroups, User user)
            throws WebApplicationException {
        ClientsUpdateResponse result = new ClientsUpdateResponse();
        if (moveClientToGroups.size() == 0) {
            return result;
        }
        try {
            long version = updateClientRegistryVersion(null);
            for (ClientUpdateItem moveClientToGroup : moveClientToGroups) {
                ClientUpdateResult moveClientResult = moveClientToOtherGroup(moveClientToGroup, version, user);
                result.getClients().add(moveClientResult);
            }
        } catch (Exception e) {
            throw new WebApplicationException("error in execute moveClients, ", e);
        }
        return result;
    }

    private ClientUpdateResult moveClientToOtherGroup(ClientUpdateItem movedClient, long version, User user) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();

            Client client = (Client) session.get(Client.class, movedClient.getIdOfClient());
            if (client == null) {
                return ClientUpdateResult.error(movedClient.getIdOfClient(),
                        String.format("Client with ID='%d' not found", movedClient.getIdOfClient()));
            }
            ClientGroup moveToGroup = (ClientGroup) session.get(ClientGroup.class,
                    new CompositeIdOfClientGroup(movedClient.getIdOfOrg(), movedClient.getIdOfClientGroup()));

            if (moveToGroup == null) {
                return ClientUpdateResult.error(movedClient.getIdOfClient(),
                        String.format("Group of client with ID='%d' not found", movedClient.getIdOfClientGroup()));
            }

            String error = executeMoveOrError(session, movedClient, client, moveToGroup, version, user, true);
            if (!StringUtils.isEmpty(error)) {
                return ClientUpdateResult.error(movedClient.getIdOfClient(), error);
            }
            session.flush();
            transaction.commit();
            transaction = null;
            return ClientUpdateResult.success(movedClient.getIdOfClient());
        } catch (Exception e) {
            logger.error("Error in moved client to other group, ", e);
            return ClientUpdateResult.error(movedClient.getIdOfClient(), e.getMessage());
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public String executeMoveOrError(Session session, ClientUpdateItem movedClient, Client client,
            ClientGroup moveToGroup, long version, User user, boolean needUpdateMiddleGroup) {
        boolean isChangeOrg = isChangeOrg(client, movedClient.getIdOfOrg());
        if (isChangeOrg) {
            String error = updateOrgOrGetError(session, movedClient.getIdOfOrg(), client, moveToGroup, user);
            if (!StringUtils.isEmpty(error)) {
                return error;
            }
        }

        String error = updateClientGroupOrGetError(session, moveToGroup, client, user, isChangeOrg);
        if (!StringUtils.isEmpty(error)) {
            return error;
        }

        if (needUpdateMiddleGroup) {
            error = updateMiddleGroupOrGetError(session, movedClient.getIdOfMiddleGroup(),
                    movedClient.getIdOfMiddleGroup(), client);
            if (!StringUtils.isEmpty(error)) {
                return error;
            }
        }
        client.setUpdateTime(new Date());
        client.setClientRegistryVersion(version);
        session.update(client);
        return null;
    }

    private boolean isChangeOrg(Client client, Long idOfOrg) {
        return idOfOrg != null && !client.getOrg().getIdOfOrg().equals(idOfOrg);
    }

    private String updateOrgOrGetError(Session session, Long idOfOrg, Client client, ClientGroup moveToGroup,
            User user) {
        Object newOrg = session.load(Org.class, idOfOrg);
        if (newOrg == null) {
            return String.format("Organization with ID='%d' not found", idOfOrg);
        }
        try {
            // миграция клиента м/у организациями
            ClientManager
                    .addClientMigrationEntry(session, client.getOrg(), client.getClientGroup(), (Org) newOrg, client,
                            ClientGroupMigrationHistory.MODIFY_IN_WEB_ARM
                                    .concat(user != null ? user.getUserName() : ""), moveToGroup.getGroupName());
            client.setOrg((Org) newOrg);
            return StringUtils.EMPTY;
        } catch (Exception ex) {
            logger.error("Error in move client to other org, ", ex);
            return ex.getMessage();
        }
    }

    String updateClientGroupOrGetError(Session session, ClientGroup moveToGroup, Client client, User user,
            boolean isChangeOrg) {
        if (!isChangeGroupClient(client, moveToGroup.getCompositeIdOfClientGroup())) {
            return StringUtils.EMPTY;
        }
        if (!isChangeOrg) {
            // миграция клиента м/у группами в рамках одной оо
            ClientManager.createClientGroupMigrationHistory(session, client, client.getOrg(),
                    moveToGroup.getCompositeIdOfClientGroup().getIdOfClientGroup(), moveToGroup.getGroupName(),
                    ClientGroupMigrationHistory.MODIFY_IN_WEB_ARM.concat(user != null ? user.getUserName() : ""));
        }
        client.setClientGroup(moveToGroup);
        client.setIdOfClientGroup(moveToGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
        return StringUtils.EMPTY;
    }

    String updateMiddleGroupOrGetError(Session session, Long idOfClientGroup, Long idOfMiddleGroup, Client client) {
        if (idOfMiddleGroup != null) {
            // перемещение в дочернюю группу
            GroupNamesToOrgs middleGroupEntity = (GroupNamesToOrgs) session
                    .get(GroupNamesToOrgs.class, idOfMiddleGroup);
            if (middleGroupEntity == null || !middleGroupEntity.getIsMiddleGroup()) {
                return String
                        .format("Middle group with ID='%d' not found or is not the middle group ", idOfClientGroup);
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

    private boolean isChangeGroupClient(Client client, CompositeIdOfClientGroup compositeIdOfClientGroup) {
        return client.getClientGroup() == null || !client.getClientGroup().getCompositeIdOfClientGroup()
                .equals(compositeIdOfClientGroup);
    }


}
