/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.Processor;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.handlers.request.feeding.RequestFeeding;
import ru.axetta.ecafe.processor.core.sync.handlers.request.feeding.RequestFeedingItem;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.dto.ApplicationForFoodDeclineResponse;
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
public class ApplicationForFoodDeclineCommand {

    private final Logger logger = LoggerFactory.getLogger(DeleteGuardianCommand.class);
    private final RuntimeContext runtimeContext;

    @Autowired
    public ApplicationForFoodDeclineCommand(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    public ApplicationForFoodDeclineResponse decline(long recordId, Date docOrderDate, String docOrderId, User user) {
        try {
            ApplicationForFood applicationForFood = loadAndCheckApplicationForFood(recordId);
            RequestFeedingItem requestFeedingItem = createRequestFeedingItem(applicationForFood, docOrderDate, docOrderId);
            Processor processor = runtimeContext.getProcessor();
            RequestFeeding requestFeeding = new RequestFeeding(requestFeedingItem, applicationForFood.getClient().getOrg().getIdOfOrg());
            processor.processRequestFeeding(requestFeeding);
            return ApplicationForFoodDeclineResponse.success(recordId);
        } catch (WebApplicationException wex) {
            logger.error("Error in decline ApplicationForFood, ", wex);
            return ApplicationForFoodDeclineResponse.error(recordId, wex.getErrorCode(), wex.getErrorMessage());
        } catch (Exception e) {
            logger.error("Error in decline ApplicationForFood, ", e);
            return ApplicationForFoodDeclineResponse.error(recordId, 500, e.getMessage());
        }
    }


    private RequestFeedingItem createRequestFeedingItem(ApplicationForFood applicationForFood, Date docOrderDate, String docOrderId) {
        RequestFeedingItem requestFeedingItem = new RequestFeedingItem(applicationForFood,
                new Date(System.currentTimeMillis()));
        requestFeedingItem.setDocOrderDate(docOrderDate);
        requestFeedingItem.setIdOfDocOrder(docOrderId);
        requestFeedingItem.setStatus(ApplicationForFoodState.DENIED_GUARDIANSHIP.getPureCode());
        requestFeedingItem.setDeclineReason(Integer.valueOf(ApplicationForFoodState.DENIED_GUARDIANSHIP.getReason()));
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
                    || !applicationForFood.isInoe()) {
                throw WebApplicationException.badRequest(400,
                        "ApplicationForFood with record ID = '" + recordId + "' decline not available due its state");
            }
            Client client = (Client) session.load(Client.class, applicationForFood.getClient().getIdOfClient());
            if (client == null) {
                throw WebApplicationException.internalServerError(500,"Error in decline ApplicationForFood, client is NULL");
            }
            if (client.getOrg() == null) {
                throw WebApplicationException.internalServerError(500,"Error in decline ApplicationForFood, org is NULL");
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
