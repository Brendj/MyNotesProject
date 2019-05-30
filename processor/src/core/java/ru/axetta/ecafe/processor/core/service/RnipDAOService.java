/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import generated.ru.gov.smev.artefacts.x.services.message_exchange.types._1.SendRequestResponse;

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
import java.util.Date;
import java.util.List;

/**
 * Created by i.semenov on 30.05.2019.
 */
@Component
@Scope("singleton")
public class RnipDAOService {
    private final static Logger logger = LoggerFactory.getLogger(RnipDAOService.class);

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    public static RnipDAOService getInstance() {
        return RuntimeContext.getAppContext().getBean(RnipDAOService.class);
    }

    @Transactional
    public List<RnipMessage> getRnipMessages() {
        Query query = entityManager.createQuery("select rm from RnipMessage rm where rm.processed = false order by rm.eventTime");
        return query.getResultList();
    }

    @Transactional
    public void saveRnipMessage(SendRequestResponse requestResponse, Contragent contragent, RnipEventType eventType) {
        RnipMessage rnipMessage = new RnipMessage(contragent, eventType, requestResponse.getMessageMetadata().getMessageId());
        entityManager.merge(rnipMessage);
    }

    @Transactional
    public void saveAsProcessed(RnipMessage rnipMessage, String responseMessage) {
        rnipMessage.setProcessed(true);
        rnipMessage.setResponseMessage(responseMessage);
        rnipMessage.setLastUpdate(new Date());
        entityManager.merge(rnipMessage);
    }

    @Transactional
    public String getRnipInfoResultString(Long idOfContragent, List<RnipEventType> eventTypes) {
        Query query = entityManager.createQuery("select rm from RnipMessage rm where rm.contragent.idOfContragent = :contragent and rm.eventType in :eventTypes order by rm.eventTime desc");
        query.setParameter("contragent", idOfContragent);
        query.setParameter("eventTypes", eventTypes);
        query.setMaxResults(10);
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
}
