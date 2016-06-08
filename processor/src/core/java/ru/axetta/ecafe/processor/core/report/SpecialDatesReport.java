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
 * User: Liya
 * Date: 17.04.16
 * Time: 11:15
 */
public class SpecialDatesReport extends BasicReportForListOrgsJob {
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
    public static final String REPORT_NAME = "Отчет по учебным дням";
    public static final String[] TEMPLATE_FILE_NAMES = {"SpecialDatesReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};

    private static final Logger logger = LoggerFactory.getLogger(SpecialDatesReport.class);
    private String htmlReport;

    public SpecialDatesReport(Date generateTime, long generateDuration, JasperPrint jasperPrint,
            Date startTime, Date endTime) {
        super(generateTime, generateDuration, jasperPrint, startTime, endTime);
    }

    public SpecialDatesReport() {
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new SpecialDatesReport();
    }

    @Override
    public SpecialDatesReportBuilder createBuilder(String templateFilename) {
        return new SpecialDatesReportBuilder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public SpecialDatesReport setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
        return this;
    }
}
