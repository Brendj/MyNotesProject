/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.daoservices.sync.SyncDAOService;
import ru.axetta.ecafe.processor.core.daoservices.sync.items.Sync;
import ru.axetta.ecafe.processor.core.persistence.SyncHistory;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

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
                SyncDAOService service = new SyncDAOService();
                service.setSession(session);
                syncItems = service.buildSyncReport(startDate, endDate, idOfOrgList);
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

}