/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

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

    private DAOService daoService = DAOService.getInstance();
    private List<Item> items;
    private Date lastUpdate;

    public static class Item {

        private String orgName;
        private Date lastSuccessfulBalanceSync;
        private Date lastUnSuccessfulBalanceSync;
        private String remoteAddr;

        public Item(String orgName, Date lastSuccessfulBalanceSync, Date lastUnSuccessfulBalanceSync,
                String remoteAddr) {
            this.orgName = orgName;
            this.lastSuccessfulBalanceSync = lastSuccessfulBalanceSync;
            this.lastUnSuccessfulBalanceSync = lastUnSuccessfulBalanceSync;
            this.remoteAddr = remoteAddr;
        }

        public String getOrgName() {
            return orgName;
        }

        public Date getLastSuccessfulBalanceSync() {
            return lastSuccessfulBalanceSync;
        }

        public Date getLastUnSuccessfulBalanceSync() {
            return lastUnSuccessfulBalanceSync;
        }

        public String getRemoteAddr() {
            return remoteAddr;
        }
    }

    public SyncMonitorPage() {
    }

    public void update() {
        List<Org> orgs = daoService.getOrderedSynchOrgsList();
        LinkedList<Item> items = new LinkedList<Item>();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        for (Org org : orgs) {
            items.add(new Item(org.getShortName(), org.getLastSuccessfulBalanceSync(),
                    org.getLastUnSuccessfulBalanceSync(),
                    runtimeContext.getProcessor().getOrgSyncAddress(org.getIdOfOrg())));
        }
        this.items = items;
        if (lastUpdate == null) {
            lastUpdate = new Date();
        }
        lastUpdate.setTime(System.currentTimeMillis());
    }

    public List<Item> getItemList() {
        if (items==null) update();
        return items;
    }


    public Date getLastUpdate() {
        return lastUpdate;
    }


    public long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }


    public String getPageFilename() {
        return "report/online/sync_monitor";
    }
}