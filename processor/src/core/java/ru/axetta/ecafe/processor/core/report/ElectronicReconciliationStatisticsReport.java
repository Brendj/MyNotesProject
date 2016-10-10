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
 * Date: 10.10.16
 * Time: 11:44
 */

public class ElectronicReconciliationStatisticsReport extends BasicReportForContragentJob {
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
    public static final String REPORT_NAME = "Статистика электронной сверки";
    public static final String[] TEMPLATE_FILE_NAMES = {"ElectronicReconciliationStatisticsReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{};

    final private static Logger logger = LoggerFactory.getLogger(ElectronicReconciliationStatisticsReport.class);

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public ElectronicReconciliationStatisticsReport(Date generateTime, long generateDuration, JasperPrint print,
            Date startTime, Date endTime, Long idOfContragent) {
        super(generateTime, generateDuration, print, startTime, endTime, idOfContragent);
    }

    public ElectronicReconciliationStatisticsReport() {
    }

    @Override
    public BasicReportForContragentJob createInstance() {
        return new ElectronicReconciliationStatisticsReport();
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new ElectronicReconciliationStatisticsBuilder(templateFilename);
    }
}
