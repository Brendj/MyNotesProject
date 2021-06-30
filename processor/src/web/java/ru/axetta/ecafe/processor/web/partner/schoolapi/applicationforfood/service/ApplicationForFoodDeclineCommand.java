/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.handlers.request.feeding.RequestFeedingItem;
import ru.axetta.ecafe.processor.core.sync.handlers.request.feeding.RequestFeedingProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.request.feeding.ResRequestFeedingETPStatuses;
import ru.axetta.ecafe.processor.core.sync.handlers.request.feeding.ResRequestFeedingItem;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.dto.ApplicationForFoodDeclineResponse;
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
public class ApplicationForFoodDeclineCommand {
    private Logger logger = LoggerFactory.getLogger(DeleteGuardianCommand.class);
    private final RuntimeContext runtimeContext;
    private static final int NOT_FOUND = 404, BAD_PARAMS = 400;

    @Autowired
    public ApplicationForFoodDeclineCommand(RuntimeContext runtimeContext)
    {
        this.runtimeContext = runtimeContext;
    }

    public ApplicationForFoodDeclineResponse decline(long recordId, Date docOrderDate, String docOrderId, User user)
    {
        Session session = null;
        Transaction transaction = null;

        try
        {
            session = this.runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();
            ApplicationForFood applicationForFood = DAOUtils.getApplicationForFoodByRecordId(session, recordId);
            if (applicationForFood == null) return ApplicationForFoodDeclineResponse.error(recordId, 404, "ApplicationForFood with record ID = '" + recordId + "' was not found");
            if (applicationForFood.getStatus().getApplicationForFoodState() != ApplicationForFoodState.RESUME || applicationForFood.getDtisznCode() != null) return ApplicationForFoodDeclineResponse.error(recordId, 400, "ApplicationForFood with record ID = '" + recordId + "' decline not available due its state");

            long nextVersion = DAOUtils.nextVersionByApplicationForFood(session);
            long nextHistoryVersion = DAOUtils.nextVersionByApplicationForFoodHistory(session);

            Client client = (Client) session.load(Client.class, applicationForFood.getClient().getIdOfClient());
            if (client == null) return ApplicationForFoodDeclineResponse.error(recordId, 500, "Error in decline ApplicationForFood, client is NULL");

            RequestFeedingItem requestFeedingItem = new RequestFeedingItem(applicationForFood, new Date(System.currentTimeMillis()));
            requestFeedingItem.setDocOrderDate(docOrderDate);
            requestFeedingItem.setIdOfDocOrder(docOrderId);
            requestFeedingItem.setStatus(ApplicationForFoodState.DENIED.getCode());
            requestFeedingItem.setDeclineReason(ApplicationForFoodDeclineReason.NO_APPROVAL.getCode());

            RequestFeedingProcessor requestFeedingProcessor = new RequestFeedingProcessor(session, null);
            Map.Entry<ResRequestFeedingItem, List<ResRequestFeedingETPStatuses>> proceedResult = requestFeedingProcessor.proceedOneItem(requestFeedingItem, nextVersion, nextHistoryVersion, client);

            session.flush();
            transaction.commit();
            transaction = null;

            requestFeedingProcessor.processETPStatuses(proceedResult.getValue());
            return ApplicationForFoodDeclineResponse.success(recordId);
        }
        catch (Exception e)
        {
            logger.error("Error in confirm documents ApplicationForFood, ", e);
            return ApplicationForFoodDeclineResponse.error(recordId, 500, "Error in decline ApplicationForFood: " + e.getMessage());
        }
        finally
        {
            if (transaction != null) HibernateUtils.rollback(transaction, logger);
            if (session != null) HibernateUtils.close(session, logger);
        }
    }
}
