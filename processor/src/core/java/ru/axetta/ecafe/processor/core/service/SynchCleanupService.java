/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 01.04.14
 * Time: 14:20
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
public class SynchCleanupService {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    private static final long WEEK_MILLISECONDS = 604800000L;
    private static final long DAY_MILLISECONDS  = 86400000L;


    public static boolean isOn() {
        return RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_SYNCH_CLEANUP_ON);
    }


    public static void setOn(boolean on) {
        RuntimeContext.getInstance().setOptionValueWithSave(Option.OPTION_SYNCH_CLEANUP_ON, "" + (on ? "1" : "0"));
    }


    public void run() throws IOException {
        if (!RuntimeContext.getInstance().isMainNode() || !isOn()) {
            //logger.info ("BI data export is turned off. You have to activate this tool using common Settings");
            return;
        }
        RuntimeContext.getAppContext().getBean(SynchCleanupService.class).execute();
    }

    @Transactional
    public void execute() {
        Session session = (Session) entityManager.getDelegate();
        clearExceptionsEntries(session);
        clearEntries(session);
        clearDaily(session);
    }

    protected void clearExceptionsEntries(Session session) {
        Query q = session.createSQLQuery("TRUNCATE table cf_synchistory_exceptions");
        q.executeUpdate();
    }

    protected void clearEntries(Session session) {
        long datelimit = System.currentTimeMillis() - WEEK_MILLISECONDS;
        Query q = session.createSQLQuery("delete from cf_synchistory where syncendtime<:datelimit");
        q.setParameter("datelimit", datelimit);
        q.executeUpdate();
    }

    protected void clearDaily(Session session) {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(System.currentTimeMillis() - DAY_MILLISECONDS);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        Query q = session.createSQLQuery("delete from cf_synchistory where syncdate<:datelimit");
        q.setParameter("datelimit", cal.getTimeInMillis());
        q.executeUpdate();
    }
}
