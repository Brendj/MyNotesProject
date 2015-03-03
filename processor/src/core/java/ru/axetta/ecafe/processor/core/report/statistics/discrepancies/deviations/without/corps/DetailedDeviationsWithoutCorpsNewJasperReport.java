/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.without.corps;

import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportForContragentJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 03.03.15
 * Time: 12:07
 */

public class DetailedDeviationsWithoutCorpsNewJasperReport extends BasicReportForAllOrgJob {

    /* Логгер для отчета  DetailedDeviationsWithoutCorpsNewJasperReport*/
    private static final Logger logger = LoggerFactory.getLogger(DetailedDeviationsWithoutCorpsNewJasperReport.class);

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public DetailedDeviationsWithoutCorpsNewJasperReport(Date generateTime, long generateDuration, JasperPrint print,
            Date startTime, Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public DetailedDeviationsWithoutCorpsNewJasperReport() {
    }

    @Override
    public DetailedDeviationsWithoutCorpsNewJasperReport createInstance() {
        return new DetailedDeviationsWithoutCorpsNewJasperReport();
    }

    @Override
    public BasicReportForContragentJob.Builder createBuilder(String templateFilename) {
        return new DetailedDeviationsWithoutCorpsNewBuilder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}