/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.enterevents;

import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportForListOrgsJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class EnterEventsMonitoringReport extends BasicReportForListOrgsJob {
    private static final Logger logger = LoggerFactory.getLogger(EnterEventsMonitoringReport.class);
    private String htmlReport;

    public EnterEventsMonitoringReport(Date generateTime, long generateDuration, JasperPrint jasperPrint,
            Date startTime, Date endTime) {
        super(generateTime, generateDuration, jasperPrint, startTime, endTime);
    }

    public EnterEventsMonitoringReport() {
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new EnterEventsMonitoringReport();
    }

    @Override
    public EnterEventsMonitoringReportBuilder createBuilder(String templateFilename) {
        return new EnterEventsMonitoringReportBuilder();
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public EnterEventsMonitoringReport setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
        return this;
    }
}
