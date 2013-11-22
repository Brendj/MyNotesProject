/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.monitoring;

import ru.axetta.ecafe.processor.core.report.SyncReport;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.hibernate.Session;

public class SyncReportPage extends OnlineReportPage {
    private SyncReport syncReport;

    public String getPageFilename() {
        return "monitoring/sync_report";
    }

    public SyncReport getSyncReport() {
        return syncReport;
    }

    public void buildReport(Session session) throws Exception {
        this.syncReport = new SyncReport();
        SyncReport.Builder reportBuilder = new SyncReport.Builder();
        this.syncReport = reportBuilder.build(session, startDate, endDate, idOfOrgList);
    }
}