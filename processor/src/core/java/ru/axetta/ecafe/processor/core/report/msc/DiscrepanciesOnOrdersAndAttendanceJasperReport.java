/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.msc;

import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.events.orders.DiscrepanciesOnOrdersAndAttendanceBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 04.02.14
 * Time: 11:55
 * To change this template use File | Settings | File Templates.
 */
public class DiscrepanciesOnOrdersAndAttendanceJasperReport extends BasicReportForAllOrgJob {
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
    public static final String REPORT_NAME = "Статистика расхождения данных по заказам и оплате";
    public static final String[] TEMPLATE_FILE_NAMES = {"DiscrepanciesOnOrdersAndAttendanceJasperReport.jasper",
                                                        "DiscrepanciesOnOrdersAndAttendanceJasperReport_summary.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};


    private final static Logger logger = LoggerFactory.getLogger(DiscrepanciesOnOrdersAndAttendanceJasperReport.class);

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {}

    public DiscrepanciesOnOrdersAndAttendanceJasperReport() {
    }


    public DiscrepanciesOnOrdersAndAttendanceJasperReport(Date generateTime, long generateDuration, JasperPrint print,
            Date startTime, Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new DiscrepanciesOnOrdersAndAttendanceJasperReport();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BasicReportForAllOrgJob.Builder createBuilder(String templateFilename) {
        return new DiscrepanciesOnOrdersAndAttendanceBuilder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_PREV_MONTH;
    }

}
