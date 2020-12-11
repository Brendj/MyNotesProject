/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import generated.ru.gov.smev.artefacts.x.services.message_exchange.types._1.SendRequestRequest;
import generated.ru.gov.smev.artefacts.x.services.message_exchange.types._1.SendRequestResponse;
import generated.ru.mos.rnip.xsd.common._2_1.DiscountType;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.RnipEventType;
import ru.axetta.ecafe.processor.core.persistence.RnipMessage;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.bind.JAXBContext;
import java.util.Date;
import java.util.List;

/**
 * Created by i.semenov on 30.05.2019.
 */
@Component
@Scope("singleton")
public class RnipDAOService {
    private final static Logger logger = LoggerFactory.getLogger(RnipDAOService.class);

    private static JAXBContext sendRequestContext;

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    public static RnipDAOService getInstance() {
        return RuntimeContext.getAppContext().getBean(RnipDAOService.class);
    }

    private JAXBContext getSendRequestContext() throws Exception {
        if (sendRequestContext == null) sendRequestContext = JAXBContext.newInstance(DiscountType.class, SendRequestRequest.class);
        return sendRequestContext;
    }

    @Transactional
    public List<RnipMessage> getRnipMessages() {
        Query query = entityManager.createQuery("select rm from RnipMessage rm join fetch rm.contragent where rm.eventTime > :eventTime "
                + "and rm.processed = false order by rm.eventTime");
        query.setParameter("eventTime", CalendarUtils.addDays(new Date(), -30));
        query.setMaxResults(RuntimeContext.getAppContext().getBean(RNIPGetPaymentsServiceV21.class).getRemainingCapacity());
        return query.getResultList();
    }

    @Transactional
    public List<RnipMessage> getProcessedRnipMessages() {
        Query query = entityManager.createQuery("select rm from RnipMessage rm "
                + "where rm.eventTime > :eventTime and rm.processed = true and rm.ackSent = false and rm.responseMessageId is not null order by rm.eventTime");
        query.setParameter("eventTime", CalendarUtils.addDays(new Date(), -30));
        return query.getResultList();
    }

    @Transactional
    public void saveRnipMessage(SendRequestResponse requestResponse, Contragent contragent, RnipEventType eventType,
            Date startDate, Date endDate, int paging) throws Exception {
        /*JAXBContext jaxbContext = getSendRequestContext();
        Marshaller marshaller = jaxbContext.createMarshaller();
        StringWriter writer = new StringWriter();
        marshaller.marshal(requestResponse, writer);
        String message = writer.toString();*/
        String message = "";
        RnipMessage rnipMessage = new RnipMessage(contragent, eventType, message,
                requestResponse.getMessageMetadata().getMessageId(), startDate, endDate, paging);
        entityManager.merge(rnipMessage);
    }

    @Transactional
    public void saveAsProcessed(RnipMessage rnipMessage, String responseMessage, String responseMessageId, RnipEventType eventType) {
        boolean processed = RNIPLoadPaymentsServiceV21.noErrors(responseMessage) || RNIPLoadPaymentsServiceV21.noData(responseMessage)
                || RNIPLoadPaymentsServiceV21.isCatalogMessage(eventType) || RNIPLoadPaymentsServiceV21.emptyPacket(responseMessage);
        rnipMessage.setProcessed(processed);
        rnipMessage.setResponseMessage(responseMessage);
        rnipMessage.setResponseMessageId(responseMessageId);
        rnipMessage.setSucceeded(RNIPLoadPaymentsServiceV21.noErrors(responseMessage));
        rnipMessage.setLastUpdate(new Date());
        entityManager.merge(rnipMessage);
    }

    @Transactional
    public void saveAsAckSent(RnipMessage rnipMessage) {
        rnipMessage.setAckSent(true);
        rnipMessage.setLastUpdate(new Date());
        entityManager.merge(rnipMessage);
    }

    @Transactional
    public String getRnipInfoResultString(Long idOfContragent, List<RnipEventType> eventTypes) {
        Query query = entityManager.createQuery("select rm from RnipMessage rm where rm.contragent.idOfContragent = :contragent and rm.eventType in :eventTypes order by rm.eventTime desc");
        query.setParameter("contragent", idOfContragent);
        query.setParameter("eventTypes", eventTypes);
        query.setMaxResults(20);
        List<RnipMessage> list = query.getResultList();
        StringBuilder sb = new StringBuilder();
        for (RnipMessage rnipMessage : list) {
            sb.append(CalendarUtils.dateTimeToString(rnipMessage.getEventTime()));
            sb.append(" - ");
            sb.append(rnipMessage.getProcessed() ? "Получен ответ" : "Ожидание асинхронного ответа");
            sb.append(" - ");
            sb.append(rnipMessage.getResponseMessage());
            sb.append("<br/>");
        }
        return sb.toString();
    }

    public List<RnipMessage> getTodayRnipMessages(Long idOfContragent) {
        return entityManager.createQuery("select rm from RnipMessage rm where rm.contragent.idOfContragent = :contragent "
                + "and rm.eventTime between :startDate and :endDate order by rm.eventTime")
        .setParameter("contragent", idOfContragent)
        .setParameter("startDate", CalendarUtils.startOfDay(new Date()))
        .setParameter("endDate", CalendarUtils.endOfDay(new Date()))
        .getResultList();
    }
}
