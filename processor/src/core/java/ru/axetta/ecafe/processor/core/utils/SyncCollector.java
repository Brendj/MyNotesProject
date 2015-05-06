/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import ru.axetta.ecafe.processor.core.sync.SyncType;

import java.util.*;

public class SyncCollector {

    private static Date startTime = new Date();
    private static List<SyncData> syncList = new ArrayList<SyncData>();
    private static Map<Long, SyncData> tempSyncs = new HashMap<Long, SyncData>();
    private static SyncCollector INSTANCE = new SyncCollector();

    private SyncCollector() {
    }

    public static SyncCollector getInstance() {
        return INSTANCE;
    }

    private static void clearOldData() {
        if (startTime.getTime() < new Date().getTime() - 7200000L) {
            startTime = new Date();
            syncList = new ArrayList<SyncData>();
            tempSyncs = new HashMap<Long, SyncData>();
        }
    }

    public static void registerSyncStart(Long syncTime) {
        clearOldData();
        tempSyncs.put(syncTime, new SyncData());
        tempSyncs.get(syncTime).setSyncStartTime(new Date());
    }

    public static void registerSyncEnd(Long syncTime) {
        clearOldData();
        if (tempSyncs.containsKey(syncTime)) {
            tempSyncs.get(syncTime).setSyncEndTime(new Date());
            syncList.add(tempSyncs.get(syncTime));
            tempSyncs.remove(syncTime);
        }
    }

    public static void setIdOfOrg(Long syncTime, Long idOfOrg) {
        clearOldData();
        if (tempSyncs.containsKey(syncTime)) {
            tempSyncs.get(syncTime).setIdOfOrg(idOfOrg);
        }
    }

    public static void setIdOfSync(Long syncTime, String idOfSync) {
        clearOldData();
        if (tempSyncs.containsKey(syncTime)) {
            tempSyncs.get(syncTime).setIdOfSync(idOfSync);
        }
    }

    public static void setSyncType(Long syncTime, SyncType syncType) {
        clearOldData();
        if (tempSyncs.containsKey(syncTime)) {
            tempSyncs.get(syncTime).setSyncType(syncType);
        }
    }

    public static void setErrMessage(Long syncTime, String errMessage) {
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

    public static void setIdData(Long syncTime, Long idOfOrg, String idOfSync, SyncType syncType) {
        setIdOfOrg(syncTime, idOfOrg);
        setIdOfSync(syncTime, idOfSync);
        setSyncType(syncTime, syncType);
    }
}