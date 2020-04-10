/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.Staff;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

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
    private boolean checkFirst;
    private boolean firstEnterFound;
    private Long enterTime;
    //private String enterMethod;
    private Long enterGuardianId;
    private Long enterEmployeeId;
    private Integer enterPassDirection;
    private Long exitTime;
    //private String exitMethod;
    private Long exitEmployeeId;
    private Long exitGuardianId;

    @PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManager;

    final String JOB_NAME_END_BENEFIT="NotificationEndBenefit";
    final String JOB_NAME_PREFERENTIAL_FOOD="NotificationPreferentialFood";

    public class NotificationEndBenefit implements Job {
        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            RuntimeContext.getAppContext().getBean(BenefitService.class).runEndBenefit();
        }
    }
    public class NotificationPreferentialFood implements Job {
        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            RuntimeContext.getAppContext().getBean(BenefitService.class).runPreferentialFood();
        }
    }

    public void scheduleSync() throws Exception {
        String syncScheduleEndBenefit = RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.notification.client.endBenefit", "");
        String syncSchedulePreferentialFood = RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.notification.client.preferentialFood", "");
        if (syncScheduleEndBenefit.equals("") && syncSchedulePreferentialFood.equals("")) {
            return;
        }
        try {
            JobDetail jobDetailEndBenefit = new JobDetail(JOB_NAME_END_BENEFIT, Scheduler.DEFAULT_GROUP, NotificationEndBenefit.class);
            JobDetail jobDetailPreferentialFood = new JobDetail(JOB_NAME_PREFERENTIAL_FOOD, Scheduler.DEFAULT_GROUP, NotificationPreferentialFood.class);

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
            if (!syncSchedulePreferentialFood.equals("")) {
                CronTrigger triggerPreferentialFood = new CronTrigger(JOB_NAME_PREFERENTIAL_FOOD, Scheduler.DEFAULT_GROUP);
                triggerPreferentialFood.setCronExpression(syncSchedulePreferentialFood);
                if (scheduler.getTrigger(JOB_NAME_PREFERENTIAL_FOOD, Scheduler.DEFAULT_GROUP)!=null) {
                    scheduler.deleteJob(JOB_NAME_PREFERENTIAL_FOOD, Scheduler.DEFAULT_GROUP);
                }
                scheduler.scheduleJob(jobDetailPreferentialFood, triggerPreferentialFood);
            }
            scheduler.start();
        } catch(Exception e) {
            //logger.error("Failed to schedule notification summary calculation service job:", e);
        }
    }

    public void runEndBenefit() {
        Date startDate = new Date(System.currentTimeMillis());
        Date endDate = DateUtils.addDays(startDate, -1);

        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            List<ClientDtisznDiscountInfo> clientDtisznDiscountInfoList =
                    DAOUtils.getCategoryDiscountListWithEndBenefitBeetwenDates(session, startDate, endDate);
            for (ClientDtisznDiscountInfo clientDtisznDiscountInfo: clientDtisznDiscountInfoList) {

                Client client = clientDtisznDiscountInfo.getClient();

                String[] values = new String[]{
                        "surname", client.getPerson().getSurname(),
                        "name", client.getPerson().getFirstName(),
                        "surname", client.getPerson().getSurname(),
                        "OrgName", client.getOrg().getShortName(),
                        "account", client.getContractId().toString(),
                        "DateEndDiscount",  CalendarUtils.dateToString(clientDtisznDiscountInfo.getDateEnd()),
                        "DtisznCode", clientDtisznDiscountInfo.getDtisznCode().toString(),
                        "DtisznDescription", clientDtisznDiscountInfo.getDtisznDescription()};
                values = EventNotificationService.attachGenderToValues(client.getGender(), values);

                RuntimeContext.getAppContext().getBean(EventNotificationService.class)
                        .sendNotification(client, null,
                                EventNotificationService.NOTIFICATION_END_BENEFIT, values, startDate);
            }



        } catch (Exception e) {
            logger.error("Error  ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }

    }

    public void runPreferentialFood() {
        Date today = new Date(System.currentTimeMillis());
        Date[] dates = CalendarUtils.getCurrentWeekBeginAndEnd(today);
        Date startDate = CalendarUtils.truncateToDayOfMonth(dates[0]);
        Date endDate = CalendarUtils.endOfDay(dates[1]);
    }

}
