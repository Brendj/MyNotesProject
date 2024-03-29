/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.payment.preferential.supply;

import ru.axetta.ecafe.processor.core.report.BasicReport;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 20.01.14
 * Time: 13:37
 * To change this template use File | Settings | File Templates.
 */
public class StatisticsPaymentPreferentialSupplyReport extends BasicReport {
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
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};


    private final List<StatisticsPaymentPreferentialSupplyItem> statisticsPaymentPreferentialSupplyItems;

    public StatisticsPaymentPreferentialSupplyReport() {
        super();
        this.statisticsPaymentPreferentialSupplyItems = Collections.emptyList();
    }

    public StatisticsPaymentPreferentialSupplyReport(Date generateTime, long generateDuration,
            List<StatisticsPaymentPreferentialSupplyItem> statisticsPaymentPreferentialSupplyItems) {
        super(generateTime, generateDuration);
        this.statisticsPaymentPreferentialSupplyItems = statisticsPaymentPreferentialSupplyItems;
    }


    public List<StatisticsPaymentPreferentialSupplyItem> getStatisticsPaymentPreferentialSupplyItems() {
        return statisticsPaymentPreferentialSupplyItems;
    }
}
