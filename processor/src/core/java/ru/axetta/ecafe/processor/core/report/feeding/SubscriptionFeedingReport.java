/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.feeding;

import ru.axetta.ecafe.processor.core.report.BasicReport;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.10.13
 * Time: 15:53
 * To change this template use File | Settings | File Templates.
 */
public class SubscriptionFeedingReport extends BasicReport {
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
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};


    private final List<SubscriptionFeedingReportItem> subscriptionFeedingReportItems;

    public SubscriptionFeedingReport() {
        super();
        this.subscriptionFeedingReportItems = Collections.emptyList();
    }

    public SubscriptionFeedingReport(Date generateTime, long generateDuration, List<SubscriptionFeedingReportItem> subscriptionFeedingReportItems) {
        super(generateTime, generateDuration);
        this.subscriptionFeedingReportItems = subscriptionFeedingReportItems;
    }

    public List<SubscriptionFeedingReportItem> getSubscriptionFeedingReportItems() {
        return subscriptionFeedingReportItems;
    }
}
