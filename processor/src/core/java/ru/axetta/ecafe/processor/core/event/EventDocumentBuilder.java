/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.event;

import ru.axetta.ecafe.processor.core.report.ReportDocument;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 28.12.2009
 * Time: 11:23:32
 * To change this template use File | Settings | File Templates.
 */
public interface EventDocumentBuilder {

    /**
     * Warning: has to be threadsafe
     *
     * @param event
     * @return
     * @throws Exception
     */
    ReportDocument buildDocument(BasicEvent event) throws Exception;

}