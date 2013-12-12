/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.monitoring;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.dashboard.DashboardServiceBean;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 12.12.13
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope(value = "session")
public class SynchErrorsPage extends BasicWorkspacePage {
    private static final Logger logger = LoggerFactory.getLogger(SynchErrorsPage.class);
    @Autowired
    DashboardServiceBean dashboardServiceBean;
    @PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManager;
    private long idoforg;
    private List<Item> items;
    private static final DateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");


    public SynchErrorsPage() {
    }

    public Object show(long idoforg) {
        this.idoforg = idoforg;
        return super.show();
    }

    public void update() {
        RuntimeContext.getAppContext().getBean(SynchErrorsPage.class).loadData();
    }

    public void doClear () {
        RuntimeContext.getAppContext().getBean(SynchErrorsPage.class).clearData();
        items.clear();
    }

    @Override
    public void onShow() {
        RuntimeContext.getAppContext().getBean(SynchErrorsPage.class).loadData();
    }

    @Transactional
    public void loadData() {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            loadData(session);
        } catch (Exception e) {
            logger.error("Failed to load errors", e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void loadData(Session session) {
        items = new ArrayList<Item>();
        org.hibernate.Query q = session.createSQLQuery(
                "select syncendtime, message "
                + "from cf_synchistory_exceptions "
                + "left join cf_synchistory on cf_synchistory.idofsync=cf_synchistory_exceptions.idofsync "
                + "where cf_synchistory_exceptions.idoforg=:idoforg");
        q.setLong("idoforg", idoforg);
        List resultList = q.list();
        for (Object entry : resultList) {
            Object o[] = (Object[]) entry;
            Long ts = HibernateUtils.getDbLong(o[0]);
            String message = HibernateUtils.getDbString(o[1]);
            items.add(new Item(ts, message));
        }
    }

    @Transactional
    public void clearData() {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            clearData(session);
        } catch (Exception e) {
            logger.error("Failed to clear errors", e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void clearData(Session session) {
        items = new ArrayList<Item>();
        org.hibernate.Query q = session.createSQLQuery(
                "delete from cf_synchistory_exceptions "
                + "where cf_synchistory_exceptions.idoforg=:idoforg");
        q.setLong("idoforg", idoforg);
        q.executeUpdate();
    }

    public List<Item> getItems() {
        return items;
    }
    
    public int getCount() {
        return items.size();
    }

    public String getPageFilename() {
        return "monitoring/sync_errors";
    }

    public class Item {
        private String message;
        private long ts;
        
        public Item(long ts, String message) {
            this.message = message;
            this.ts = ts;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public long getTs() {
            return ts;
        }

        public void setTs(long ts) {
            this.ts = ts;
        }
        
        public String getDate() {
            Date d = new Date(ts);
            return FORMAT.format(d);
        }
    }
}
