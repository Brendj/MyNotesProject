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
import ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.dto.AplicationForFoodConfirmDocumentsResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.error.WebApplicationException;
import ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.service.DeleteGuardianCommand;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ApplicationForFoodConfirmDocumentsCommand {

    private final Logger logger = LoggerFactory.getLogger(DeleteGuardianCommand.class);
    private final RuntimeContext runtimeContext;

    @Autowired
    public ApplicationForFoodConfirmDocumentsCommand(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    public AplicationForFoodConfirmDocumentsResponse confirmDocuments(long recordId, User user) {
        try {
            ApplicationForFood applicationForFood = loadAndCheckApplicationForFood(recordId);
            RequestFeedingItem requestFeedingItem = createRequestFeedingItem(applicationForFood);
            Processor processor = runtimeContext.getProcessor();
            RequestFeeding requestFeeding = new RequestFeeding(requestFeedingItem,
                    applicationForFood.getClient().getOrg().getIdOfOrg());
            processor.processRequestFeeding(requestFeeding);
            return AplicationForFoodConfirmDocumentsResponse.success(recordId);
        } catch (WebApplicationException wex) {
            logger.error("Error in confirm documents ApplicationForFood, ", wex);
            return AplicationForFoodConfirmDocumentsResponse.error(recordId, wex.getErrorCode(), wex.getErrorMessage());
        } catch (Exception e) {
            logger.error("Error in confirm documents ApplicationForFood, ", e);
            return AplicationForFoodConfirmDocumentsResponse.error(recordId, 500, e.getMessage());
        }
    }

    private RequestFeedingItem createRequestFeedingItem(ApplicationForFood applicationForFood) {
        RequestFeedingItem requestFeedingItem = new RequestFeedingItem(applicationForFood,
                new Date(System.currentTimeMillis()));
        requestFeedingItem.setStatus(Integer.valueOf(ApplicationForFoodState.RESUME.getCode()));
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
            if (applicationForFood.getStatus().getApplicationForFoodState() != ApplicationForFoodState.PAUSED
                    || !applicationForFood.isInoe()) {
                throw WebApplicationException.badRequest(400, "ApplicationForFood with record ID = '" + recordId
                        + "' confirm documents not available due its state");
            }
            Client client = (Client) session.load(Client.class, applicationForFood.getClient().getIdOfClient());
            if (client == null) {
                throw WebApplicationException.internalServerError(500,
                        "Error in confirm documents ApplicationForFood, client is NULL");
            }
            if (client.getOrg() == null) {
                throw WebApplicationException.internalServerError(500,
                        "Error in confirm documents ApplicationForFood, org is NULL");
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
