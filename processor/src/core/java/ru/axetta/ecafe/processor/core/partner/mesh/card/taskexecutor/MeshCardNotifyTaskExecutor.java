/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.mesh.card.taskexecutor;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.mesh.card.service.logic.MeshClientCardRefService;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.MeshClientCardRef;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.ejb.DependsOn;
import java.util.Date;
import java.util.List;

@DependsOn("runtimeContext")
@Service
public class MeshCardNotifyTaskExecutor {
    private static final String CRON_EXP_PROPERTY = "ecafe.processing.mesh.card.taskexecutor.cron";
    private static final String TARGET_NODE_PROPERTY = "ecafe.processing.mesh.card.taskexecutor.node";
    private static final String JOB_NAME = "MeshCardNotify";
    private String targetNode = "";
    private final Logger log = LoggerFactory.getLogger(MeshCardNotifyTaskExecutor.class);
    private Scheduler scheduler;

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
            scheduler = schedulerFactory.getScheduler();

            CronTrigger trigger = new CronTrigger(JOB_NAME, Scheduler.DEFAULT_GROUP);
            trigger.setCronExpression(cronExp);

            scheduler.scheduleJob(jobDetailCardNotify, trigger);
            scheduler.start();
        }
    }

    public void run(){
        Session session = null;
        Transaction transaction = null;
        try {
            Date lastProcessing = getProcessingTime();
            session = RuntimeContext.getInstance().createPersistenceSession();
            List<Card> cards = DAOUtils.getAllCardForMESHByTime(session, lastProcessing);
            for(Card c : cards){
                if(c.getClient() != null && StringUtils.isEmpty(c.getClient().getMeshGUID())){
                    continue;
                }
                try {
                    transaction = session.beginTransaction();

                    if(c.refNotExistsOrNotSending() && c.getClient() != null){ // Client exists, but no ref
                        MeshClientCardRef ref = meshClientCardRefService.createRef(c);
                        c.setMeshCardClientRef(ref);
                    } else if(c.getClient() == null){ // Ref exists, but card without owner
                        if(c.getMeshCardClientRef() == null){
                            continue;
                        }
                        MeshClientCardRef ref = meshClientCardRefService.deleteRef(c.getMeshCardClientRef());
                        c.setMeshCardClientRef(null);

                        session.delete(ref);
                    } else if(!c.getClient().equals(c.getMeshCardClientRef().getClient())){ // If the card owner has changed
                        meshClientCardRefService.changeRef(c);
                    } else {
                        meshClientCardRefService.updateRef(c.getMeshCardClientRef());
                    }
                    session.update(c);

                    transaction.commit();
                    transaction = null;
                } catch (Exception e){
                    log.error("", e);
                } finally {
                    HibernateUtils.rollback(transaction, log);
                }
            }
        } catch (Exception e){
            log.error("Critical exception, task skipped ", e);
        } finally {
            HibernateUtils.rollback(transaction, log);
            HibernateUtils.close(session, log);
        }
    }

    private Date getProcessingTime() {
        try{
            Trigger trigger = scheduler.getTrigger(JOB_NAME, Scheduler.DEFAULT_GROUP);
            long  delta = trigger.getNextFireTime().getTime() - trigger.getPreviousFireTime().getTime();
            return new Date(trigger.getPreviousFireTime().getTime() - delta);
        } catch (Exception e){
            return CalendarUtils.startOfDay(new Date());
        }
    }
}
