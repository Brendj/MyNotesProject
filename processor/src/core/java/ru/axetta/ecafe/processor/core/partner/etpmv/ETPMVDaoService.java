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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public void saveOutgoingStatus(String messageId, String messageText, Boolean isSent, String errorMessage, ApplicationForFoodStatus status) {
        try {
            EtpOutgoingMessage etpOutgoingMessage = new EtpOutgoingMessage();
            etpOutgoingMessage.setEtpMessageId(messageId);
            etpOutgoingMessage.setCreatedDate(new Date());
            etpOutgoingMessage.setEtpMessagePayload(messageText);
            etpOutgoingMessage.setIsSent(isSent);
            etpOutgoingMessage.setErrorMessage(errorMessage);
            etpOutgoingMessage.setLastUpdate(new Date());
            entityManager.merge(etpOutgoingMessage);
            if (isSent) {
                Query query = entityManager.createQuery("select h from ApplicationForFoodHistory h where h.applicationForFood.serviceNumber = :serviceNumber and status = :status");
                query.setParameter("serviceNumber", messageId);
                query.setParameter("status", status);
                try {
                    ApplicationForFoodHistory history = (ApplicationForFoodHistory) query.getSingleResult();
                    history.setSendDate(new Date());
                    entityManager.merge(history);
                } catch (Exception ignore) { }
            }
        } catch (Exception e) {
            logger.error("Error in saving outgoing ETP status: ", e);
        }
    }

    @Transactional
    public void updateOutgoingStatus(EtpOutgoingMessage message, Boolean isSent) {
        message.setIsSent(isSent);
        entityManager.merge(message);
    }

    @Transactional
    public void saveBKStatus(String message, Boolean isSent) {
        EtpBKMessage etpBKMessage = new EtpBKMessage();
        etpBKMessage.setMessage(message);
        etpBKMessage.setCreatedDate(new Date());
        etpBKMessage.setIsSent(isSent);
        entityManager.persist(etpBKMessage);
    }

    @Transactional(readOnly = true)
    public ApplicationForFood findApplicationForFood(String guid) {
        Query query = entityManager.createQuery("select a from ApplicationForFood a where a.client.clientGUID = :guid order by a.createdDate desc");
        query.setParameter("guid", guid);
        query.setMaxResults(1);
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
        if (dtisznCode == null) {
            DAOUtils.updateApplicationForFood(session, client, new ApplicationForFoodStatus(ApplicationForFoodState.PAUSED, null));
        }
    }

    @Transactional(readOnly = true)
    public boolean benefitExists(String benefit) {
        Query query = entityManager.createNativeQuery("select cast (count(*) as bigint) from cf_categorydiscounts_dszn where etpcode = :benefit");
        query.setParameter("benefit", Long.parseLong(benefit));
        Object result = query.getSingleResult();
        return (((BigInteger)result).longValue() > 0);
    }

    @Transactional(readOnly = true)
    public Long getDSZNBenefit(String benefit) {
        Query query = entityManager.createQuery("select b.code from CategoryDiscountDSZN b where b.ETPCode = :benefit");
        query.setParameter("benefit", Long.parseLong(benefit));
        query.setMaxResults(1);
        return new Long((Integer)query.getSingleResult());
    }

    @Transactional(readOnly = true)
    public String getDSZNBenefitName(String benefit) {
        Query query = entityManager.createQuery("select b.description from CategoryDiscountDSZN b where b.code = :benefit");
        Integer ben;
        try {
            ben = Integer.parseInt(benefit);
        } catch (Exception e) {
            ben = 0;
        }
        query.setParameter("benefit", ben);
        query.setMaxResults(1);
        return (String)query.getSingleResult();
    }

    @Transactional(readOnly = true)
    public List<EtpOutgoingMessage> getNotSendedMessages() {
        Query query = entityManager.createQuery("select m from EtpOutgoingMessage m where m.isSent = false order by m.createdDate");
        return query.getResultList();
    }

    @Transactional
    public List<ApplicationForFood> getDataForAISContingent() {
        Query query = entityManager.createQuery("select a from ApplicationForFood a join fetch a.client c where a.sendToAISContingent = false "
                + " and ((a.dtisznCode <> null and a.status = :statusDtiszn) or (a.dtisznCode = null and a.status = :statusInoe))");
        query.setParameter("statusDtiszn", new ApplicationForFoodStatus(ApplicationForFoodState.REGISTERED, null));
        query.setParameter("statusInoe", new ApplicationForFoodStatus(ApplicationForFoodState.OK, null));
        return query.getResultList();
    }

    @Transactional
    public Long getApplicationForFoodNextVersion() {
        Session session = entityManager.unwrap(Session.class);
        return DAOUtils.nextVersionByApplicationForFood(session);
    }

    @Transactional
    public Long getApplicationForFoodHistoryNextVersion() {
        Session session = entityManager.unwrap(Session.class);
        return DAOUtils.nextVersionByApplicationForFoodHistory(session);
    }

    @Transactional
    public List<ApplicationForFood> confirmFromAISContingent(String guid, Long nextVersion, Long historyVersion) {
        List<ApplicationForFood> result = new ArrayList<ApplicationForFood>();
        Session session = entityManager.unwrap(Session.class);
        Query query = entityManager.createQuery("select app from ApplicationForFood app "
                + "where app.client.idOfClient in (select c.idOfClient from Client c where c.clientGUID = :guid) and app.sendToAISContingent = false");
        query.setParameter("guid", guid);
        List<ApplicationForFood> apps = query.getResultList();
        ApplicationForFoodStatus status = new ApplicationForFoodStatus(ApplicationForFoodState.INFORMATION_REQUEST_SENDED, null);
        for (ApplicationForFood applicationForFood : apps) {
            if (applicationForFood.getDtisznCode() != null) {
                result.add(DAOUtils.updateApplicationForFoodWithSendToAISContingent(session, applicationForFood, status,
                        nextVersion, historyVersion));
            } else {
                DAOUtils.updateApplicationForFoodSendToAISContingentOnly(session, applicationForFood, nextVersion);
            }
        }
        return result;
    }

}
