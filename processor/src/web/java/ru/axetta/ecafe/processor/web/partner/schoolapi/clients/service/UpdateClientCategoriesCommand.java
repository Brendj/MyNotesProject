/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.clients.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.DiscountManager;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.DiscountChangeHistory;
import ru.axetta.ecafe.processor.core.persistence.User;
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
import org.springframework.stereotype.Component;

import java.util.*;

import static ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils.updateClientRegistryVersion;

@Component
class UpdateClientCategoriesCommand {

    private final Logger logger = LoggerFactory.getLogger(UpdateClientCategoriesCommand.class);
    private final RuntimeContext runtimeContext;

    @Autowired
    public UpdateClientCategoriesCommand(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    public ClientsUpdateResponse updateDiscounts(Collection<ClientUpdateItem> updateClients, User user) {
        ClientsUpdateResponse result = new ClientsUpdateResponse();
        if (updateClients.size() == 0) {
            return result;
        }
        try {
            long version = updateClientRegistryVersion(null);
            for (ClientUpdateItem item : updateClients) {
                ClientUpdateResult updateResult = updateDiscountsForClient(item, version, user);
                result.getClients().add(updateResult);
            }
        } catch (Exception e) {
            logger.error("Error in update client info, ", e);
            throw new WebApplicationException("Error in update client info, ", e);
        }
        return result;
    }

    private ClientUpdateResult updateDiscountsForClient(ClientUpdateItem item, long version, User user) {
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

            Set<CategoryDiscount> oldCategoriesDiscounts = client.getCategories();
            Set<CategoryDiscount> newCategoriesDiscounts = getNewCategoryDiscounts(item, session);

            boolean wasUpdates = false;
            if (!newCategoriesDiscounts.equals(oldCategoriesDiscounts)) {
                wasUpdates = true;
                client.setCategories(newCategoriesDiscounts);
                client.setLastDiscountsUpdate(new Date());
                DiscountManager.saveDiscountHistory(session, client, client.getOrg(), oldCategoriesDiscounts,
                        newCategoriesDiscounts, client.getDiscountMode(), client.getDiscountMode(),
                        DiscountChangeHistory.MODIFY_IN_WEB_ARM.concat(user != null ? user.getUserName() : ""));
            }
            if (item.getUseLastEEModeForPlan() != null || item.getStartExcludeDate() != null
                    || item.getEndExcludedDate() != null) {
                wasUpdates = true;
                ExcludeFromPlanCommand.setDisableFromPlan(item.getStartExcludeDate(), item.getEndExcludedDate(), item.getUseLastEEModeForPlan(), client);
            }
            if (wasUpdates) {
                client.setUpdateTime(new Date());
                client.setClientRegistryVersion(version);
            }
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

    private Set<CategoryDiscount> getNewCategoryDiscounts(ClientUpdateItem item, Session session) {
        List<Long> categoriesIds = item.getCategoriesDiscounts();
        if (!categoriesIds.isEmpty()) {
            return new HashSet<CategoryDiscount>(
                    session.createQuery("select cd from CategoryDiscount cd where idOfCategoryDiscount in :categoryIds")
                            .setParameterList("categoryIds", categoriesIds).list());

        } else {
            return new HashSet<>();
        }
    }

}
