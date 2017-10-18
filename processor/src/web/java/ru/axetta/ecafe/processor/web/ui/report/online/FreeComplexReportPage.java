/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.report.AllComplexReport;
import ru.axetta.ecafe.processor.core.report.FreeComplexReport;

import org.hibernate.Session;

public class FreeComplexReportPage extends OnlineReportPage {
    private AllComplexReport complexReport;

    public String getPageFilename() {
        return "report/online/free_complex_report";
    }

    public AllComplexReport getComplexReport() {
        return complexReport;
    }

    public void buildReport(Session session) throws Exception {
        this.complexReport = new FreeComplexReport();
        FreeComplexReportBuilder reportBuilder = new FreeComplexReportBuilder();
        this.complexReport = reportBuilder.build(session, startDate, endDate, idOfOrgList);
    }
}
