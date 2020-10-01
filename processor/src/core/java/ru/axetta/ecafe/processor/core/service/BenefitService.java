/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

import static ru.axetta.ecafe.processor.core.logic.ClientManager.findGuardiansByClient;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 16.03.16
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */

@Component
@Scope("singleton")
public class BenefitService {
    Logger logger = LoggerFactory.getLogger(BenefitService.class);


    final static String JOB_NAME_END_BENEFIT="NotificationEndBenefit";
    public final static String DATE_END_DISCOUNT="DateEndDiscount";
    public final static String DTISZN_CODE="DtisznCode";
    public final static String DTISZN_DESCRIPTION="DtisznDescription";
    public final static String DATE="date";
    public final static String SERVICE_NUMBER="ServiceNumber";
    public final static String ID_DISCOUNT_INFO="idDiscountInfo";
    public static final String NODE_PROPERTY = "ecafe.processor.notification.client.endBenefit.node";


    public static class NotificationEndBenefit implements Job {
        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            RuntimeContext.getAppContext().getBean(BenefitService.class).runEndBenefit(false);
        }
    }


    public void scheduleSync() throws Exception {
        if (!isOn())
            return;
        String syncScheduleEndBenefit = RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.notification.client.endBenefit", "");
        try {
            JobDetail jobDetailEndBenefit = new JobDetail(JOB_NAME_END_BENEFIT, Scheduler.DEFAULT_GROUP, NotificationEndBenefit.class);
            SchedulerFactory sfb = new StdSchedulerFactory();
            Scheduler scheduler = sfb.getScheduler();
            if (!syncScheduleEndBenefit.equals("")) {
                CronTrigger triggerEndBenefit = new CronTrigger(JOB_NAME_END_BENEFIT, Scheduler.DEFAULT_GROUP);
                triggerEndBenefit.setCronExpression(syncScheduleEndBenefit);
                if (scheduler.getTrigger(JOB_NAME_END_BENEFIT, Scheduler.DEFAULT_GROUP)!=null) {
                    scheduler.deleteJob(JOB_NAME_END_BENEFIT, Scheduler.DEFAULT_GROUP);
                }
                scheduler.scheduleJob(jobDetailEndBenefit, triggerEndBenefit);
            }
            scheduler.start();
        } catch(Exception e) {
            logger.error("Failed to schedule notification end benefit service job:", e);
        }
    }

    public static boolean isOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getConfigProperties().getProperty(BenefitService.NODE_PROPERTY, "1");
        String[] nodes = reqInstance.split(",");
        for (String node : nodes) {
            if (!StringUtils.isBlank(instance) && !StringUtils.isBlank(reqInstance)
                    && instance.trim().equals(node.trim())) {
                return true;
            }
        }
        return false;
    }

    public void runEndBenefit(boolean forTest) {
        Date startDate = new Date(System.currentTimeMillis());
        startDate = CalendarUtils.addOneDay(startDate);
        startDate = CalendarUtils.startOfDay(startDate);
        Integer days;
        String dayss = RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.notification.client.endBenefitdays", "1");
        try
        {
            days = Integer.parseInt(dayss);
        } catch (Exception e){
            days = 1;
        }
        Date endDate = DateUtils.addDays(startDate, days);
        endDate = CalendarUtils.addDays(endDate, -1);
        endDate = CalendarUtils.endOfDay(endDate);
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            List<ClientDtisznDiscountInfo> clientDtisznDiscountInfoList = DAOUtils
                    .getCategoryDiscountListWithEndBenefitBeetwenDates(session, startDate, endDate);
            for (ClientDtisznDiscountInfo clientDtisznDiscountInfo: clientDtisznDiscountInfoList) {

                Client client = clientDtisznDiscountInfo.getClient();
                String[] values = new String[]{
                        DATE_END_DISCOUNT,  CalendarUtils.dateToString(clientDtisznDiscountInfo.getDateEnd()),
                        DTISZN_CODE, clientDtisznDiscountInfo.getDtisznCode().toString(),
                        DTISZN_DESCRIPTION, clientDtisznDiscountInfo.getDtisznDescription(),
                        ID_DISCOUNT_INFO, clientDtisznDiscountInfo.getIdOfClientDTISZNDiscountInfo().toString()};
                values = EventNotificationService.attachGenderToValues(client.getGender(), values);
                if (forTest)
                    values = attachValue(values, "TEST", "true");
                List<Client> guardians = findGuardiansByClient(session, client.getIdOfClient(), null);
                if (!(guardians == null || guardians.isEmpty())) {
                    //Оправка всем представителям
                    for (Client destGuardian : guardians) {
                        RuntimeContext.getAppContext().getBean(EventNotificationService.class)
                                .sendNotification(destGuardian, client,
                                        EventNotificationService.NOTIFICATION_END_BENEFIT, values, new Date());
                    }
                }
                else
                {
                    //Отправка только клиенту
                    RuntimeContext.getAppContext().getBean(EventNotificationService.class)
                            .sendNotification(client, null,
                                    EventNotificationService.NOTIFICATION_END_BENEFIT, values, startDate);
                }
            }
        } catch (Exception e) {
            logger.error("Error notificatin end benefit", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }

    }

    private String[] attachValue(String[] values, String name, String value) {
        String[] newValues = new String[values.length + 2];
        System.arraycopy(values, 0, newValues, 0, values.length);
        newValues[newValues.length-2] = name;
        newValues[newValues.length-1] = value;
        return newValues;
    }
}
