/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.report.ClientPaymentsReport;

import org.hibernate.Session;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 10.04.13
 * Time: 13:37
 * To change this template use File | Settings | File Templates.
 */
public class ClientPaymentsPage extends OnlineReportPage {
    private ClientPaymentsReport clientPaymentsReport;

    public String getPageFilename() {
        return "report/online/client_payments_report";
    }

    public ClientPaymentsReport getClientPaymentsReport() {
        return clientPaymentsReport;
    }

    public void buildReport(Session session) throws Exception {
        this.clientPaymentsReport = new ClientPaymentsReport();
        ClientPaymentsReport.Builder reportBuilder = new ClientPaymentsReport.Builder();
        this.clientPaymentsReport = reportBuilder.build(session, startDate, endDate, idOfOrgList);
    }

}
