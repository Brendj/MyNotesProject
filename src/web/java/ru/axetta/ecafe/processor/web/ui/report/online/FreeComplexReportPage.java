/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.report.FreeComplexReport;

import org.hibernate.Session;

public class FreeComplexReportPage extends OnlineReportPage {
    private FreeComplexReport complexReport;

    public String getPageFilename() {
        return "report/online/free_complex_report";
    }

    public FreeComplexReport getComplexReport() {
        return complexReport;
    }

    public void buildReport(Session session) throws Exception {
        this.complexReport = new FreeComplexReport();
        FreeComplexReport.Builder reportBuilder = new FreeComplexReport.Builder();
        this.complexReport = reportBuilder.build(session, startDate, endDate, idOfOrgList);
    }
}
