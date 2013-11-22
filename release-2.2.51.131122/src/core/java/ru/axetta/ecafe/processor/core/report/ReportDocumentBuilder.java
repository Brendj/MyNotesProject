/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 28.12.2009
 * Time: 11:23:32
 * To change this template use File | Settings | File Templates.
 */
public interface ReportDocumentBuilder {

    ReportDocument buildDocument(String ruleId, BasicReport report) throws Exception;

}
