/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JasperPrint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 28.03.16
 * Time: 19:16
 * To change this template use File | Settings | File Templates.
 */
public class BasicBasketReport extends BasicReportForListOrgsJob {
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
    public static final String REPORT_NAME = "Учёт причин снижения объемов оказания услуг";
    public static final String[] TEMPLATE_FILE_NAMES = {"BasicBasketReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};

    private static final Logger logger = LoggerFactory.getLogger(BasicBasketReport.class);
    private String htmlReport;

    public BasicBasketReport(Date generateTime, long generateDuration, JasperPrint jasperPrint,
            Date startTime, Date endTime) {
        super(generateTime, generateDuration, jasperPrint, startTime, endTime);
    }

    public BasicBasketReport() {
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public BasicBasketReport createInstance() {
        return new BasicBasketReport();
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new BasicBasketReportBuilder(templateFilename);
    }

    public BasicBasketReport setHtmlReport(String htmlReport) {
        if (htmlReport.length() < 500) {
            this.htmlReport = "Нет данных по выбранному условию";
        } else {
            this.htmlReport = htmlReport;
        }
        return this;
    }

    public String getHtmlReport() {
        return htmlReport;
    }
}
