/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.monitoring;

import ru.axetta.ecafe.processor.core.report.StatusSyncReport;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 07.10.11
 * Time: 16:10
 * To change this template use File | Settings | File Templates.
 */
public class StatusSyncReportPage extends OnlineReportPage {
    private StatusSyncReport statusSyncReport;

    public String getPageFilename() {
        return "monitoring/status_sync_report";
    }

    public StatusSyncReport getStatusSyncReport() {
        return statusSyncReport;
    }

    public void buildReport(Session session) throws Exception {
        this.statusSyncReport = new StatusSyncReport();
        StatusSyncReport.Builder reportBuilder = new StatusSyncReport.Builder();
        this.statusSyncReport = reportBuilder.build(session);
    }

}
