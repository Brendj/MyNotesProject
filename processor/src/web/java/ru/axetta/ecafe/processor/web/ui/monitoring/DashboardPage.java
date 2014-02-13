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

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

@Component
@Scope(value = "session")
public class DashboardPage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler {

    @Autowired
    DashboardServiceBean dashboardServiceBean;

    DashboardResponse.PaymentSystemStats psStatus;
    DashboardResponse response = new DashboardResponse();
    DashboardResponse.OrgBasicStats orgBasicStats;

    Date reportDate = new Date();
    Org filterOrg;
    private int orgStatus = 1;


    @Override
    public String getPageFilename() {
        return "monitoring/dashboard";
    }

    @Override
    public void onShow() throws Exception {
    }

    public void updateOrgStatus() {
        try {
            response = dashboardServiceBean.getOrgInfo(new DashboardResponse(), reportDate, filterOrg==null?null:filterOrg.getIdOfOrg());
        } catch (Exception e) {
            logAndPrintMessage("Ошибка подготовки данных", e);
        }
    }

    public Object updateOrgBasicStats() {
        try {
            orgBasicStats = dashboardServiceBean
                    .getOrgBasicStats(reportDate, filterOrg == null ? null : filterOrg.getIdOfOrg(), orgStatus);
        } catch (Exception e) {
            logAndPrintMessage("Ошибка подготовки данных", e);
        }
        return null;
    }

    public String showOrgBasicStatsCSVList() {
        try {
            orgBasicStats = dashboardServiceBean
                    .getOrgBasicStats(reportDate, filterOrg == null ? null : filterOrg.getIdOfOrg(), orgStatus);
        } catch (Exception e) {
            logAndPrintMessage("Ошибка подготовки данных", e);
        }
        return "showOrgBasicStatsCSVList";
    }


    public Object updatePaySysStatus() {
        try {
            psStatus = dashboardServiceBean.getPaymentSystemInfo(reportDate);
        } catch (Exception e) {
            logAndPrintMessage("Ошибка подготовки данных", e);
        }
        return null;
    }

    public DashboardResponse.PaymentSystemStats getPsStatus() {
        return psStatus;
    }

    public int getOrgStatus() {
        return orgStatus;
    }

    public void setOrgStatus(int orgStatus) {
        this.orgStatus = orgStatus;
    }

    public DashboardResponse getResponse() {
        return response;
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
