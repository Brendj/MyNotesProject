/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.report.SyncReport;

import org.hibernate.classic.Session;

public class SyncReportPage extends OnlineReportPage {
    private SyncReport syncReport;

    public String getPageFilename() {
        return "report/online/sync_report";
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