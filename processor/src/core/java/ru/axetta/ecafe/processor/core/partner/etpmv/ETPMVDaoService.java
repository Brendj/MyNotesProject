/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.etpmv;

import org.hibernate.query.NativeQuery;
import ru.axetta.ecafe.processor.core.partner.etpmv.enums.MessageType;
import ru.axetta.ecafe.processor.core.partner.etpmv.enums.StatusETPMessageType;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.proactive.ProactiveMessage;
import ru.axetta.ecafe.processor.core.persistence.proactive.ProactiveMessageStatus;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.axetta.ecafe.processor.core.push.model.AbstractPushData;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.DocValidationRequest;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.GuardianshipValidationRequest;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.Errors;

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
    public void saveEtpPacket(String messageId, String messageText, ETPMessageType messageType) {
        Query query = entityManager.createQuery("select count(m.etpMessageId) from EtpIncomingMessage m where m.etpMessageId like :messageId");
        query.setParameter("messageId", messageId + "%");
        Long res = (Long)query.getSingleResult();
        if (res > 0) messageId += "__" + res;

        EtpIncomingMessage etpIncomingMessage = new EtpIncomingMessage();
        etpIncomingMessage.setEtpMessageId(messageId);
        etpIncomingMessage.setEtpMessagePayload(messageText);
        etpIncomingMessage.setCreatedDate(new Date());
        etpIncomingMessage.setIsProcessed(false);
        etpIncomingMessage.setMessageType(messageType.getCode());
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
            etpOutgoingMessage.setMessageType(ETPMessageType.ZLP.getCode());
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

    public List<ApplicationForFood> findApplicationsForFoodSendedMezhvedRequest(Session session) {
        Query query = session.createQuery("select a from ApplicationForFood a where a.status in (:status1, :status2) and a.archived = false ");
        query.setParameter("status1", new ApplicationForFoodStatus(ApplicationForFoodState.GUARDIANSHIP_VALIDITY_REQUEST_SENDED));
        query.setParameter("status2", new ApplicationForFoodStatus(ApplicationForFoodState.DOC_VALIDITY_REQUEST_SENDED));
        return query.getResultList();
    }

    public List<ApplicationForFood> findApplicationsForFoodSendedBenefitRequest(Session session) {
        Query query = session.createQuery("select a from ApplicationForFood a where a.status = :status and a.archived = false");
        query.setParameter("status", new ApplicationForFoodStatus(ApplicationForFoodState.BENEFITS_VALIDITY_REQUEST_SENDED));
        return query.getResultList();
    }

    @Transactional(readOnly = true)
    public ApplicationForFood findApplicationForFood(String guid) {
        Query query = entityManager.createQuery("select a from ApplicationForFood a where a.client.meshGUID = :guid order by a.createdDate desc");
        query.setParameter("guid", guid);
        query.setMaxResults(1);
        try {
            return (ApplicationForFood) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Transactional
    public ApplicationForFood findApplicationForFoodByServiceNumber(String serviceNumber) {
        Session session = entityManager.unwrap(Session.class);
        return DAOUtils.findApplicationForFoodByServiceNumber(session, serviceNumber);
    }

    @Transactional
    public void createApplicationForGood(Client client, List<Integer> dtisznCodes, String mobile,
            String guardianName, String guardianSecondName, String guardianSurname, String serviceNumber,
            ApplicationForFoodCreatorType creatorType, Boolean validDoc, Boolean validGuardianship) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        DAOUtils.createApplicationForFood(session, client, dtisznCodes, mobile,
                guardianName, guardianSecondName, guardianSurname, serviceNumber, creatorType, validDoc, validGuardianship);
        DAOUtils.updateApplicationForFood(session, client, new ApplicationForFoodStatus(ApplicationForFoodState.REGISTERED));
        if (dtisznCodes == null) {
            DAOUtils.updateApplicationForFood(session, client, new ApplicationForFoodStatus(ApplicationForFoodState.PAUSED));
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
    public Integer getDSZNBenefit(String benefit) {
        Query query = entityManager.createQuery("select b.code from CategoryDiscountDSZN b where b.ETPCode = :benefit");
        query.setParameter("benefit", Long.parseLong(benefit));
        query.setMaxResults(1);
        return (Integer)query.getSingleResult();
    }

    @Transactional(readOnly = true)
    public List<Integer> getDSZNBenefits(List<String> benefits) {
        Query query = entityManager.createQuery("select b.code from CategoryDiscountDSZN b where b.ETPTextCode in (:benefits)");
        query.setParameter("benefits", benefits);
        return query.getResultList();
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
    public List<ApplicationForFoodHistory> getNotSendedApplicationForFoodHistory(Date startDate, Date endDate) {
        Query query = entityManager.createQuery("select h from ApplicationForFoodHistory h join fetch h.applicationForFood "
                + "where h.sendDate is null and h.createdDate between :startDate and :endDate "
                + "order by h.applicationForFood.idOfApplicationForFood, h.createdDate");
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setMaxResults(1000);
        return query.getResultList();
    }

    @Transactional
    public List<ApplicationForFood> getDataForAISContingent() {
        Query query = entityManager.createQuery("select a from ApplicationForFood a join fetch a.client c join fetch a.dtisznCodes codes "
                + " where a.sendToAISContingent = false and a.serviceNumber like :old_format "
                + " and ((codes.dtisznCode <> null and a.status = :statusDtiszn) or (codes.dtisznCode = null and a.status = :statusInoe)) "
                + " and (c.clientGroup.compositeIdOfClientGroup.idOfClientGroup < :group_employees or c.clientGroup.compositeIdOfClientGroup.idOfClientGroup = :group_displaced)");
        query.setParameter("statusDtiszn", new ApplicationForFoodStatus(ApplicationForFoodState.REGISTERED));
        query.setParameter("statusInoe", new ApplicationForFoodStatus(ApplicationForFoodState.OK));
        query.setParameter("group_employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
        query.setParameter("group_displaced", ClientGroup.Predefined.CLIENT_DISPLACED.getValue());
        query.setParameter("old_format", "%" + ETPMVService.ISPP_ID + "%");
        return query.getResultList();
    }

    @Transactional
    public void updateApplicationForFoodWithStatus(Long idOfApplicationForFood,
                                                   ApplicationForFoodStatus status) throws Exception  {
        Session session = entityManager.unwrap(Session.class);
        Long applicationVersion = getApplicationForFoodNextVersion();
        Long historyVersion = getApplicationForFoodHistoryNextVersion();
        ApplicationForFood applicationForFood = entityManager.find(ApplicationForFood.class, idOfApplicationForFood);
        DAOUtils.updateApplicationForFoodWithVersionHistorySafe(session, applicationForFood, status,
                        applicationVersion, historyVersion, true);
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
    public List<ApplicationForFood> confirmFromAISContingent(String meshGuid, Long nextVersion, Long historyVersion) {
        List<ApplicationForFood> result = new ArrayList<ApplicationForFood>();
        Session session = entityManager.unwrap(Session.class);
        Query query = entityManager.createQuery("select app from ApplicationForFood app "
                + "where app.client.idOfClient in (select c.idOfClient from Client c where c.meshGUID = :guid) and app.sendToAISContingent = false");
        if (!StringUtils.isEmpty(meshGuid)) {
            query.setParameter("guid", meshGuid);
        }
        List<ApplicationForFood> apps = query.getResultList();
        ApplicationForFoodStatus status = new ApplicationForFoodStatus(ApplicationForFoodState.INFORMATION_REQUEST_SENDED);
        for (ApplicationForFood applicationForFood : apps) {
            try {
                if (!applicationForFood.isInoe()) {
                    result.add(DAOUtils.updateApplicationForFoodWithSendToAISContingent(session, applicationForFood, status,
                            nextVersion, historyVersion));
                } else {
                    if (ApplicationForFoodState.DENIED_BENEFIT != applicationForFood.getStatus().getApplicationForFoodState()
                    && ApplicationForFoodState.DENIED_GUARDIANSHIP != applicationForFood.getStatus().getApplicationForFoodState()
                    && ApplicationForFoodState.DENIED_PASSPORT != applicationForFood.getStatus().getApplicationForFoodState()) {
                        DAOUtils.updateApplicationForFoodSendToAISContingentOnly(session, applicationForFood, nextVersion);
                    }
                }
            } catch (Exception e) {
                logger.error("Unable to update application for food", e);
            }
        }
        return result;
    }

    @Transactional
    public String getOriginalMessageFromApplicationForFood(ApplicationForFood applicationForFood) {
        List<String> list = entityManager.createQuery("select m.etpMessagePayload from EtpIncomingMessage m where m.etpMessageId = :messageId")
                .setParameter("messageId", applicationForFood.getServiceNumber())
                .getResultList();
        return list.get(0);
    }

    @Transactional
    public void saveMezhvedRequest(AbstractPushData request, String jsonString, Long idOfApplicationForFood) {
        ApplicationForFood applicationForFood = entityManager.find(ApplicationForFood.class, idOfApplicationForFood);
        AppMezhvedRequest appMezhvedRequest = new AppMezhvedRequest(request, jsonString, applicationForFood);
        entityManager.persist(appMezhvedRequest);
        if (request instanceof GuardianshipValidationRequest) {
            applicationForFood.setGuardianshipConfirmed(ApplicationForFoodMezhvedState.REQUEST_SENT);
            entityManager.merge(applicationForFood);
        }
        /*if (request instanceof BenefitValidationRequest) {
            applicationForFood.setGuardianshipConfirmed(ApplicationForFoodMezhvedState.REQUEST_SENT);
            entityManager.merge(applicationForFood);
        }*/
        if (request instanceof DocValidationRequest) {
            applicationForFood.setDocConfirmed(ApplicationForFoodMezhvedState.REQUEST_SENT);
            entityManager.merge(applicationForFood);
        }
    }

    @Transactional
    public AppMezhvedRequest updateMezhvedRequest(String requestId, List<Errors> errors, String message) {
        Query query = entityManager.createQuery("select a from AppMezhvedRequest a " +
                "where a.requestId = :requestId");
        query.setParameter("requestId", requestId);
        AppMezhvedRequest appMezhvedRequest = (AppMezhvedRequest)query.getSingleResult();
        if (errors != null)
            appMezhvedRequest.setResponseType(AppMezhvedResponseType.ERROR);
        else
            appMezhvedRequest.setResponseType(AppMezhvedResponseType.OK);
        appMezhvedRequest.setResponsePayload(message);
        appMezhvedRequest.setResponseDate(new Date());
        appMezhvedRequest.setLastUpdate(new Date());

        appMezhvedRequest = entityManager.merge(appMezhvedRequest);
        appMezhvedRequest.getApplicationForFood().getDtisznCodes().size();
        return appMezhvedRequest;
    }

    @Transactional
    public ApplicationForFood getApplicationForFoodWithDtisznCodes(String serviceNumber) {
        Query query = entityManager.createQuery("select a from ApplicationForFood a join fetch a.dtisznCodes codes "
                + " where a.serviceNumber = :serviceNumber");
        query.setParameter("serviceNumber", serviceNumber);
        try {
            return (ApplicationForFood) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Transactional
    public ApplicationForFood getApplicationForFoodWithPerson(String serviceNumber) {
        Query query = entityManager.createQuery("select a from ApplicationForFood a join fetch a.client c join fetch c.person " +
                "where a.serviceNumber = :serviceNumber");
        query.setParameter("serviceNumber", serviceNumber);
        return (ApplicationForFood)query.getSingleResult();
    }

    @Transactional
    public ClientDtisznDiscountInfo getClientDtisznDiscountInfoAppointed(Client client) {
        Query query = entityManager.createQuery("select info from ClientDtisznDiscountInfo info where info.client = :client " +
                "and info.archived = false and info.appointedMSP = true");
        query.setParameter("client", client);
        query.setMaxResults(1);
        try {
            return (ClientDtisznDiscountInfo) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public CategoryDiscountDSZN getCategoryDiscountDSZNByCode(Integer code) {
        Query query = entityManager.createQuery("select d from CategoryDiscountDSZN d where d.code=:code");
        query.setParameter("code", code);
        try {
            return (CategoryDiscountDSZN) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


    @Transactional
    public void saveMezvedKafkaError(String msg, String topic, Integer type, String error, Long idOfApplicationForFood) {
        try{
            ApplicationForFood applicationForFood = entityManager.find(ApplicationForFood.class, idOfApplicationForFood);
            AppMezhvedErrorSendKafka appMezhvedErrorSendKafka = new AppMezhvedErrorSendKafka(msg, topic, type, error, applicationForFood);
            entityManager.persist(appMezhvedErrorSendKafka);
        } catch (Exception e)
        {
            logger.error("Error in saveMezvedKafkaError: " + e);
        }
    }

    @Transactional
    public List<AppMezhvedErrorSendKafka> getMezvedKafkaError() {
        try{
            Query query = entityManager.createQuery("select a from AppMezhvedErrorSendKafka a join fetch a.applicationForFood c");
            return query.getResultList();
        } catch (Exception e)
        {
            logger.error("Error in getMezvedKafkaError: " + e);
        }
        return null;
    }

    @Transactional
    public void updateMezhvedKafkaError(AppMezhvedErrorSendKafka appMezhvedErrorSendKafka, Boolean success) {
        try {
            if (!success) {
                //Если не удалось отправить повторно, то просто обновляем время
                appMezhvedErrorSendKafka.setUpdatedate(new Date());
                entityManager.merge(appMezhvedErrorSendKafka);
            } else {
                //Если отправка успешна, то убираем запись из таблицы
                entityManager.remove(appMezhvedErrorSendKafka);
            }
        } catch (Exception e)
        {
            logger.error("Error in updateMezhvedKafkaError: " + e);
        }
    }

    @Transactional
    public void saveProactiveOutgoingMessage(String messageId, String messageText, Boolean isSent, String errorMessage) {
        try {
            EtpOutgoingMessage etpOutgoingMessage = new EtpOutgoingMessage();
            etpOutgoingMessage.setEtpMessageId(messageId);
            etpOutgoingMessage.setCreatedDate(new Date());
            etpOutgoingMessage.setEtpMessagePayload(messageText);
            etpOutgoingMessage.setIsSent(isSent);
            etpOutgoingMessage.setErrorMessage(errorMessage);
            etpOutgoingMessage.setLastUpdate(new Date());
            etpOutgoingMessage.setMessageType(ETPMessageType.PROACTIVE.getCode());
            entityManager.merge(etpOutgoingMessage);
        } catch (Exception e) {
            logger.error("Error in saving outgoing proactive ETP message: ", e);
        }
    }

    @Transactional
    public void saveProactiveMessage(Client client, Client guardian, Integer dtisznCode, String serviceNumber, String ssoid) {
        try {
            ProactiveMessage proactiveMessage = new ProactiveMessage();
            proactiveMessage.setClient(client);
            proactiveMessage.setGuardian(guardian);
            proactiveMessage.setDtisznCode(dtisznCode);
            proactiveMessage.setServicenumber(serviceNumber);
            proactiveMessage.setSsoid(ssoid);
            proactiveMessage.setMessage_type(MessageType.MOS);
            proactiveMessage.setCreateddate(new Date());
            proactiveMessage.setLastupdate(new Date());
            entityManager.persist(proactiveMessage);
        } catch (Exception e)
        {
            logger.error("Error in saveProactiveMessage: " + e);
        }
    }

    @Transactional
    public ProactiveMessage getProactiveMessages(String serviceNumber) {
        Query query = entityManager.createQuery("select d from ProactiveMessage d join fetch d.client c " +
                "join fetch c.person where " +
                "d.servicenumber=:servicenumber");
        query.setParameter("servicenumber", serviceNumber);
        try {
            return (ProactiveMessage) query.getSingleResult();
        } catch (NoResultException e) {
            logger.error("Error in getProactiveMessage: " + e);
            return null;
        }
    }

    @Transactional
    public ProactiveMessage getProactiveMessages(Client client, Client guardian, Integer dtisznCode) {
        Query query = entityManager.createQuery("select d from ProactiveMessage d where " +
                "d.client=:client and d.guardian=:guardian and d.dtisznCode=:dtisznCode");
        query.setParameter("client", client);
        query.setParameter("guardian", guardian);
        query.setParameter("dtisznCode", dtisznCode);
        try {
            return (ProactiveMessage) query.getSingleResult();
        } catch (NoResultException e) {
            logger.error("Error in getProactiveMessage: " + e);
            return null;
        }
    }

    @Transactional
    public List<ProactiveMessage> getProactiveMessages(Client client, Integer dtisznCode) {
        Query query = entityManager.createQuery("select d from ProactiveMessage d where " +
                "d.client=:client and d.dtisznCode=:dtisznCode and d.status<>:status");
        query.setParameter("client", client);
        query.setParameter("dtisznCode", dtisznCode);
        query.setParameter("status", StatusETPMessageType.REFUSE_USER);
        try {
            return (ArrayList<ProactiveMessage>) query.getResultList();
        } catch (NoResultException e) {
            logger.error("Error in getProactiveMessages: " + e);
            return new ArrayList<>();
        }
    }

    @Transactional
    public ArrayList<ProactiveMessage> getProactiveMessageStatus(StatusETPMessageType statusETPMessageType) {
        Query query = entityManager.createQuery("select d from ProactiveMessage d where " +
                "d.status=:status");
        query.setParameter("status", statusETPMessageType);
        try {
            return (ArrayList<ProactiveMessage>)query.getResultList();
        } catch (NoResultException e) {
            logger.error("Error in getProactiveMessageStatus: " + e);
            return null;
        }
    }

    @Transactional
    public void updateProactiveMessage(ProactiveMessage proactiveMessage, StatusETPMessageType statusETPMessageType) {
        try {
            proactiveMessage.setStatus(statusETPMessageType);
            proactiveMessage.setLastupdate(new Date());
            entityManager.merge(proactiveMessage);
        } catch (Exception e)
        {
            logger.error("Error in updateProactiveMessage: " + e);
        }
    }

    @Transactional
    public void saveProactiveMessageStatus(ProactiveMessage proactiveMessage, StatusETPMessageType statusETPMessageType) {
        try {
            ProactiveMessageStatus proactiveMessageStatus = new ProactiveMessageStatus();
            proactiveMessageStatus.setProactiveMessage(proactiveMessage);
            proactiveMessageStatus.setStatus(statusETPMessageType);
            proactiveMessageStatus.setCreateddate(new Date());
            entityManager.persist(proactiveMessageStatus);
            updateProactiveMessage(proactiveMessage, statusETPMessageType);
        } catch (Exception e)
        {
            logger.error("Error in saveProactiveMessageStatus: ", e);
        }
    }

    @Transactional
    public Long getNextServiceNumber() {
        try {
            Session session = entityManager.unwrap(Session.class);
            NativeQuery nativeQuery = session.createNativeQuery("select nextval('proaktiv_service_number_seq')");
            return HibernateUtils.getDbLong(nativeQuery.getSingleResult());
        } catch (Exception e)
        {
            logger.error("Error in getNextServiceNumber: " + e);
        }
        return null;
    }
}
