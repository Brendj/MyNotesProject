/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.msc;

import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.report.BasicReportForContragentJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.statistics.payment.preferential.supply.StatisticsPaymentPreferentialSupplyBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 20.01.14
 * Time: 11:56
 * To change this template use File | Settings | File Templates.
 */
public class StatisticsPaymentPreferentialSupplyJasperReport extends BasicReportForContragentJob {
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
    public static final String REPORT_NAME = "Статистика оплаты льготного питания";
    public static final String[] TEMPLATE_FILE_NAMES = {"StatisticsPaymentPreferentialSupplyJasperReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{3, -20};


    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {}

    public StatisticsPaymentPreferentialSupplyJasperReport(Date generateTime, long generateDuration, JasperPrint print,
            Date startTime, Date endTime, Long idOfContragent) {
        super(generateTime, generateDuration, print, startTime, endTime, idOfContragent);
    }

    @Override
    protected Integer getContragentSelectClass() {
        return Contragent.TSP;
    }

    public StatisticsPaymentPreferentialSupplyJasperReport() {}

    public StatisticsPaymentPreferentialSupplyJasperReport createInstance() {
        return new StatisticsPaymentPreferentialSupplyJasperReport();
    }

    @Override
    public BasicReportForContragentJob.Builder createBuilder(String templateFilename) {
        return new StatisticsPaymentPreferentialSupplyBuilder(templateFilename);
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    public int getDefaultReportPeriod (){
        return REPORT_PERIOD_LAST_WEEK;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsPaymentPreferentialSupplyJasperReport.class);
}
