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
 * User: anvarov
 * Date: 21.01.16
 * Time: 16:30
 * To change this template use File | Settings | File Templates.
 */
public class OutOfSynchronizationReport extends BasicReportForListOrgsJob {

    /* Логгер для отчета  DetailedDeviationsWithoutCorpsNewJasperReport*/
    private static final Logger logger = LoggerFactory.getLogger(OutOfSynchronizationReport.class);

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public OutOfSynchronizationReport(Date generateTime, long generateDuration, JasperPrint jasperPrint,
            Date startTime, Date endTime) {
        super(generateTime, generateDuration, jasperPrint, startTime, endTime);
    }

    public OutOfSynchronizationReport() {
    }

    @Override
    public OutOfSynchronizationReport createInstance() {
        return new OutOfSynchronizationReport();
    }

    @Override
    public OutOfSynchronizationReport.Builder createBuilder(String templateFilename) {
        return new OutOfSynchronizationReportBuilder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
