/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.payment.preferential.supply;

import ru.axetta.ecafe.processor.core.report.BasicReport;
import ru.axetta.ecafe.processor.core.report.feeding.SubscriptionFeedingReportItem;

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
