/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import ru.axetta.ecafe.processor.core.sync.SyncType;

import org.slf4j.LoggerFactory;

import java.util.*;

public class SyncCollector {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SyncStatsManager.class);
    private static Date startTime = new Date();
    private static List<SyncData> syncList = new ArrayList<SyncData>();
    private static Map<Long, SyncData> tempSyncs = new HashMap<Long, SyncData>();


    private SyncCollector() {
    }

    public static SyncCollector getInstance() {
        return SyncCollectorHolder.INSTANCE;
    }

    private void clearOldData() {
        if (startTime.getTime() < new Date().getTime() - 7200000L) {
            startTime = new Date();
            syncList = new ArrayList<SyncData>();
            tempSyncs = new HashMap<Long, SyncData>();
        }
    }

    public void registerSyncStart(Long syncTime) {
        clearOldData();
        tempSyncs.put(syncTime, new SyncData());
        tempSyncs.get(syncTime).setSyncStartTime(new Date());
    }

    public void registerSyncEnd(Long syncTime) {
        clearOldData();
        if (tempSyncs.containsKey(syncTime)) {
            tempSyncs.get(syncTime).setSyncEndTime(new Date());
            syncList.add(tempSyncs.get(syncTime));
            tempSyncs.remove(syncTime);
        }
    }

    public void setIdOfOrg(Long syncTime, long idOfOrg) {
        clearOldData();
        if (tempSyncs.containsKey(syncTime)) {
            tempSyncs.get(syncTime).setIdOfOrg(idOfOrg);
        }
    }

    public void setIdOfSync(Long syncTime, String idOfSync) {
        clearOldData();
        if (tempSyncs.containsKey(syncTime)) {
            tempSyncs.get(syncTime).setIdOfSync(idOfSync);
        }
    }

    public void setSyncType(Long syncTime, SyncType syncType) {
        clearOldData();
        if (tempSyncs.containsKey(syncTime)) {
            tempSyncs.get(syncTime).setSyncType(syncType);
        }
    }

    public void setErrMessage(Long syncTime, String errMessage) {
        clearOldData();
        if (tempSyncs.containsKey(syncTime)) {
            tempSyncs.get(syncTime).setErrorMessage(errMessage);
        }
    }

    public static Date getStartTime() {
        return startTime;
    }

    public static void setStartTime(Date startTime) {
        SyncCollector.startTime = startTime;
    }

    public static List<SyncData> getSyncList() {
        return syncList;
    }

    public static void setSyncList(List<SyncData> syncList) {
        SyncCollector.syncList = syncList;
    }

    public static Map<Long, SyncData> getTempSyncs() {
        return tempSyncs;
    }

    public static void setTempSyncs(Map<Long, SyncData> tempSyncs) {
        SyncCollector.tempSyncs = tempSyncs;
    }

    private static class SyncCollectorHolder {

        private static final SyncCollector INSTANCE = new SyncCollector();
    }

    protected class SyncData {

        private String idOfSync;
        private long idOfOrg;
        private SyncType syncType;
        private Date syncStartTime;
        private Date syncEndTime;
        private String errorMessage;

        public String getIdOfSync() {
            return idOfSync;
        }

        public void setIdOfSync(String idOfSync) {
            this.idOfSync = idOfSync;
        }

        public long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public SyncType getSyncType() {
            return syncType;
        }

        public void setSyncType(SyncType syncType) {
            this.syncType = syncType;
        }

        public Date getSyncStartTime() {
            return syncStartTime;
        }

        public void setSyncStartTime(Date syncStartTime) {
            this.syncStartTime = syncStartTime;
        }

        public Date getSyncEndTime() {
            return syncEndTime;
        }

        public void setSyncEndTime(Date syncEndTime) {
            this.syncEndTime = syncEndTime;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}