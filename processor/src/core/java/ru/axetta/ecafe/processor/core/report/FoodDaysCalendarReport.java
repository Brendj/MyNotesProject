/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JasperPrint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class FoodDaysCalendarReport extends BasicReportForAllOrgJob {

    public static final String REPORT_NAME = "Журнал ведения календаря дней питания";
    public static final String[] TEMPLATE_FILE_NAMES = {"FoodDaysCalendarReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};

    private static final Logger logger = LoggerFactory.getLogger(FoodDaysCalendarReport.class);
    private String htmlReport;

    public FoodDaysCalendarReport(Date generateTime, long generateDuration, JasperPrint jasperPrint,
            Date startTime, Date endTime) {
        super(generateTime, generateDuration, jasperPrint, startTime, endTime);
    }

    public FoodDaysCalendarReport() {
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new FoodDaysCalendarReport();
    }

    @Override
    public FoodDaysCalendarReportBuilder createBuilder(String templateFilename) {
        return new FoodDaysCalendarReportBuilder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public FoodDaysCalendarReport setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
        return this;
    }
}
