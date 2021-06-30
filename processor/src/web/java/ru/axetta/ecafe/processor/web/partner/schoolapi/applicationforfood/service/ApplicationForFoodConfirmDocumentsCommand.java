/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ApplicationForFood;
import ru.axetta.ecafe.processor.core.persistence.ApplicationForFoodState;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.handlers.request.feeding.RequestFeedingItem;
import ru.axetta.ecafe.processor.core.sync.handlers.request.feeding.RequestFeedingProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.request.feeding.ResRequestFeedingETPStatuses;
import ru.axetta.ecafe.processor.core.sync.handlers.request.feeding.ResRequestFeedingItem;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.dto.AplicationForFoodConfirmDocumentsResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.service.DeleteGuardianCommand;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ApplicationForFoodConfirmDocumentsCommand {

    private Logger logger = LoggerFactory.getLogger(DeleteGuardianCommand.class);
    private final RuntimeContext runtimeContext;

    @Autowired
    public ApplicationForFoodConfirmDocumentsCommand(RuntimeContext runtimeContext)
    {
        this.runtimeContext = runtimeContext;
    }

    public AplicationForFoodConfirmDocumentsResponse confirmDocuments(long recordId, User user)
    {
        Session session = null;
        Transaction transaction = null;

        try
        {
            session = this.runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();
            ApplicationForFood applicationForFood = DAOUtils.getApplicationForFoodByRecordId(session, recordId);
            if (applicationForFood == null) return AplicationForFoodConfirmDocumentsResponse.error(recordId, 404, "ApplicationForFood with record ID = '" + recordId + "' was not found");
            if (applicationForFood.getStatus().getApplicationForFoodState() != ApplicationForFoodState.PAUSED || applicationForFood.getDtisznCode() != null) return AplicationForFoodConfirmDocumentsResponse.error(recordId, 400, "ApplicationForFood with record ID = '" + recordId + "' confirm documents not available due its state");

            long nextVersion = DAOUtils.nextVersionByApplicationForFood(session);
            long nextHistoryVersion = DAOUtils.nextVersionByApplicationForFoodHistory(session);

            Client client = (Client) session.load(Client.class, applicationForFood.getClient().getIdOfClient());
            if (client == null) return AplicationForFoodConfirmDocumentsResponse.error(recordId, 500, "Error in confirm documents ApplicationForFood, client is NULL");

            RequestFeedingItem requestFeedingItem = new RequestFeedingItem(applicationForFood, new Date(System.currentTimeMillis()));
            requestFeedingItem.setStatus(ApplicationForFoodState.RESUME.getCode());

            RequestFeedingProcessor requestFeedingProcessor = new RequestFeedingProcessor(session, null);
            Map.Entry<ResRequestFeedingItem, List<ResRequestFeedingETPStatuses>> proceedResult = requestFeedingProcessor.proceedOneItem(requestFeedingItem, nextVersion, nextHistoryVersion, client);

            session.flush();
            transaction.commit();
            transaction = null;

            requestFeedingProcessor.processETPStatuses(proceedResult.getValue());
            return AplicationForFoodConfirmDocumentsResponse.success(recordId);
        }
        catch (Exception e)
        {
            logger.error("Error in confirm documents ApplicationForFood, ", e);
            return AplicationForFoodConfirmDocumentsResponse.error(recordId, 500, "Error in confirm documents ApplicationForFood: " + e.getMessage());
        }
        finally
        {
            if (transaction != null) HibernateUtils.rollback(transaction, logger);
            if (session != null) HibernateUtils.close(session, logger);
        }
    }
}
