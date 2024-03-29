/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.financialControlReports;

import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportForContragentJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.statistics.sfk.latepayment.LatePaymentReportBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 27.08.15
 * Time: 11:13
 */

public class LatePaymentReport extends BasicReportForAllOrgJob {
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
    public static final String REPORT_NAME = "Сводный отчет по несвоевременной оплате питания";
    public static final String[] TEMPLATE_FILE_NAMES = {"LatePaymentReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{-3, -32, -50, -51};

    public static final String LATE_PAYMENT_DAYS_COUNT_TYPE = "latePaymentDaysCountType";
    public static final String LATE_PAYMENT_BY_ONE_DAY_COUNT_TYPE = "latePaymentByOneDayCountType";

    private static final Logger logger = LoggerFactory.getLogger(LatePaymentReport.class);

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public LatePaymentReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public LatePaymentReport() {
    }

    @Override
    public LatePaymentReport createInstance() {
        return new LatePaymentReport();
    }

    @Override
    public BasicReportForContragentJob.Builder createBuilder(String templateFilename) {
        return new LatePaymentReportBuilder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
