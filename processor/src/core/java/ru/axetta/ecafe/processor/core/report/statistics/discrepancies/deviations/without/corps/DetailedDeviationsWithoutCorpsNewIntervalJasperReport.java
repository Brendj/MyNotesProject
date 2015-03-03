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
 * Date: 03.03.15
 * Time: 12:10
 */

public class DetailedDeviationsWithoutCorpsNewIntervalJasperReport extends BasicReportForAllOrgJob {

    /* Логгер для отчета DetailedDeviationsWithoutCorpsNewIntervalJasperReport*/
    private static final Logger logger = LoggerFactory
            .getLogger(DetailedDeviationsWithoutCorpsNewIntervalJasperReport.class);

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public DetailedDeviationsWithoutCorpsNewIntervalJasperReport(Date generateTime, long generateDuration,
            JasperPrint print, Date startTime, Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public DetailedDeviationsWithoutCorpsNewIntervalJasperReport() {
    }

    @Override
    public DetailedDeviationsWithoutCorpsNewIntervalJasperReport createInstance() {
        return new DetailedDeviationsWithoutCorpsNewIntervalJasperReport();
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new DetailedDeviationsWithoutCorpsNewBuilder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}