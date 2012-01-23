/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.event;

import ru.axetta.ecafe.processor.core.persistence.ReportHandleRule;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 15.12.2009
 * Time: 13:55:14
 * To change this template use File | Settings | File Templates.
 */
public class EventNotificator {

    private static final Logger logger = LoggerFactory.getLogger(EventNotificator.class);
    private final ExecutorService executorService;
    private final EventProcessor eventProcessor;
    private final SessionFactory sessionFactory;
    private final DateFormat timeFormat;
    private final Map<Class, Map<Integer, EventDocumentBuilder>> documentBuilders;

    public EventNotificator(ExecutorService executorService, EventProcessor eventProcessor,
            SessionFactory sessionFactory, String eventPath, DateFormat dateFormat, DateFormat timeFormat) {
        this.executorService = executorService;
        this.eventProcessor = eventProcessor;
        this.sessionFactory = sessionFactory;
        this.timeFormat = timeFormat;
        this.documentBuilders = new HashMap<Class, Map<Integer, EventDocumentBuilder>>();

        Map<Integer, EventDocumentBuilder> paymentProcessEventDocumentBuilders = new HashMap<Integer, EventDocumentBuilder>();
        paymentProcessEventDocumentBuilders.put(ReportHandleRule.HTML_FORMAT,
                new PaymentProcessEvent.HtmlEventBuilder(eventPath, (DateFormat) dateFormat.clone(),
                        (DateFormat) timeFormat.clone()));
        this.documentBuilders.put(PaymentProcessEvent.class, paymentProcessEventDocumentBuilders);

        Map<Integer, EventDocumentBuilder> syncEventDocumentBuilders = new HashMap<Integer, EventDocumentBuilder>();
        syncEventDocumentBuilders.put(ReportHandleRule.HTML_FORMAT,
                new SyncEvent.HtmlEventBuilder(eventPath, (DateFormat) dateFormat.clone(),
                        (DateFormat) timeFormat.clone()));
        this.documentBuilders.put(SyncEvent.class, syncEventDocumentBuilders);
    }

    /**
     * Warning: has to be threadsafe
     *
     * @param event
     */
    public void fire(PaymentProcessEvent.RawEvent event) {
        try {
            executorService.execute(
                    new PaymentProcessEvent.ProcessTask(eventProcessor, sessionFactory, event, getTimeFormat(),
                            documentBuilders.get(PaymentProcessEvent.class)));
        } catch (Exception e) {
            logger.error(String.format("Failed to fire event: %s", event), e);
        }
    }

    /**
     * Warning: has to be threadsafe
     *
     * @param event
     */
    public void fire(SyncEvent.RawEvent event) {
        try {
            executorService.execute(new SyncEvent.ProcessTask(eventProcessor, sessionFactory, event, getTimeFormat(),
                    documentBuilders.get(SyncEvent.class)));
        } catch (Exception e) {
            logger.error(String.format("Failed to fire event: %s", event), e);
        }
    }

    private DateFormat getTimeFormat() {
        synchronized (this.timeFormat) {
            return (DateFormat) this.timeFormat.clone();
        }
    }


}