/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.financialControlReports;

import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportForContragentJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.statistics.sfk.LatePaymentReportBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 27.08.15
 * Time: 11:13
 */

public class LatePaymentReport extends BasicReportForAllOrgJob {

    private static final Logger logger = LoggerFactory.getLogger(LatePaymentReport.class);

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public LatePaymentReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public LatePaymentReport() {
    }

    @Override
    public LatePaymentReport createInstance() {
        return new LatePaymentReport();
    }

    @Override
    public BasicReportForContragentJob.Builder createBuilder(String templateFilename) {
        return new LatePaymentReportBuilder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
