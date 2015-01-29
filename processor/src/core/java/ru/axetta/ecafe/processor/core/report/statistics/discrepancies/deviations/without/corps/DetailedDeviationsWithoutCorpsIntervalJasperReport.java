/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.without.corps;

import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 29.01.15
 * Time: 14:49
 */

public class DetailedDeviationsWithoutCorpsIntervalJasperReport extends BasicReportForAllOrgJob {

    /* Логгер для отчета DetailedDeviationsWithoutCorpsIntervalJasperReport*/
    private static final Logger logger = LoggerFactory
            .getLogger(DetailedDeviationsWithoutCorpsIntervalJasperReport.class);

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public DetailedDeviationsWithoutCorpsIntervalJasperReport(Date generateTime, long generateDuration,
            JasperPrint print, Date startTime, Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public DetailedDeviationsWithoutCorpsIntervalJasperReport() {
    }

    @Override
    public DetailedDeviationsWithoutCorpsIntervalJasperReport createInstance() {
        return new DetailedDeviationsWithoutCorpsIntervalJasperReport();
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new DetailedDeviationsWithoutCorpsBuilder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
