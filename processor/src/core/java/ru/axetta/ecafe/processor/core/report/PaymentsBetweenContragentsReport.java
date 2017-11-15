/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JasperPrint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by i.semenov on 14.11.2017.
 */
public class PaymentsBetweenContragentsReport extends BasicReportForListOrgsJob {
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
    public static final String REPORT_NAME = "Отчет по объемам оплат между поставщиками питания";
    public static final String[] TEMPLATE_FILE_NAMES = {"PaymentsBetweenContragentsReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};

    private static final Logger logger = LoggerFactory.getLogger(PaymentsBetweenContragentsReport.class);
    private String htmlReport;

    public PaymentsBetweenContragentsReport(Date generateTime, long generateDuration, JasperPrint jasperPrint,
            Date startTime, Date endTime) {
        super(generateTime, generateDuration, jasperPrint, startTime, endTime);
    }

    public PaymentsBetweenContragentsReport() {}

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public PaymentsBetweenContragentsReport createInstance() {
        return new PaymentsBetweenContragentsReport();
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new PaymentsBetweenContragentsReportBuilder(templateFilename);
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public PaymentsBetweenContragentsReport setHtmlReport(String htmlReport) {
        if (htmlReport.length() < 500) {
            this.htmlReport = "Нет данных по выбранному условию";
        } else {
            this.htmlReport = htmlReport;
        }
        return this;
    }
}
