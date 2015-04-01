/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.SMSDeliveryReport;
import ru.axetta.ecafe.processor.core.report.SMSDeliveryReportItem;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 31.03.15
 * Time: 14:59
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
public class SmsDeliveryCalculationService {
    private final static Logger logger = LoggerFactory.getLogger(SmsDeliveryCalculationService.class);
    private static final long DAY_MILLISECONDS = 86400000L;
    public static final String POSTFIX = "Ts";
    public static String[] DATA_TYPES = new String[] {"0_maxDelayMiddayTs",
                                                      "0_sumDelayMiddayTs",
                                                      "0_maxDelayNightTs",
                                                      "0_sumDelayNightTs",
                                                      "0_lastSyncTs"};

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    public void run() {
        RuntimeContext.getAppContext().getBean(SmsDeliveryCalculationService.class).doRun();
    }

    @Transactional
    public void doRun() {
        Session session = null;
        try {
            session = entityManager.unwrap(Session.class);

            Calendar cal = new GregorianCalendar();
            cal.setTimeInMillis(System.currentTimeMillis());
            cal = resetCalendar(cal);
            Date end = cal.getTime();
            cal.setTimeInMillis(cal.getTimeInMillis() - DAY_MILLISECONDS);
            Date start = cal.getTime();

            List<SMSDeliveryReportItem> items = SMSDeliveryReport.Builder.findSmsSyncItem(session, start, end, null);
            saveData(session, items, cal.getTime());
        } catch (Exception e) {
            logger.error("Failed to consolidate sms delivery info", e);
        }
    }

    protected void saveData(Session session, List<SMSDeliveryReportItem> items, Date syncDate) {
        Query delQ = session.createSQLQuery("delete from cf_synchistory_calc where idOfOrg=:idOfOrg and calcDateAt=:calcDateAt");
        Query insQ = session.createSQLQuery("insert into cf_synchistory_calc (idOfOrg, calcDateAt, calcType, calcValue) values (:idOfOrg, :calcDateAt, :calcType, :calcValue)");
        delQ.setParameter("calcDateAt", syncDate.getTime());
        insQ.setParameter("calcDateAt", syncDate.getTime());
        for(SMSDeliveryReportItem i : items) {
            long idOfOrg = i.getOrgId();
            insQ.setParameter("idOfOrg", idOfOrg);
            delQ.setParameter("idOfOrg", idOfOrg);
            delQ.executeUpdate();
            for(String k : i.getValues().keySet()) {
                String v = i.getValues().get(k);
                Integer dataType = SmsDeliveryCalculationService.getDataTypeId(k);
                if(dataType == null) {
                    continue;
                }
                insQ.setParameter("calcType", dataType);
                insQ.setParameter("calcValue", Long.parseLong(v));
                insQ.executeUpdate();
            }
        }
    }

    public static final Calendar resetCalendar(Calendar calendar) {
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        return calendar;
    }

    public static final Integer getDataTypeId(String name) {
        for(int i=0; i<DATA_TYPES.length; i++) {
            String dt = DATA_TYPES[i];
            if(dt.equals(name)) {
                return i;
            }
        }
        return null;
    }

    public static final String getDataTypeName(int id) {
        if(id >= DATA_TYPES.length || id < 0) {
            return null;
        }
        return DATA_TYPES[id];
    }
}