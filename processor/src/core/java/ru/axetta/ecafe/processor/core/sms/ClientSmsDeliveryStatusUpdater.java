/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms;

import org.hibernate.SessionFactory;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.ExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 24.02.2010
 * Time: 15:41:07
 * To change this template use File | Settings | File Templates.
 */
public class ClientSmsDeliveryStatusUpdater {

    private static final Logger logger = LoggerFactory.getLogger(ClientSmsDeliveryStatusUpdater.class);

    private final ExecutorService executorService;
    private final Scheduler scheduler;
    private final int period;
    private final SessionFactory sessionFactory;
    private final SmsService smsService;

    public ClientSmsDeliveryStatusUpdater(ExecutorService executorService, Scheduler scheduler, int period,
            SessionFactory sessionFactory, SmsService smsService) {
        this.executorService = executorService;
        this.scheduler = scheduler;
        this.period = period;
        this.sessionFactory = sessionFactory;
        this.smsService = smsService;
    }

    public void start() throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Scheduling client SMS delivery status update task");
        }
        UpdateClientSmsDeliveryStatusJob.ExecuteEnvironment executeEnvironment = new UpdateClientSmsDeliveryStatusJob.ExecuteEnvironment(
                executorService, sessionFactory, smsService);

        Class jobClass = UpdateClientSmsDeliveryStatusJob.class;
        JobDetail jobDetail = new JobDetail(jobClass.getCanonicalName(), Scheduler.DEFAULT_GROUP, jobClass);
        jobDetail.getJobDataMap().put(UpdateClientSmsDeliveryStatusJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
        Trigger trigger = TriggerUtils.makeSecondlyTrigger(period);
        trigger.setName(jobClass.getCanonicalName());
        trigger.setStartTime(new Date());
        scheduler.scheduleJob(jobDetail, trigger);
    }

}