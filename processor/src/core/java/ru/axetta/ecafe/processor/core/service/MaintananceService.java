/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.cleaner.DBCleaner;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;

@Component
@Scope("singleton")
public class MaintananceService {
    Logger logger = LoggerFactory.getLogger(MaintananceService.class);

    Date lastCleanDate;
    Integer maintananceHour;

    @Autowired
    RuntimeContext runtimeContext;

    public void run() {
        if (maintananceHour==null) {
            maintananceHour = runtimeContext.getPropertiesValue(RuntimeContext.PARAM_NAME_DB_MAINTANANCE_HOUR, 22);
        }
        //if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)==maintananceHour && (lastCleanDate==null || System.currentTimeMillis()-lastCleanDate.getTime()>12*60*60*1000)) {
            logger.info("Starting DB maintanance procedures...");
            lastCleanDate = new Date();
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            String report = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                report = DBCleaner.clean(persistenceSession, logger);
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } catch (Exception e) {
                logger.error("Failed clean data base", e);
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
            logger.info("DB maintanance procedures finished successfully. " + report);
        //}
    }
}
