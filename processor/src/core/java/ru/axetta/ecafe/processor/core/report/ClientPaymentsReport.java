/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JasperPrint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 11.04.13
 * Time: 12:08
 * To change this template use File | Settings | File Templates.
 */
public class ClientPaymentsReport extends BasicReportForAllOrgJob {

    /* Логгер для отчета  ClientPaymentsReport*/
    private static final Logger logger = LoggerFactory.getLogger(ClientPaymentsReport.class);

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public ClientPaymentsReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public ClientPaymentsReport() {
    }

    @Override
    public ClientPaymentsReport createInstance() {
        return new ClientPaymentsReport();
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new ClientPaymentsBuilder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
