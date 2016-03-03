/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discounts;

import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 24.02.16
 * Time: 17:36
 */
public class OrgDiscountsReport extends BasicReportForAllOrgJob {

    private static final Logger logger = LoggerFactory.getLogger(OrgDiscountsReport.class);

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public OrgDiscountsReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public OrgDiscountsReport() {
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new OrgDiscountsReport();
    }

    @Override
    public OrgDiscountsBuilder createBuilder(String templateFilename) {
        return new OrgDiscountsBuilder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
