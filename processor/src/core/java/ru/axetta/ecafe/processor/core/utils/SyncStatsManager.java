/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.sync.SyncType;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ziganshin
 * Date: 4/23/15
 * Time: 2:46 PM
 * todo удалить по завершению процесса диагностики
 * Добавлено для диагностики процесса синхронизации
 */

@Component
@Scope("prototype")
public class SyncStatsManager {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SyncStatsManager.class);
    private static final SyncCollector syncCollector = SyncCollector.getInstance();

    public void logData() throws Exception {
        RuntimeContext.getAppContext().getBean(SyncStatsManager.class).showData();
    }

    @Async
    public void showData() throws Exception {
        List<SyncCollector.SyncData> syncListCopy;
        List<SyncCollector.SyncData> errList = new ArrayList<SyncCollector.SyncData>();
        Long time;
        synchronized (SyncCollector.class) {
            syncListCopy = syncCollector.syncList;
            syncCollector.syncList = new ArrayList<SyncCollector.SyncData>();
            time = syncCollector.startTime.getTime();
            syncCollector.startTime = new Date();
        }
        logger.info(syncListCopy.size() + " syncs completed after " + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .format(new Date(time)) + ".");
        Map<SyncType, Long> syncTypesCount = new HashMap<SyncType, Long>();
        for (SyncCollector.SyncData syncData : syncListCopy) {
            Long syncTypeCount = 1L;
            if (syncTypesCount.containsKey(syncData.getSyncType())) {
                syncTypeCount += syncTypesCount.get(syncData.getSyncType());
            }
            syncTypesCount.put(syncData.getSyncType(), syncTypeCount);
        }
        for (SyncType syncType : syncTypesCount.keySet()) {
            logger.info("Sync type " + syncType.toString() + " completed " + syncTypesCount.get(syncType) + " times.");
        }
        // Если SyncData не обработан в течении часа, то есть проблема
        for (Long syncTime : syncCollector.tempSyncs.keySet()) {
            if (syncTime < (time - 3600000L)) {
                SyncCollector.SyncData syncData = null;
                synchronized (SyncCollector.class) {
                    if (syncCollector.tempSyncs.containsKey(syncTime)) {
                        syncData = syncCollector.tempSyncs.get(syncTime);
                        errList.add(syncData);
                        syncCollector.tempSyncs.remove(syncTime);
                    }
                }
            }
        }
        for (SyncCollector.SyncData syncData : errList) {
            String errorMessage =
                    "Sync error on:" + " idOfSync(" + syncData.getIdOfSync() + "), " + " idOfOrg(" + syncData
                            .getIdOfOrg() + "), " + " syncType(" + syncData.getSyncType().toString() + "), "
                            + " syncStartTime(" + syncData.getSyncStartTime() + "), " + " syncEndTime(" + syncData
                            .getSyncEndTime() + "), " + " errMsg(" + syncData.getErrorMessage() + ").";
            logger.error(errorMessage);
        }
    }
}
