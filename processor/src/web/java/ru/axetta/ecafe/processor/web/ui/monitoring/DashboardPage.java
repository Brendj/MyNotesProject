/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.monitoring;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.dashboard.DashboardServiceBean;
import ru.axetta.ecafe.processor.dashboard.data.DashboardResponse;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Scope(value = "session")
public class DashboardPage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler {

    @Autowired
    DashboardServiceBean dashboardServiceBean;

    DashboardResponse.PaymentSystemStats psStatus;
    DashboardResponse orgStatus = new DashboardResponse();
    DashboardResponse.OrgBasicStats orgBasicStats;

    Date reportDate = new Date();
    Org filterOrg;


    @Override
    public String getPageFilename() {
        return "monitoring/dashboard";
    }

    @Override
    public void onShow() throws Exception {
    }

    public void updateOrgStatus() {
        try {
            orgStatus = dashboardServiceBean.getOrgInfo(new DashboardResponse(), reportDate, filterOrg==null?null:filterOrg.getIdOfOrg());
        } catch (Exception e) {
            logAndPrintMessage("Ошибка подготовки данных", e);
        }
    }
    public void updateOrgBasicStats() {
        try {
            orgBasicStats = dashboardServiceBean.getOrgBasicStats(reportDate, filterOrg==null?null:filterOrg.getIdOfOrg());
        } catch (Exception e) {
            logAndPrintMessage("Ошибка подготовки данных", e);
        }
    }
    public void updatePaySysStatus() {
        try {
            psStatus = dashboardServiceBean.getPaymentSystemInfo(reportDate);
        } catch (Exception e) {
            logAndPrintMessage("Ошибка подготовки данных", e);
        }
    }

    public DashboardResponse.PaymentSystemStats getPsStatus() {
        return psStatus;
    }

    public DashboardResponse getOrgStatus() {
        return orgStatus;
    }

    public DashboardResponse.OrgBasicStats getOrgBasicStats() {
        return orgBasicStats;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public Org getFilterOrg() {
        return filterOrg;
    }

    public void setFilterOrg(Org filterOrg) {
        this.filterOrg = filterOrg;
    }
    
    public String getFilterOrgName() {
        return filterOrg==null?"":filterOrg.getShortName();
    }

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (idOfOrg==null) filterOrg = null;
        else filterOrg = (Org)session.get(Org.class, idOfOrg);
    }
}
