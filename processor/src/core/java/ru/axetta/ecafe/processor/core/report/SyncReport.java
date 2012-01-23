/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.SyncHistory;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SyncReport extends BasicReport {

    private final List<Sync> syncItems;

    public static class Builder {

        public SyncReport build(Session session, Date startDate, Date endDate, List<Long> idOfOrgList)
                throws Exception {
            Date generateTime = new Date();
            List<Sync> syncItems = new ArrayList<Sync>();
            if (!idOfOrgList.isEmpty()) {
                Criteria syncCriteria = session.createCriteria(SyncHistory.class);
                LogicalExpression expression1 = Restrictions
                        .and(Restrictions.ge("syncStartTime", startDate), Restrictions.le("syncStartTime", endDate));
                LogicalExpression expression2 = Restrictions
                        .and(Restrictions.ge("syncEndTime", startDate), Restrictions.le("syncEndTime", endDate));
                LogicalExpression expression = Restrictions.or(expression1, expression2);
                // Обработать лист с организациями
                String orgCondition = "(";
                for (Long idOfOrg : idOfOrgList) {
                    orgCondition = orgCondition.concat("idOfOrg = " + idOfOrg + " or ");
                }
                orgCondition = orgCondition.substring(0, orgCondition.length() - 4) + ")";
                expression = Restrictions.and(Restrictions.sqlRestriction(orgCondition), expression);
                syncCriteria.add(expression);
                syncCriteria.addOrder(Order.asc("org.idOfOrg"));
                List<SyncHistory> syncHistoryItems = syncCriteria.list();

                for (SyncHistory sync : syncHistoryItems) {
                    syncItems.add(new Sync(sync));
                }
            }
            return new SyncReport(generateTime, new Date().getTime() - generateTime.getTime(), syncItems);
        }

    }

    public SyncReport() {
        super();
        this.syncItems = Collections.emptyList();
    }

    public SyncReport(Date generateTime, long generateDuration, List<Sync> syncItems) {
        super(generateTime, generateDuration);
        this.syncItems = syncItems;
    }

    public List<Sync> getSyncItems() {
        return syncItems;
    }

    public static class Sync {

        private long idOfOrg;
        private String officialName;
        private Date syncStartTime;
        private Date syncEndTime;

        Sync(SyncHistory syncHistory) {
            this.idOfOrg = syncHistory.getOrg().getIdOfOrg();
            this.officialName = syncHistory.getOrg().getOfficialName();
            this.syncStartTime = syncHistory.getSyncStartTime();
            this.syncEndTime = syncHistory.getSyncEndTime();
        }

        public long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public String getOfficialName() {
            return officialName;
        }

        public void setOfficialName(String officialName) {
            this.officialName = officialName;
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
    }
}