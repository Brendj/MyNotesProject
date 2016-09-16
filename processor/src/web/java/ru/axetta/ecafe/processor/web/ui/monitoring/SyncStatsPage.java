package ru.axetta.ecafe.processor.web.ui.monitoring;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.SyncHistoryCalc;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportWithContragentPage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * Created with IntelliJ IDEA.
 * User: ziganshin
 * Date: 5/5/15
 * Time: 6:11 PM
 */
@Component
@Scope("session")
public class SyncStatsPage extends OnlineReportWithContragentPage {

    private final static Logger logger = LoggerFactory.getLogger(SyncStatsPage.class);

    private Boolean applyUserSettings = false;
    private Date[] period = CalendarUtils.getCurrentWeekBeginAndEnd(new Date());
    private Date periodStart = period[0];
    private Date periodEnd = period[1];
    private List<Item> syncStatsOnPeriod = new ArrayList<Item>();

    public Object update() {
        syncStatsOnPeriod = updateSyncStatsOnPeriod();
        return null;
    }

    private List<Item> updateSyncStatsOnPeriod() {
        List<Item> result = new ArrayList<Item>();
        Long successfulSyncCount = 0L;
        Long filteredSyncCount = 0L;
        Long errorSyncCount = 0L;
        Long averageResyncTime = 0L;
        List<Long> resyncTimes = new ArrayList<Long>();
        Long minSyncDuration = null;
        Long averageSyncDuration = 0L;
        Long maxSyncDuration = 0L;
        List<Long> syncDurations = new ArrayList<Long>();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            List<SyncHistoryCalc> existedSyncHistoryCalcList = new ArrayList<SyncHistoryCalc>();
            if (idOfOrgList.size() > 0) {
                for (Long idOfOrg : idOfOrgList) {
                    existedSyncHistoryCalcList.addAll(
                            getSyncHistoryCalcList(persistenceSession, idOfOrg, getPeriodStart(), getPeriodEnd()));
                }
            } else {
                existedSyncHistoryCalcList.addAll(
                        getSyncHistoryCalcList(persistenceSession, null, getPeriodStart(), getPeriodEnd()));
            }

            for (SyncHistoryCalc syncHistoryCalc : existedSyncHistoryCalcList) {
                switch (syncHistoryCalc.getDataType()) {
                    case SyncHistoryCalc.SUCCESSFUL_SYNC_COUNT_POSITION:
                        successfulSyncCount += Long.parseLong(syncHistoryCalc.getValue());
                        break;
                    case SyncHistoryCalc.FILTERED_SYNC_COUNT_POSITION:
                        filteredSyncCount += Long.parseLong(syncHistoryCalc.getValue());
                        break;
                    case SyncHistoryCalc.ERROR_SYNC_COUNT_POSITION:
                        errorSyncCount += Long.parseLong(syncHistoryCalc.getValue());
                        break;
                    case SyncHistoryCalc.AVG_RESYNC_TIME_POSITION:
                        resyncTimes.add(Long.parseLong(syncHistoryCalc.getValue()));
                        break;
                    case SyncHistoryCalc.MIN_SYNC_DURATION:
                        if (minSyncDuration == null) {
                            minSyncDuration = Long.parseLong(syncHistoryCalc.getValue());
                        } else {
                            if (minSyncDuration > Long.parseLong(syncHistoryCalc.getValue())) {
                                minSyncDuration = Long.parseLong(syncHistoryCalc.getValue());
                            }
                        }
                        break;
                    case SyncHistoryCalc.AVG_SYNC_DURATION:
                        syncDurations.add(Long.parseLong(syncHistoryCalc.getValue()));
                        break;
                    case SyncHistoryCalc.MAX_SYNC_DURATION:
                        if (maxSyncDuration < Long.parseLong(syncHistoryCalc.getValue())) {
                            maxSyncDuration = Long.parseLong(syncHistoryCalc.getValue());
                        }
                        break;
                }
            }

            if (resyncTimes.size() > 0) {
                for (Long currentValue : resyncTimes) {
                    averageResyncTime += currentValue;
                }
                averageResyncTime /= resyncTimes.size();
            }

            if (syncDurations.size() > 0) {
                for (Long currentValue : syncDurations) {
                    averageSyncDuration += currentValue;
                }
                averageSyncDuration /= syncDurations.size();
            }

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        result.add(new Item("Успешных синхронизаций", successfulSyncCount.toString()));
        result.add(new Item("Повторных (отбитых) синхронизаций", filteredSyncCount.toString())); // todo заменить слово "повторных" на "отбитых"
        result.add(new Item("Неуспешных синхронизаций", errorSyncCount.toString()));
        result.add(new Item("Среднее время реконнекта (мс)", averageResyncTime.toString()));
        result.add(new Item("Минимальное время синхронизации (мс)", minSyncDuration !=  null ? minSyncDuration.toString() : "0"));
        result.add(new Item("Среднее время синхронизации (мс)", averageSyncDuration.toString()));
        result.add(new Item("Максимальное время синхронизации (мс)", maxSyncDuration.toString()));

        return result;
    }

    private List<SyncHistoryCalc> getSyncHistoryCalcList(Session persistenceSession, Long idOfOrg, Date periodStart, Date periodEnd) {
        return DAOUtils
                .getSyncHistoryCalc(persistenceSession, idOfOrg, periodStart, periodEnd, null);
    }

    public String getPageFilename() {
        return "monitoring/sync_stats_manager_data";
    }

    public SyncStatsPage() {

    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Date getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(Date periodStart) {
        this.periodStart = periodStart;
    }

    public Date getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(Date periodEnd) {
        this.periodEnd = periodEnd;
    }

    public List<Item> getSyncStatsOnPeriod() {
        return syncStatsOnPeriod;
    }

    public void setSyncStatsOnPeriod(List<Item> syncStatsOnPeriod) {
        this.syncStatsOnPeriod = syncStatsOnPeriod;
    }

    public Boolean getApplyUserSettings() {
        return applyUserSettings;
    }

    public void setApplyUserSettings(Boolean applyUserSettings) {
        this.applyUserSettings = applyUserSettings;
    }

    public class Item {
        private String name;
        private String value;

        public Item(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
