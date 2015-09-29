/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.financialControlReports;

import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.statistics.sfk.adjustmentpayment.AdjustmentPaymentReportBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 28.09.15
 * Time: 11:47
 */

public class AdjustmentPaymentReport extends BasicReportForAllOrgJob {

    private static final Logger logger = LoggerFactory.getLogger(AdjustmentPaymentReport.class);

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public AdjustmentPaymentReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public AdjustmentPaymentReport() {
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new AdjustmentPaymentReport();
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new AdjustmentPaymentReportBuilder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
