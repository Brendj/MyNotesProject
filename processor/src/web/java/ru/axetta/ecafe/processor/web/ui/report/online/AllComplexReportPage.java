/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.report.AllComplexReport;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 25.01.12
 * Time: 22:54
 * To change this template use File | Settings | File Templates.
 */
public class AllComplexReportPage extends OnlineReportPage {
    private AllComplexReport complexReport;

    public String getPageFilename() {
        return "report/online/all_complex_report";
    }

    public AllComplexReport getComplexReport() {
        return complexReport;
    }

    public void buildReport(Session session) throws Exception {
        this.complexReport = new AllComplexReport();
        AllComplexReportBuilder reportBuilder = new AllComplexReportBuilder();
        this.complexReport = reportBuilder.build(session, startDate, endDate, idOfOrgList);
    }
}
