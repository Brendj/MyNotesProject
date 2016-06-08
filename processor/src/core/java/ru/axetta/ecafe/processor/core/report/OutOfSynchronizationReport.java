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
    /*
    * Параметры отчета для добавления в правила и шаблоны
    *
    * При создании любого отчета необходимо добавить параметры:
    * REPORT_NAME - название отчета на русском
    * TEMPLATE_FILE_NAMES - названия всех jasper-файлов, созданных для отчета
    * IS_TEMPLATE_REPORT - добавлять ли отчет в шаблоны отчетов
    * PARAM_HINTS - параметры отчета (смотри ReportRuleConstants.PARAM_HINTS)
    * заполняется, если отчет добавлен в шаблоны (класс AutoReportGenerator)
    *
    * Затем КАЖДЫЙ класс отчета добавляется в массив ReportRuleConstants.ALL_REPORT_CLASSES
    */
    public static final String REPORT_NAME = "Отчет по отсутствию быстрой синхронизации в ОО";
    public static final String[] TEMPLATE_FILE_NAMES = {"OutOfSynchronizationReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{-3};


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
