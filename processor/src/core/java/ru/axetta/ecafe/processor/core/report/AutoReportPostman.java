/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 16.12.2009
 * Time: 11:22:22
 * To change this template use File | Settings | File Templates.
 */
public interface AutoReportPostman {

    void postReport(String address, String subject, ReportDocument reportDocument) throws Exception;

}
