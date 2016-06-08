/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.feeding;

import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.report.BasicReportForOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.10.13
 * Time: 15:53
 * SubscriptionFeeding Jasper Report
 */
public class SubscriptionFeedingJasperReport extends BasicReportForOrgJob {
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
    public static final String REPORT_NAME = "Отчет по абонементному питанию";
    public static final String[] TEMPLATE_FILE_NAMES = {"SubscriptionFeedingJasperReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{3, 4, 5};


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
