/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import ru.axetta.ecafe.processor.core.sync.SyncType;

import java.util.*;

public class SyncCollector {

    private static Date startTime = new Date();
    private static List<SyncData> syncList = new ArrayList<SyncData>();
    // todo проработать вопрос об изменении ключа, т.к. могут случаться коллизии,
    // todo когда два клиента будут пытаться синхронизироваться одновременно
    private static Map<Long, SyncData> tempSyncs = new HashMap<Long, SyncData>();
    private static SyncCollector INSTANCE = new SyncCollector();
    private static Boolean reportOn = true;

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
        if (reportOn) {
            clearOldData();
            tempSyncs.put(syncTime, new SyncData());
            tempSyncs.get(syncTime).setSyncStartTime(new Date());
        }
    }

    public static void registerSyncEnd(Long syncTime) {
        if (reportOn) {
            clearOldData();
            if (tempSyncs.containsKey(syncTime)) {
                SyncData sync = tempSyncs.get(syncTime);
                sync.setSyncEndTime(new Date());
                Date endTime = sync.getSyncEndTime();
                Date startTime = sync.getSyncStartTime();
                if (endTime != null && startTime != null) {
                    sync.setDuration(endTime.getTime() - startTime.getTime());
                }
                if(sync.getSyncType() != null) {
                    syncList.add(sync);
                }
                tempSyncs.remove(syncTime);
            }
        }
    }

    public static void setIdOfOrg(Long syncTime, Long idOfOrg) {
        if (reportOn) {
            clearOldData();
            if (tempSyncs.containsKey(syncTime)) {
                tempSyncs.get(syncTime).setIdOfOrg(idOfOrg);
            }
        }
    }

    public static void setIdOfSync(Long syncTime, String idOfSync) {
        if (reportOn) {
            clearOldData();
            if (tempSyncs.containsKey(syncTime)) {
                tempSyncs.get(syncTime).setIdOfSync(idOfSync);
            }
        }
    }

    public static void setSyncType(Long syncTime, SyncType syncType) {
        if (reportOn) {
            clearOldData();
            if (tempSyncs.containsKey(syncTime)) {
                tempSyncs.get(syncTime).setSyncType(syncType);
            }
        }
    }

    public static void setErrMessage(Long syncTime, String errMessage) {
        if (reportOn) {
            clearOldData();
            if (tempSyncs.containsKey(syncTime)) {
                tempSyncs.get(syncTime).setErrorMessage(errMessage);
            }
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

    public static void setProperties(Boolean isReportOn) {
        reportOn = isReportOn;
    }


    public static Boolean getReportOn() {
        return reportOn;
    }
}