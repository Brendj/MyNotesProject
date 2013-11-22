/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.event;

import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 11.02.2010
 * Time: 14:26:33
 * To change this template use File | Settings | File Templates.
 */
public interface EventProcessor {

    /**
     * Warning: has to be threadsafe
     *
     * @param event
     * @param properties
     * @param eventDocumentBuilders
     * @throws Exception
     */
    void processEvent(BasicEvent event, Properties properties, Map<Integer, EventDocumentBuilder> eventDocumentBuilders)
            throws Exception;

    /**
     * Warning: has to be threadsafe
     *
     * @throws Exception
     */
    void loadEventNotificationRules() throws Exception;
}