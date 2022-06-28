/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.web.CommonTaskServlet;

/**
 * Created with IntelliJ IDEA.
 * User: a.voinov
 * Date: 04.03.21
 */

@Component
@Scope("singleton")
public class ArchivedExeptionService {
    private static final Logger logger = LoggerFactory.getLogger(ArchivedExeptionService.class);
    final static String AUTO_ARCHIVED = "AutoArchivedExeption";
    public static class archivedExeption implements Job {

        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            try {
                RuntimeContext.getAppContext().getBean(ArchivedExeptionService.class).start();
            } catch (Exception e) {
                logger.error("Failed auto arcived EMIAS", e);
            }
        }

        public static void manualStart() throws JobExecutionException {
            try {
                RuntimeContext.getAppContext().getBean(ArchivedExeptionService.class).start();
            } catch (Exception e) {
                logger.error("Failed manual arcived EMIAS", e);
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
            logger.error("Failed scheduleSync AutoArchivedExeption", e);
        }
    }


    public void start() throws Exception {
        //Ставим флаг Архивный тем записям, которые устарели
        DAOService.getInstance().updateExemptionVisiting();
    }
}
