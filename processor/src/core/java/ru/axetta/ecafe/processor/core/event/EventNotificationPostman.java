/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.event;

import ru.axetta.ecafe.processor.core.report.ReportDocument;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 16.12.2009
 * Time: 11:22:22
 * To change this template use File | Settings | File Templates.
 */
public interface EventNotificationPostman {

    void postEvent(String address, String subject, ReportDocument eventDocument) throws Exception;

}