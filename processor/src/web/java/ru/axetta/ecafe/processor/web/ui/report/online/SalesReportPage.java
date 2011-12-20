/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.report.SalesReport;

import org.hibernate.classic.Session;

public class SalesReportPage extends OnlineReportPage {
    private SalesReport salesReport;

    public String getPageFilename() {
        return "report/online/sales_report";
    }

    public SalesReport getSalesReport() {
        return salesReport;
    }

    public void buildReport(Session session) throws Exception {
        this.salesReport = new SalesReport();
        SalesReport.Builder reportBuilder = new SalesReport.Builder();
        this.salesReport = reportBuilder.build(session, startDate, endDate, idOfOrgList);
    }
}