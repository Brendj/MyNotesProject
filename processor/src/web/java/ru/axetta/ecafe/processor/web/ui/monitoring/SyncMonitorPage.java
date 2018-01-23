/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.monitoring;

import ru.axetta.ecafe.processor.dashboard.DashboardServiceBean;
import ru.axetta.ecafe.processor.dashboard.data.DashboardResponse;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 19.09.12
 * Time: 18:09
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope(value = "session")
public class SyncMonitorPage extends BasicWorkspacePage {

    @Autowired
    DashboardServiceBean dashboardServiceBean;
    
    private List<DashboardResponse.OrgSyncStatItem> items;
    private Date lastUpdate;
    private List<DashboardResponse.PaymentSystemStatItem> payStatItems;
    private List<DashboardResponse.NamedParams> namedParams;
    private List<DashboardResponse.MenuLastLoadItem> lastLoadItems;
    

    public SyncMonitorPage() {
    }

    public Object update() {
        this.payStatItems = dashboardServiceBean.getPaymentSystemInfo(new Date()).getPaymentSystemItemInfos();
        this.items = dashboardServiceBean.getOrgSyncInfo().getOrgSyncStatItems();
        this.namedParams = dashboardServiceBean.getNamedParams();
        this.lastLoadItems = dashboardServiceBean.getMenuLastLoad();
        if (lastUpdate == null) {
            lastUpdate = new Date();
        }
        lastUpdate.setTime(System.currentTimeMillis());
        return null;
    }

    public List<DashboardResponse.OrgSyncStatItem> getItemList() {
        if (items==null) update();
        return items;
    }

    public List<DashboardResponse.PaymentSystemStatItem> getPayStatItems() {
        return payStatItems;
    }

    public List<DashboardResponse.NamedParams> getNamedParams() {
        return namedParams;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public List<DashboardResponse.MenuLastLoadItem> getLastLoadItems() {
        return lastLoadItems;
    }

    public long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }


    public String getPageFilename() {
        return "monitoring/sync_monitor";
    }
}