/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.mesh.card.taskexecutor;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.mesh.card.service.logic.MeshClientCardRefService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.ejb.DependsOn;
import java.util.Date;

@DependsOn("runtimeContext")
@Service
public class MeshCardNotifyTaskExecutor {
    private static final String CRON_EXP_PROPERTY = "ecafe.processing.mesh.card.taskexecutor.cron";
    private static final String TARGET_NODE_PROPERTY = "ecafe.processing.mesh.card.taskexecutor.node";
    private static final String JOB_NAME = "MeshCardNotify";
    private String targetNode = "";
    private final Logger log = LoggerFactory.getLogger(MeshCardNotifyTaskExecutor.class);

    private MeshClientCardRefService meshClientCardRefService;

    public static class MeshCardNotify implements Job {
        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            RuntimeContext.getAppContext().getBean(MeshCardNotifyTaskExecutor.class).run();
        }
    }

    public void scheduleSync() throws Exception {
        targetNode = RuntimeContext.getInstance().getConfigProperties()
                .getProperty(TARGET_NODE_PROPERTY, "");
        if(!RuntimeContext.getInstance().getNodeName().equals(targetNode)){ // Если не целевая нода, то шедулер даже не пытаемся запускать
            return;
        }
        String cronExp = RuntimeContext.getInstance().getConfigProperties()
                .getProperty(CRON_EXP_PROPERTY, "");

        if(!cronExp.isEmpty()) { // Если крона не задана, то так же не пытаемся создавать шедулер
            meshClientCardRefService = RuntimeContext.getAppContext().getBean(MeshClientCardRefService.class);

            JobDetail jobDetailCardNotify = new JobDetail(JOB_NAME, Scheduler.DEFAULT_GROUP,
                    MeshCardNotifyTaskExecutor.MeshCardNotify.class);

            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = schedulerFactory.getScheduler();

            CronTrigger trigger = new CronTrigger(JOB_NAME, Scheduler.DEFAULT_GROUP);
            trigger.setCronExpression(cronExp);

            scheduler.scheduleJob(jobDetailCardNotify, trigger);
            scheduler.start();
        }
    }

    public void run(){
        try {
            Date lastProcessing = CalendarUtils.parseDateWithDayTime(DAOService.getInstance().getDateLastProcessedCard());
            if(lastProcessing == null){
                lastProcessing = new Date();
            }
        } catch (Exception e){
            log.error("", e);
        }
    }
}
