/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.report.EnterEventReport;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 20.12.11
 * Time: 12:56
 * To change this template use File | Settings | File Templates.
 */
public class EnterEventReportPage extends OnlineReportPage {

    private EnterEventReport enterEventReport;

    public String getPageFilename() {
        return "report/online/enter_event_report";
    }

    public EnterEventReport getEnterEventReport() {
        return enterEventReport;
    }

    public void buildReport(Session session) throws Exception {
        this.enterEventReport = new EnterEventReport();
        EnterEventReport.Builder reportBuilder = new EnterEventReport.Builder();
        this.enterEventReport = reportBuilder.build(session, startDate, endDate, idOfOrgList);
    }

}
