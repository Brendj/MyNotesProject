/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.report.PayComplexReport;

import org.hibernate.classic.Session;

public class PayComplexReportPage extends OnlineReportPage {
    private PayComplexReport complexReport;

    public String getPageFilename() {
        return "report/online/pay_complex_report";
    }

    public PayComplexReport getComplexReport() {
        return complexReport;
    }

    public void buildReport(Session session) throws Exception {
        this.complexReport = new PayComplexReport();
        PayComplexReport.Builder reportBuilder = new PayComplexReport.Builder();
        this.complexReport = reportBuilder.build(session, startDate, endDate, idOfOrgList);
    }
}
