/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.feeding;

import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.report.BasicReport;
import ru.axetta.ecafe.processor.core.report.BasicReportForOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.10.13
 * Time: 15:53
 * SubscriptionFeeding Jasper Report
 */
public class SubscriptionFeedingJasperReport extends BasicReportForOrgJob {

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {}

    public SubscriptionFeedingJasperReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime,idOfOrg);
    }

    public SubscriptionFeedingJasperReport() {}

    @Override
    public BasicReportForOrgJob createInstance() {
        return new SubscriptionFeedingJasperReport();
    }

    @Override
    public BasicReportJob.Builder createBuilder(String templateFilename) {
        return new SubscriptionFeedingReportBuilder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_PREV_PREV_PREV_DAY;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionFeedingJasperReport.class);
}
