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
 * User: i.semenov
 * Date: 28.03.16
 * Time: 19:16
 * To change this template use File | Settings | File Templates.
 */
public class ZeroTransactionsReport extends BasicReportForListOrgsJob {
    private static final Logger logger = LoggerFactory.getLogger(ZeroTransactionsReport.class);
    private String htmlReport;

    public ZeroTransactionsReport(Date generateTime, long generateDuration, JasperPrint jasperPrint,
            Date startTime, Date endTime) {
        super(generateTime, generateDuration, jasperPrint, startTime, endTime);
    }

    public ZeroTransactionsReport() {
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public ZeroTransactionsReport createInstance() {
        return new ZeroTransactionsReport();
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new BalanceLeavingReportBuilder(templateFilename);
    }

    public ZeroTransactionsReport setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
        return this;
    }

    public String getHtmlReport() {
        return htmlReport;
    }
}
