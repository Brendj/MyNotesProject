/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.clients.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.ClientUpdateItem;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.ClientUpdateResult;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.ClientsUpdateResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.error.WebApplicationException;

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
class ExcludeFromPlanCommand {
    private final Logger logger = LoggerFactory.getLogger(ExcludeFromPlanCommand.class);
    private final RuntimeContext runtimeContext;

    @Autowired
    public ExcludeFromPlanCommand(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    public ClientsUpdateResponse excludeClients(Collection<ClientUpdateItem> updateClients) throws
            WebApplicationException {
        ClientsUpdateResponse result = new ClientsUpdateResponse();
        if (updateClients.size() == 0) {
            return result;
        }
        try {
            long version = updateClientRegistryVersion(null);
            for (ClientUpdateItem item : updateClients) {
                ClientUpdateResult updateResult = excludeClientFromPlan(item, version);
                result.getClients().add(updateResult);
            }
        } catch (Exception e) {
            logger.error("Error in update client info, ", e);
            throw new WebApplicationException("Error in update client info, ", e);
        }
        return result;
    }

    private ClientUpdateResult excludeClientFromPlan(ClientUpdateItem item, long version) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();

            Client client = (Client) session.get(Client.class, item.getIdOfClient());
            if (client == null) {
                return ClientUpdateResult.error(item.getIdOfClient(),
                        String.format("Client with ID='%d' not found", item.getIdOfClient()));
            }
            client.setDisablePlanEndDate(item.getEndExcludedDate());
            if (item.getEndExcludedDate() != null) {
                client.setUseLastEEModeForPlan(item.getUseLastEEModeForPlan());
                client.setDisablePlanCreationDate(item.getStartExcludeDate());
            }
            else {
                client.setUseLastEEModeForPlan(false);
                client.setDisablePlanCreationDate(null);
            }
            client.setUpdateTime(new Date());
            client.setClientRegistryVersion(version);

            session.update(client);
            session.flush();
            transaction.commit();
            transaction = null;
            return ClientUpdateResult.success(item.getIdOfClient());

        } catch (Exception e) {
            logger.error("Error in update client info, ", e);
            return ClientUpdateResult.error(item.getIdOfClient(), e.getMessage());
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }


}
