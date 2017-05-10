/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import org.hibernate.Session;

import java.util.Date;
import java.util.List;

/**
 * Created by almaz anvarov on 04.05.2017.
 */
public class MonitoringOfReportService {

    private final Session session;


    public MonitoringOfReportService(Session session) {
        this.session = session;
    }


    public List<ReportItem> buildReportItems(Date startTime, Date endTime, List<Long> idOfOrgList) {
        return null;
    }

    public static class ReportItem {

    }

    public static class MonitoringOfItem {

    }
}
