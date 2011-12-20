/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.report.ClientReport;

import org.hibernate.classic.Session;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 20.10.11
 * Time: 12:01
 * To change this template use File | Settings | File Templates.
 */
public class ClientReportPage extends OnlineReportPage {
    private ClientReport clientReport;

    public String getPageFilename() {
        return "report/online/client_report";
    }

    public ClientReport getClientReport() {
        return clientReport;
    }

    public void buildReport(Session session) throws Exception {
        this.clientReport = new ClientReport();
        ClientReport.Builder reportBuilder = new ClientReport.Builder();
        this.clientReport = reportBuilder.build(session);
    }
}
