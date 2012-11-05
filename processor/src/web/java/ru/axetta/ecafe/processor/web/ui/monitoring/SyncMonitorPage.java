/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.monitoring;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.dashboard.DashboardServiceBean;
import ru.axetta.ecafe.processor.dashboard.data.DashboardResponse;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
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
    

    public SyncMonitorPage() {
    }

    public void update() {
        this.payStatItems = dashboardServiceBean.getPaymentSystemInfo(new Date()).getPaymentSystemItemInfos();
        this.items = dashboardServiceBean.getOrgSyncInfo().getOrgSyncStatItems();
        if (lastUpdate == null) {
            lastUpdate = new Date();
        }
        lastUpdate.setTime(System.currentTimeMillis());
    }

    public List<DashboardResponse.OrgSyncStatItem> getItemList() {
        if (items==null) update();
        return items;
    }

    public List<DashboardResponse.PaymentSystemStatItem> getPayStatItems() {
        return payStatItems;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }


    public long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }


    public String getPageFilename() {
        return "monitoring/sync_monitor";
    }
}