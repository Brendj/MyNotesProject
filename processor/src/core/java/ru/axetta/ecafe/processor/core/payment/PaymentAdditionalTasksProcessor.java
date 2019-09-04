/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.payment;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.atol.AtolPaymentNotificator;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.persistence.ClientPaymentAddon;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by i.semenov on 26.08.2019.
 */
@Component
@Scope("singleton")
@DependsOn("runtimeContext")
public class PaymentAdditionalTasksProcessor {

    private static final Logger logger = LoggerFactory.getLogger(PaymentAdditionalTasksProcessor.class);
    List<IPaymentNotificator> listTasks;
    public static final String NOTIFICATORS_CONFIG_PROPERTY = "ecafe.processor.payment.notificators";
    public static final String NOTIFICATORS_CRON_CONFIG_PROPERTY = "ecafe.processor.payment.notificators.cron";
    public static final String NOTIFICATORS_NODE_CONFIG_PROPERTY = "ecafe.processor.payment.notificators.node";
    public static final String SCHEDULE_NAME = "Payment_Notification";

    public PaymentAdditionalTasksProcessor() {
        listTasks = new ArrayList<IPaymentNotificator>();
        String notificators = RuntimeContext.getInstance().getConfigProperties().getProperty(NOTIFICATORS_CONFIG_PROPERTY, "");
        String[] arr = notificators.split(",");
        for (String notificator : arr) {
            switch (notificator) {
                case "ATOL" : listTasks.add(new AtolPaymentNotificator());
                    break;
            }
        }
        String syncScheduleCron = RuntimeContext.getInstance().getConfigProperties().getProperty(NOTIFICATORS_CRON_CONFIG_PROPERTY, "");
        if (RuntimeContext.getInstance().actionIsOnByNode(NOTIFICATORS_NODE_CONFIG_PROPERTY) && !StringUtils.isEmpty(syncScheduleCron)) {
            try {
                JobDetail jobDetail = new JobDetail(SCHEDULE_NAME, Scheduler.DEFAULT_GROUP, NotificationJob.class);
                SchedulerFactory sfb = new StdSchedulerFactory();
                Scheduler scheduler = sfb.getScheduler();
                CronTrigger triggerDaily = new CronTrigger(SCHEDULE_NAME, Scheduler.DEFAULT_GROUP);
                triggerDaily.setCronExpression(syncScheduleCron);
                if (scheduler.getTrigger(SCHEDULE_NAME, Scheduler.DEFAULT_GROUP) != null) {
                    scheduler.deleteJob(SCHEDULE_NAME, Scheduler.DEFAULT_GROUP);
                }
                scheduler.scheduleJob(jobDetail, triggerDaily);
            } catch (Exception e) {
                logger.error("Error in schedule payment notification task: ", e);
            }
        }
    }

    public void savePayment(Session session, ClientPayment clientPayment) {
        if (!isToSavePayment(clientPayment)) return;
        ClientPaymentAddon clientPaymentAddon = new ClientPaymentAddon(clientPayment);
        for (IPaymentNotificator notificator : listTasks) {
            notificator.addNotificatorValues(clientPaymentAddon, clientPayment);
        }
        session.save(clientPaymentAddon);
        //saveClientPaymentAddon(clientPaymentAddon);
    }

    private boolean isToSavePayment(ClientPayment clientPayment) {
        for (IPaymentNotificator notificator : listTasks) {
            if (notificator.isToSave(clientPayment)) return true;
        }
        return false;
    }

    public void runNotifications() {
        logger.info("Start payment notifications task");
        for (IPaymentNotificator notificator : listTasks) {
            notificator.sendNotifications();
        }
        logger.info("End payment notifications task");
    }

    public static class NotificationJob implements StatefulJob {
        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            RuntimeContext.getAppContext().getBean(PaymentAdditionalTasksProcessor.class).runNotifications();
        }
    }
}
