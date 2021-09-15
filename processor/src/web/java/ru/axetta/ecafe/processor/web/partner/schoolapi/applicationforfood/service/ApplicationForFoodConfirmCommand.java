/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.Processor;
import ru.axetta.ecafe.processor.core.persistence.ApplicationForFood;
import ru.axetta.ecafe.processor.core.persistence.ApplicationForFoodState;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.handlers.request.feeding.RequestFeeding;
import ru.axetta.ecafe.processor.core.sync.handlers.request.feeding.RequestFeedingItem;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.dto.ApplicationForFoodConfirmResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.error.WebApplicationException;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ApplicationForFoodConfirmCommand {

    private final Logger logger = LoggerFactory.getLogger(ApplicationForFoodConfirmCommand.class);
    private final RuntimeContext runtimeContext;

    @Autowired
    public ApplicationForFoodConfirmCommand(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    public ApplicationForFoodConfirmResponse confirm(long recordId, Date docOrderDate, String docOrderId,
            Date discountStartDate, Date discountEndDate, User user) {
        try {
            ApplicationForFood applicationForFood = loadAndCheckApplicationForFood(recordId);
            RequestFeedingItem requestFeedingItem = createRequestFeedingItem(applicationForFood, docOrderDate,
                    docOrderId, discountStartDate, discountEndDate);
            Processor processor = runtimeContext.getProcessor();
            RequestFeeding requestFeeding = new RequestFeeding(requestFeedingItem,
                    applicationForFood.getClient().getOrg().getIdOfOrg());
            processor.processRequestFeeding(requestFeeding);
            return ApplicationForFoodConfirmResponse.success(recordId);
        } catch (WebApplicationException wex) {
            logger.error("Error in confirm ApplicationForFood, ", wex);
            return ApplicationForFoodConfirmResponse.error(recordId, wex.getErrorCode(), wex.getErrorMessage());
        } catch (Exception e) {
            logger.error("Error in confirm ApplicationForFood, ", e);
            return ApplicationForFoodConfirmResponse.error(recordId, 500, e.getMessage());
        }
    }

    private RequestFeedingItem createRequestFeedingItem(ApplicationForFood applicationForFood,
            Date docOrderDate, String docOrderId, Date discountStartDate, Date discountEndDate) {
        RequestFeedingItem requestFeedingItem = new RequestFeedingItem(applicationForFood,
                new Date(System.currentTimeMillis()));
        requestFeedingItem.setDocOrderDate(docOrderDate);
        requestFeedingItem.setIdOfDocOrder(docOrderId);
        requestFeedingItem.setOtherDiscountStartDate(discountStartDate);
        requestFeedingItem.setOtherDiscountEndDate(discountEndDate);
        requestFeedingItem.setStatus(ApplicationForFoodState.OK.getCode());
        requestFeedingItem.setDeclineReason(null);
        requestFeedingItem.setResCode(RequestFeedingItem.ERROR_CODE_ALL_OK);
        return requestFeedingItem;
    }

    private ApplicationForFood loadAndCheckApplicationForFood(long recordId) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = this.runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();
            ApplicationForFood applicationForFood = DAOUtils.getApplicationForFoodByRecordId(session, recordId);
            if (applicationForFood == null) {
                throw WebApplicationException.notFound(404,
                        "ApplicationForFood with record ID = '" + recordId + "' was not found");
            }
            if (applicationForFood.getStatus().getApplicationForFoodState() != ApplicationForFoodState.RESUME
                    || applicationForFood.getDtisznCode() != null) {
                throw WebApplicationException.badRequest(400,
                        "ApplicationForFood with record ID = '" + recordId + "' confirm not available due its state");
            }
            Client client = (Client) session.load(Client.class, applicationForFood.getClient().getIdOfClient());
            if (client == null) {
                throw WebApplicationException.internalServerError(500,
                        "Error in confirm ApplicationForFood, client is NULL");
            }
            if (client.getOrg() == null) {
                throw WebApplicationException.internalServerError(500,
                        "Error in confirm ApplicationForFood, org is NULL");
            }
            session.flush();
            transaction.commit();
            transaction = null;
            return applicationForFood;
        } finally {
            if (transaction != null) {
                HibernateUtils.rollback(transaction, logger);
            }
            if (session != null) {
                HibernateUtils.close(session, logger);
            }
        }

    }

}
