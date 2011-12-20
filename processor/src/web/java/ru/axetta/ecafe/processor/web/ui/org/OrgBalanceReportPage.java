/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.report.OrgBalanceReport;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.classic.Session;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class OrgBalanceReportPage extends BasicWorkspacePage {

    private String shortName;
    private Date baseDate;
    private OrgBalanceReport orgBalanceReport;

    public String getPageFilename() {
        return "org/balance_report";
    }

    public String getShortName() {
        return shortName;
    }

    public Date getBaseDate() {
        return baseDate;
    }

    public void setBaseDate(Date baseDate) {
        this.baseDate = baseDate;
    }

    public OrgBalanceReport getOrgBalanceReport() {
        return orgBalanceReport;
    }

    public OrgBalanceReportPage() {
        this.shortName = null;
        this.baseDate = DateUtils.addDays(new Date(), 1);
        this.orgBalanceReport = new OrgBalanceReport(this.baseDate);
    }

    public void fill(Session session, Long idOfOrg) throws Exception {
        Org org = (Org) session.load(Org.class, idOfOrg);
        fill(org);
    }

    public void buildReport(Session session, Long idOfOrg) throws Exception {
        Org org = (Org) session.load(Org.class, idOfOrg);
        this.orgBalanceReport = new OrgBalanceReport(this.baseDate);
        OrgBalanceReport.Builder orgBalanceReportBuilder = new OrgBalanceReport.Builder();
        this.orgBalanceReport = orgBalanceReportBuilder.build(session, this.baseDate, org);
        fill(org);
    }

    private void fill(Org org) throws Exception {
        this.shortName = org.getShortName();
    }
}