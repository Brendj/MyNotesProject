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
