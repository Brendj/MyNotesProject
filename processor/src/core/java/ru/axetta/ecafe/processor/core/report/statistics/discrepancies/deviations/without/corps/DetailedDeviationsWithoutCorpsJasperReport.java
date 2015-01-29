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
 * User: Алмаз
 * Date: 27.01.15
 * Time: 19:13
 * To change this template use File | Settings | File Templates.
 */
public class DetailedDeviationsWithoutCorpsJasperReport extends BasicReportForAllOrgJob {

    /* Логгер для отчета  DetailedDeviationsWithoutCorpsJasperReport*/
    private static final Logger logger = LoggerFactory.getLogger(DetailedDeviationsWithoutCorpsJasperReport.class);

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public DetailedDeviationsWithoutCorpsJasperReport(Date generateTime, long generateDuration, JasperPrint print,
            Date startTime, Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public DetailedDeviationsWithoutCorpsJasperReport() {
    }

    @Override
    public DetailedDeviationsWithoutCorpsJasperReport createInstance() {
        return new DetailedDeviationsWithoutCorpsJasperReport();
    }

    @Override
    public BasicReportForContragentJob.Builder createBuilder(String templateFilename) {
        return new DetailedDeviationsWithoutCorpsBuilder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
