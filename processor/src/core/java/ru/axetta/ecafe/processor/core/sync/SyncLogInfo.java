/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync;

/**
 * Created by i.semenov on 10.09.2021.
 */
public class SyncLogInfo {
    private final long idOfOrg;
    private final String idOfSync;
    private long syncTime;

    public SyncLogInfo(long idOfOrg, String idOfSync) {
        this.idOfOrg = idOfOrg;
        this.idOfSync = idOfSync;
        this.syncTime = System.currentTimeMillis();
    }

    public long getIdOfOrg() {
        return idOfOrg;
    }

    public String getIdOfSync() {
        return idOfSync;
    }

    public long getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(long syncTime) {
        this.syncTime = syncTime;
    }
}
