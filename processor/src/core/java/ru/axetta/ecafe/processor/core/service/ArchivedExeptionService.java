/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: a.voinov
 * Date: 04.03.21
 */

@Component
@Scope("singleton")
public class ArchivedExeptionService {


    final static String AUTO_ARCHIVED = "AutoArchivedExeption";
    final EventNotificationService notificationService = RuntimeContext.getAppContext()
            .getBean(EventNotificationService.class);
    public static class archivedExeption implements Job {

        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                RuntimeContext.getAppContext().getBean(ArchivedExeptionService.class)
                        .start(persistenceSession);
                persistenceTransaction.commit();
            } catch (Exception e) {
            }
        }

        public static void manualStart() throws JobExecutionException {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                RuntimeContext.getAppContext().getBean(ArchivedExeptionService.class)
                        .start(persistenceSession);
                persistenceTransaction.commit();
            } catch (Exception e) {
            }
        }
    }

    public static boolean isOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getConfigProperties().
                getProperty("ecafe.processor.archived.exeption.node", "6");
        String[] nodes = reqInstance.split(",");
        for (String node : nodes) {
            if (!StringUtils.isBlank(instance) && !StringUtils.isBlank(reqInstance)
                    && instance.trim().equals(node.trim())) {
                return true;
            }
        }
        return false;
    }

    public void scheduleSync() throws Exception {
        if (!isOn())
            return;
        String syncScheduleSync = RuntimeContext.getInstance().getConfigProperties().
                getProperty("ecafe.processor.exemptionvisiting.archived.time", "0 0 5 ? * * *");
        try {
            JobDetail jobDetailSync = new JobDetail(AUTO_ARCHIVED, Scheduler.DEFAULT_GROUP, archivedExeption.class);
            SchedulerFactory sfb = new StdSchedulerFactory();
            Scheduler scheduler = sfb.getScheduler();

            CronTrigger triggerSync = new CronTrigger(AUTO_ARCHIVED, Scheduler.DEFAULT_GROUP);
            triggerSync.setCronExpression(syncScheduleSync);
            if (scheduler.getTrigger(AUTO_ARCHIVED, Scheduler.DEFAULT_GROUP) != null) {
                scheduler.deleteJob(AUTO_ARCHIVED, Scheduler.DEFAULT_GROUP);
            }
            scheduler.scheduleJob(jobDetailSync, triggerSync);
            scheduler.start();
        } catch (Exception e) {
        }
    }


    public void start(Session session) throws Exception {
        //Ставим флаг Архивный тем записям, которые устарели
        DAOService.getInstance().updateExemptionVisiting();
    }
}
