/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.report.OrgDiscountsReport;

import org.hibernate.Session;

import static ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage.CompleteHandler;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 11.02.12
 */
public class OrgDiscountsReportPage extends OnlineReportPage implements CompleteHandler {

    private OrgDiscountsReport orgDiscountsReport;

    public String getPageFilename() {
        return "report/online/org_discounts_report";
    }

    public OrgDiscountsReport getOrgDiscountsReport() {
        return orgDiscountsReport;
    }

    public void buildReport(Session session) throws Exception {
        OrgDiscountsReport.Builder builder = new OrgDiscountsReport.Builder();
        orgDiscountsReport = builder.build(session, idOfOrg);
    }
}
