/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JasperPrint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 11.04.13
 * Time: 12:08
 * To change this template use File | Settings | File Templates.
 */
public class ClientPaymentsReport extends BasicReportForAllOrgJob {
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
    public static final String REPORT_NAME = "Отчет по начислениям";
    public static final String[] TEMPLATE_FILE_NAMES = {"ClientPaymentsReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};


    /* Логгер для отчета  ClientPaymentsReport*/
    private static final Logger logger = LoggerFactory.getLogger(ClientPaymentsReport.class);

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public ClientPaymentsReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public ClientPaymentsReport() {
    }

    @Override
    public ClientPaymentsReport createInstance() {
        return new ClientPaymentsReport();
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new ClientPaymentsBuilder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
