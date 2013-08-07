/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.Org;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 07.10.11
 * Time: 16:14
 * To change this template use File | Settings | File Templates.
 */
public class StatusSyncReport extends BasicReport {

    private List<Sync> syncItems = new ArrayList<Sync>();

    public static class Builder {

        @SuppressWarnings("unchecked")
        public StatusSyncReport build(Session session) throws Exception {
            Date generateTime = new Date();
            Calendar localCalendar = Calendar.getInstance();
            localCalendar.setTime(generateTime);
            localCalendar.set(Calendar.HOUR_OF_DAY, 0);
            localCalendar.set(Calendar.MINUTE, 0);
            localCalendar.set(Calendar.SECOND, 0);
            localCalendar.set(Calendar.MILLISECOND, 0);
            Date startDate = localCalendar.getTime();
            localCalendar.add(Calendar.DATE, 1);
            Date endDate = localCalendar.getTime();

            Query q = session.createQuery("select sh.org.idOfOrg as idOfOrg, max(sh.syncEndTime) \n" +
                    "from SyncHistory as sh \n" +
                    "where sh.syncEndTime <= :today group by idOfOrg");
            q.setParameter("today", startDate);
            List<Object[]> resultList = (List<Object[]>) q.list();
            Map<Long, Date> todayMap = new HashMap<Long, Date>();
            for (Object[] result : resultList) {
                todayMap.put((Long) result[0], (Date) result[1]);
            }

            q = session.createQuery("select sh.org.idOfOrg as idOfOrg from SyncHistory sh where sh.syncEndTime >= :startDate and sh.syncEndTime < :endDate group by idOfOrg");
            q.setParameter("startDate", startDate);
            q.setParameter("endDate", endDate);
            List<Long> lastDateList = (List<Long>) q.list();

            Criteria orgCriteria = session.createCriteria(Org.class);
            orgCriteria.setProjection(Projections.projectionList()
                    .add(Projections.property("idOfOrg"),"idOfOrg")
                    .add(Projections.property("shortName"),"shortName")
                    .add(Projections.property("officialName"),"officialName")
            );
            orgCriteria.addOrder(Order.asc("idOfOrg"));
            orgCriteria.setResultTransformer(Transformers.aliasToBean(BasicReportJob.OrgShortItem.class));
            List<BasicReportJob.OrgShortItem> orgItems = orgCriteria.list();

            q = session.createQuery("select she.message as message \n" +
                    "from SyncHistory sh join sh.syncHistoryExceptions as she \n" +
                    "where sh.syncEndTime = :time and sh.org.idOfOrg = :orgId");
            List<Sync> syncItems = new ArrayList<Sync>();
            for (BasicReportJob.OrgShortItem org : orgItems) {
                Long orgId = org.getIdOfOrg();
                Sync sync = new Sync(orgId, org.getOfficialName(), lastDateList.contains(orgId), todayMap.get(orgId));
                // вытягиваем сообщения об ошибках.
                if (todayMap.containsKey(orgId)) {
                    q.setParameter("time", todayMap.get(orgId));
                    q.setParameter("orgId", orgId);
                    List<String> messageList = q.list();
                    sync.getSyncErrors().addAll(messageList);
                }
                syncItems.add(sync);
            }
            return new StatusSyncReport(generateTime, new Date().getTime() - generateTime.getTime(), syncItems);
        }

    }

    public StatusSyncReport() {
        super();
    }

    public StatusSyncReport(Date generateTime, long generateDuration, List<Sync> syncItems) {
        super(generateTime, generateDuration);
        this.syncItems = syncItems;
    }

    public List<Sync> getSyncItems() {
        return syncItems;
    }

    public static class Sync {
        private long idOfOrg;
        private String officialName;
        private boolean snchrnzd;
        private Date lastSyncTime;
        private List<String> syncErrors = new ArrayList<String>();

        public Sync(long idOfOrg, String officialName, boolean snchrnzd, Date lastSyncTime) {
            this.idOfOrg = idOfOrg;
            this.officialName = officialName;
            this.snchrnzd = snchrnzd;
            this.lastSyncTime = lastSyncTime;
        }

        public long getIdOfOrg() {
            return idOfOrg;
        }

        public String getOfficialName() {
            return officialName;
        }

        public boolean isSnchrnzd() {
            return snchrnzd;
        }

        public Date getLastSyncTime() {
            return lastSyncTime;
        }

        public void setLastSyncTime(Date lastSyncTime) {
            this.lastSyncTime = lastSyncTime;
        }

        public List<String> getSyncErrors() {
            return syncErrors;
        }

        public void setSyncErrors(List<String> syncErrors) {
            this.syncErrors = syncErrors;
        }
    }
}
