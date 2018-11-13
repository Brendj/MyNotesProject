/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.etpmv;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

/**
 * Created by nuc on 02.11.2018.
 */
@Component
@Scope("singleton")
public class ETPMVDaoService {
    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;
    private static final Logger logger = LoggerFactory.getLogger(ETPMVDaoService.class);

    @Transactional
    public void saveEtpPacket(String messageId, String messageText) {
        EtpIncomingMessage etpIncomingMessage = new EtpIncomingMessage();
        etpIncomingMessage.setEtpMessageId(messageId);
        etpIncomingMessage.setEtpMessagePayload(messageText);
        etpIncomingMessage.setCreatedDate(new Date());
        etpIncomingMessage.setIsProcessed(false);
        entityManager.persist(etpIncomingMessage);
    }

    @Transactional
    public void updateEtpPacketWithSuccess(String messageId) {
        EtpIncomingMessage etpIncomingMessage = entityManager.find(EtpIncomingMessage.class, messageId);
        if (etpIncomingMessage != null) {
            etpIncomingMessage.setLastUpdate(new Date());
            etpIncomingMessage.setIsProcessed(true);
            entityManager.merge(etpIncomingMessage);
        }
    }

    @Transactional
    public void saveOutgoingStatus(String messageId, String messageText, Boolean isSent) {
        try {
            EtpOutgoingMessage etpOutgoingMessage = entityManager.find(EtpOutgoingMessage.class, messageId);
            if (etpOutgoingMessage == null) {
                etpOutgoingMessage = new EtpOutgoingMessage();
                etpOutgoingMessage.setEtpMessageId(messageId);
                etpOutgoingMessage.setCreatedDate(new Date());
                etpOutgoingMessage.setEtpMessagePayload(messageText);
            }
            etpOutgoingMessage.setIsSent(isSent);
            etpOutgoingMessage.setLastUpdate(new Date());
            entityManager.merge(etpOutgoingMessage);
        } catch (Exception e) {
            logger.error("Error in saving outgoing ETP status: ", e);
        }
    }

    @Transactional
    public void saveBKStatus(String message, Boolean isSent) {
        EtpBKMessage etpBKMessage = new EtpBKMessage();
        etpBKMessage.setMessage(message);
        etpBKMessage.setCreatedDate(new Date());
        etpBKMessage.setIsSent(isSent);
        entityManager.persist(etpBKMessage);
    }

    public ApplicationForFood findApplicationForFood(String guid) {
        Query query = entityManager.createQuery("select a from ApplicationForFood a where a.client.clientGUID = :guid order by a.createdDate desc limit 1");
        query.setParameter("guid", guid);
        try {
            return (ApplicationForFood) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Transactional
    public void createApplicationForGood(Client client, Long dtisznCode, String mobile,
            String guardianName, String guardianSecondName, String guardianSurname, String serviceNumber,
            ApplicationForFoodCreatorType creatorType) {
        Session session = entityManager.unwrap(Session.class);
        DAOUtils.createApplicationForFood(session, client, dtisznCode, mobile,
                guardianName, guardianSecondName, guardianSurname, serviceNumber, creatorType);
        DAOUtils.updateApplicationForFood(session, client, new ApplicationForFoodStatus(ApplicationForFoodState.REGISTERED, null));
    }
}
