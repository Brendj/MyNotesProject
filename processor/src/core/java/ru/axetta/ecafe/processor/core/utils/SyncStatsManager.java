/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.SyncHistoryCalc;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.SyncType;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
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
 * Для диагностики процесса синхронизации
 */

@Component
@Scope("prototype")
public class SyncStatsManager {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SyncStatsManager.class);

    /**
     * Запускается по расписанию каждые 10 минут и сохраняет всю накопленную (после предыдущего запуска) информацию в БД
     *
     * @throws Exception
     */
    @Async
    public static void shortDataProcess() throws Exception {
        List<SyncData> syncListCopy;
        Long time;
        synchronized (SyncCollector.class) {
            syncListCopy = SyncCollector.getSyncList();
            SyncCollector.setSyncList(new ArrayList<SyncData>());
            time = SyncCollector.getStartTime().getTime();
            SyncCollector.setStartTime(new Date());
        }

        Date syncTime = getShortSyncTime();

        Map<SyncType, Long> syncTypesCount = getSyncTypesCount(syncListCopy);

        // Если SyncData не обработан в течении часа, то есть проблема в алгоритме.
        // todo вместо исключения отсылать письмо
        List<SyncData> errList = getErrorSyncs(time);
        if (errList.size() > 0) {
            throw new Exception("SyncStatsManager failed to process sync data");
        }

        showShortSyncData(syncListCopy.size(), time, syncTypesCount, errList);

        Map<Long, List<SyncData>> syncDataMap = buildSyncDataMap(syncListCopy);

        for (Long idOfOrg : syncDataMap.keySet()) {
            List<SyncData> orgSyncDataList = syncDataMap.get(idOfOrg);

            Map<Integer, String> orgStats = getOrgStats(orgSyncDataList);
            for (Integer type : orgStats.keySet()) {
                createSyncHistoryCalc(idOfOrg, syncTime, type, orgStats.get(type));
            }
        }
    }

    /**
     * Сортирует список синхронизаций
     *
     * @param syncListCopy - список синхронизаций
     * @return - синхронизации отсортированные по ОО
     */
    private static Map<Long, List<SyncData>> buildSyncDataMap(List<SyncData> syncListCopy) {
        Map<Long, List<SyncData>> syncDataMap = new HashMap<Long, List<SyncData>>();
        for (SyncData syncData : syncListCopy) {
            Long idOfOrg = syncData.getIdOfOrg();
            if (syncDataMap.containsKey(idOfOrg)) {
                syncDataMap.get(idOfOrg).add(syncData);
            } else {
                List<SyncData> orgSyncDataList = new ArrayList<SyncData>();
                orgSyncDataList.add(syncData);
                syncDataMap.put(idOfOrg, orgSyncDataList);
            }
        }
        return syncDataMap;
    }

    /**
     * Обрезает время к началу текущего 10-минутного интервала
     *
     * @return - время начала текущего 10-минутного интервала
     */
    private static Date getShortSyncTime() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        int minute = calendar.get(Calendar.MINUTE);
        calendar.set(Calendar.MINUTE, (minute / 10) * 10);
        return calendar.getTime();
    }

    /**
     * Формирует и возвращает статистику по полученному списку синхронизаций
     *
     * @param syncDataList - список данных сессий синхронизаций
     * @return - статистика по списку сессий синхронизаций
     */
    private static Map<Integer, String> getOrgStats(List<SyncData> syncDataList) {
        Long successfulSyncCount = 0L;
        Long filteredSyncCount = 0L;
        Long errorSyncCount = 0L;
        Long averageReconnectTime = 0L; // in ms
        List<Long> reconnectIntervals = new ArrayList<Long>();
        Date filteredSyncEndTime = null;
        for (SyncData syncData : syncDataList) {
            String errorMessage = syncData.getErrorMessage();
            if (filteredSyncEndTime != null) {
                reconnectIntervals.add(syncData.getSyncStartTime().getTime() - filteredSyncEndTime.getTime());
                filteredSyncEndTime = null;
            }
            if (errorMessage == null || errorMessage.equals("")) {
                successfulSyncCount++;
            } else {
                int errCode = 0;
                try {
                    errCode = Integer.parseInt(errorMessage);
                } catch (NumberFormatException e) {
                    logger.error("Error message int parsing error: ", e);
                }
                if (errCode == 429) {
                    filteredSyncCount++;
                    filteredSyncEndTime = syncData.getSyncEndTime();
                } else {
                    errorSyncCount++;
                }
            }
        }

        Map<Integer, String> stats = new HashMap<Integer, String>();
        if (successfulSyncCount > 0L) {
            stats.put(SyncHistoryCalc.SUCCESSFUL_SYNC_COUNT_POSITION, successfulSyncCount.toString());
        }
        if (filteredSyncCount > 0L) {
            stats.put(SyncHistoryCalc.FILTERED_SYNC_COUNT_POSITION, filteredSyncCount.toString());
        }
        if (errorSyncCount > 0L) {
            stats.put(SyncHistoryCalc.ERROR_SYNC_COUNT_POSITION, errorSyncCount.toString());
        }
        if (reconnectIntervals.size() > 0) {
            for (Long interval : reconnectIntervals) {
                averageReconnectTime += interval;
            }
            averageReconnectTime /= reconnectIntervals.size();
            stats.put(SyncHistoryCalc.AVG_RESYNC_TIME_POSITION, averageReconnectTime.toString());
        }
        return stats;
    }

    /**
     * Выводит статистику по в лог
     *
     * @param syncDataListSize - количество синхронизаций за период после time
     * @param time             - время начала накопления данных по синхронизациям
     * @param syncTypesCount   - количество синхронизаций по типам
     * @param errList          - список синхронизаций завершенных с неизвестной ошибкой
     */
    private static void showShortSyncData(Integer syncDataListSize, Long time, Map<SyncType, Long> syncTypesCount,
            List<SyncData> errList) {
        logger.info(syncDataListSize + " syncs completed after " + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .format(new Date(time)) + ".");
        for (SyncType syncType : syncTypesCount.keySet()) {
            logger.info("Sync type " + syncType.toString() + " completed " + syncTypesCount.get(syncType) + " times.");
        }

        for (SyncData syncData : errList) {
            String errorMessage =
                    "Sync error on:" + " idOfSync(" + syncData.getIdOfSync() + "), " + " idOfOrg(" + syncData
                            .getIdOfOrg() + "), " + " syncType(" + syncData.getSyncType().toString() + "), "
                            + " syncStartTime(" + syncData.getSyncStartTime() + "), " + " syncEndTime(" + syncData
                            .getSyncEndTime() + "), " + " errMsg(" + syncData.getErrorMessage() + ").";
            logger.error(errorMessage);
        }
    }

    /**
     * Проверка на наличие ошибок (незакрытые записи о синхронизациях).
     *
     * @param time - время начала текущего 10-минутного интервала сбора данных
     * @return - список незакрытых данных по синхронизациям
     */
    private static List<SyncData> getErrorSyncs(Long time) {
        List<SyncData> errList = new ArrayList<SyncData>();
        for (Long syncTime : SyncCollector.getTempSyncs().keySet()) {
            if (syncTime < (time - 3600000L)) {
                SyncData syncData;
                synchronized (SyncCollector.class) {
                    if (SyncCollector.getTempSyncs().containsKey(syncTime)) {
                        syncData = SyncCollector.getTempSyncs().get(syncTime);
                        errList.add(syncData);
                        SyncCollector.getTempSyncs().remove(syncTime);
                    }
                }
            }
        }
        return errList;
    }

    /**
     * Подсчет количества сеансов синхронизаций по типам
     *
     * @param syncDataList - список данных синхронизаций
     * @return - количество синхронизаций по типам
     */
    private static Map<SyncType, Long> getSyncTypesCount(List<SyncData> syncDataList) {
        Map<SyncType, Long> syncTypesCount = new HashMap<SyncType, Long>();
        for (SyncData syncData : syncDataList) {
            Long syncTypeCount = 1L;
            if (syncTypesCount.containsKey(syncData.getSyncType())) {
                syncTypeCount += syncTypesCount.get(syncData.getSyncType());
            }
            syncTypesCount.put(syncData.getSyncType(), syncTypeCount);
        }
        return syncTypesCount;
    }

    /**
     * Создает и сохраняет объект с данными о синхронизации
     *
     * @param idOfOrg   - id ОО
     * @param syncTime  - время сеанса синхронизации (начало 10-минутног или суточного интервала)
     * @param syncType  - тип сохраняемых данных
     * @param syncValue - значение сохраняемых данных
     * @return - сохраненный объект
     * @throws Exception
     */
    private static SyncHistoryCalc createSyncHistoryCalc(Long idOfOrg, Date syncTime, Integer syncType,
            String syncValue) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            List<SyncHistoryCalc> existedSyncHistoryCalcList = DAOUtils
                    .getSyncHistoryCalc(persistenceSession, idOfOrg, syncTime, new Date(), syncType);
            SyncHistoryCalc syncHistoryCalc;
            if (existedSyncHistoryCalcList.size() > 0) {
                if (existedSyncHistoryCalcList.size() > 1) {
                    throw new Exception("Critical error in SyncStatsManager");
                }
                syncHistoryCalc = existedSyncHistoryCalcList.get(0);
                Long syncValueLong = (Long.parseLong(syncValue) + Long.parseLong(syncHistoryCalc.getValue()));
                syncValue = syncValueLong.toString();
                persistenceSession.delete(syncHistoryCalc);
            }
            syncHistoryCalc = new SyncHistoryCalc(idOfOrg, syncTime, syncType, syncValue);
            persistenceSession.save(syncHistoryCalc);
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return syncHistoryCalc;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    /**
     * Обертка для запуска по расписанию
     *
     * @throws Exception
     */
    public void shortDataLog() throws Exception {
        shortDataProcess();
    }

    /**
     * Обертка для запуска по расписанию
     *
     * @throws Exception
     */
    public void processLogData() throws Exception {
        processLogData(1);
    }

    /**
     * Запускается по расписанию каждые сутки и сохраняет всю накопленную (после предыдущего запуска) информацию в БД
     *
     * @param afterdays - период спустя которое строится отчет (запуск за предыдущие периоды не влияет
     *                  на консистентность данных, а всего лишь пересохраняет их в первозданном виде)
     * @throws Exception
     */
    @Async
    public void processLogData(int afterdays) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        List yesterdaySyncHistoryCalcList;
        Date currentTime = new Date();
        Date periodEnd = CalendarUtils.truncateToDayOfMonth(currentTime);
        periodEnd = CalendarUtils.addDays(periodEnd, -afterdays + 1);
        Date periodStart = CalendarUtils.addDays(periodEnd, -1);
        Map<Long, List<SyncHistoryCalc>> syncMapByOrg = new HashMap<Long, List<SyncHistoryCalc>>();
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria criteria = persistenceSession.createCriteria(SyncHistoryCalc.class);
            criteria.add(Restrictions.between("syncDay", periodStart, periodEnd));

            yesterdaySyncHistoryCalcList = criteria.list();



            if (yesterdaySyncHistoryCalcList != null) {
                for (Object o : yesterdaySyncHistoryCalcList) {
                    SyncHistoryCalc syncHistoryCalc = (SyncHistoryCalc) o;
                    Long idOfOrg = syncHistoryCalc.getIdOfOrg();
                    if (syncMapByOrg.containsKey(idOfOrg)) {
                        syncMapByOrg.get(idOfOrg).add(syncHistoryCalc);
                    } else {
                        List<SyncHistoryCalc> newSyncHistoryCalc = new ArrayList<SyncHistoryCalc>();
                        newSyncHistoryCalc.add(syncHistoryCalc);
                        syncMapByOrg.put(idOfOrg, newSyncHistoryCalc);
                    }
                    persistenceSession.delete(o);
                }
            }


            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        Map<Integer, String> stats = new HashMap<Integer, String>();
        for (Long idOfOrg : syncMapByOrg.keySet()) {
            List<SyncHistoryCalc> orgShorSyncData = syncMapByOrg.get(idOfOrg);
            Long successfullSyncCount = 0L;
            Long filteredSyncCount = 0L;
            Long errorSyncCount = 0L;
            Long averageReconnectTime = 0L; // in ms
            List<Long> reconnectIntervals = new ArrayList<Long>();
            for (SyncHistoryCalc syncHistoryCalc : orgShorSyncData) {
                switch (syncHistoryCalc.getDataType()) {
                    case SyncHistoryCalc.SUCCESSFUL_SYNC_COUNT_POSITION:
                        successfullSyncCount += Long.parseLong(syncHistoryCalc.getValue());
                        break;
                    case SyncHistoryCalc.FILTERED_SYNC_COUNT_POSITION:
                        filteredSyncCount += Long.parseLong(syncHistoryCalc.getValue());
                        break;
                    case SyncHistoryCalc.ERROR_SYNC_COUNT_POSITION:
                        errorSyncCount += Long.parseLong(syncHistoryCalc.getValue());
                        break;
                    case SyncHistoryCalc.AVG_RESYNC_TIME_POSITION:
                        reconnectIntervals.add(Long.parseLong(syncHistoryCalc.getValue()));
                        break;
                    default:

                }
            }
            if (successfullSyncCount > 0L) {
                stats.put(SyncHistoryCalc.SUCCESSFUL_SYNC_COUNT_POSITION, successfullSyncCount.toString());
            }
            if (filteredSyncCount > 0L) {
                stats.put(SyncHistoryCalc.FILTERED_SYNC_COUNT_POSITION, filteredSyncCount.toString());
            }
            if (errorSyncCount > 0L) {
                stats.put(SyncHistoryCalc.ERROR_SYNC_COUNT_POSITION, errorSyncCount.toString());
            }
            if (reconnectIntervals.size() > 0) {
                for (Long interval : reconnectIntervals) {
                    averageReconnectTime += interval;
                }
                averageReconnectTime /= reconnectIntervals.size();
                stats.put(SyncHistoryCalc.AVG_RESYNC_TIME_POSITION, averageReconnectTime.toString());
            }
            for (Integer type : stats.keySet()) {
                createSyncHistoryCalc(idOfOrg, periodStart, type, stats.get(type));
            }
        }
    }
}
