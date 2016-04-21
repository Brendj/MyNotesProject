/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JasperPrint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 17.04.16
 * Time: 11:15
 */
public class SpecialDatesReport extends BasicReportForListOrgsJob {
    private static final Logger logger = LoggerFactory.getLogger(SpecialDatesReport.class);
    private String htmlReport;

    public SpecialDatesReport(Date generateTime, long generateDuration, JasperPrint jasperPrint,
            Date startTime, Date endTime) {
        super(generateTime, generateDuration, jasperPrint, startTime, endTime);
    }

    public SpecialDatesReport() {
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new SpecialDatesReport();
    }

    @Override
    public SpecialDatesReportBuilder createBuilder(String templateFilename) {
        return new SpecialDatesReportBuilder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public SpecialDatesReport setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
        return this;
    }
}
