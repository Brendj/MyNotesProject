/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVService;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ApplicationForFoodProcessingService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ApplicationForFoodProcessingService.class);

    public static final String CRON_EXPRESSION_PROPERTY = "ecafe.processor.application.food.processing.cronExpression";
    public static final String NODE_PROPERTY = "ecafe.processor.application.food.processing.node";
    public static final String TRIGGER_DAY_COUNT = "ecafe.processor.application.food.processing.days";
    public static final String TRIGGER_DAY_COUNT_DEFAULT_VALUE = "5";

    public void run() throws Exception {
        if (!isOn())
            return;
        runTask();
    }

    public static boolean isOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getConfigProperties().getProperty(NODE_PROPERTY, "1");
        if (StringUtils.isBlank(instance) || StringUtils.isBlank(reqInstance) || !instance.trim().equals(
                reqInstance.trim())) {
            return false;
        }
        return true;
    }

    public void runTask() throws Exception {
        RuntimeContext context = RuntimeContext.getInstance();
        Session session = null;
        Transaction transaction = null;
        try {
            logger.info("Start processing applications for food for 1060 status");

            session = context.createPersistenceSession();
            transaction = session.beginTransaction();

            ApplicationForFoodStatus pausedStatus = new ApplicationForFoodStatus(ApplicationForFoodState.PAUSED, null);

            List<ApplicationForFood> applicationForFoodList =
                    DAOUtils.getApplicationForFoodListByStatus(session, pausedStatus, true);
            logger.info(String.format("%d applications was found", applicationForFoodList.size()));

            Integer daysCount;
            try {
                daysCount = Integer.parseInt(
                        context.getConfigProperties().getProperty(TRIGGER_DAY_COUNT, TRIGGER_DAY_COUNT_DEFAULT_VALUE));
            } catch (NumberFormatException e) {
                logger.error("Incorrect config value for days count - " +
                        context.getConfigProperties().getProperty(TRIGGER_DAY_COUNT, ""));
                daysCount = Integer.parseInt(TRIGGER_DAY_COUNT_DEFAULT_VALUE);
            }

            // first key - idOfOrg, second - created date of status 1060
            Map<Long, Map<Date, Date>> dayTriggerHash = new HashMap<Long, Map<Date, Date>>();

            Date fireDate = new Date();

            ApplicationForFoodStatus resumeStatus = new ApplicationForFoodStatus(ApplicationForFoodState.RESUME, null);
            ApplicationForFoodStatus deniedStatus = new ApplicationForFoodStatus(ApplicationForFoodState.DENIED,
                    ApplicationForFoodDeclineReason.NO_DOCS);

            ETPMVService service = RuntimeContext.getAppContext().getBean(ETPMVService.class);

            for (ApplicationForFood application : applicationForFoodList) {
                if (null == transaction || !transaction.isActive()) {
                    transaction = session.beginTransaction();
                }
                Org clientOrg = application.getClient().getOrg();
                Date statusCreatedDate = null;
                for (ApplicationForFoodHistory history : application.getApplicationForFoodHistories()) {
                    if (history.getStatus().equals(pausedStatus)) {
                        statusCreatedDate = history.getCreatedDate();
                        break;
                    }
                }

                if (!dayTriggerHash.containsKey(clientOrg.getIdOfOrg())) {
                    dayTriggerHash.put(clientOrg.getIdOfOrg(), new HashMap<Date, Date>());
                }

                //dayTriggerHash.get(clientOrg.getIdOfOrg()).put(statusCreatedDate,
                //        getTriggerDateForOrg(session, clientOrg, statusCreatedDate, daysCount, application.getClient().getIdOfClientGroup()));
                dayTriggerHash.get(clientOrg.getIdOfOrg()).put(statusCreatedDate,
                        getTriggerDateByProductionCalendar(session, statusCreatedDate, daysCount));
                Date triggerDate = dayTriggerHash.get(clientOrg.getIdOfOrg()).get(statusCreatedDate);
                if (triggerDate.getTime() <= fireDate.getTime()) {
                    Long applicationVersion = DAOUtils.nextVersionByApplicationForFood(session);
                    Long historyVersion = DAOUtils.nextVersionByApplicationForFoodHistory(session);

                    application = DAOUtils.updateApplicationForFoodWithVersion(session, application, resumeStatus, applicationVersion,
                            historyVersion);
                    service.sendStatusAsync(System.currentTimeMillis(), application.getServiceNumber(),
                            application.getStatus().getApplicationForFoodState(),
                            application.getStatus().getDeclineReason());
                    application = DAOUtils.updateApplicationForFoodWithVersion(session, application, deniedStatus, applicationVersion,
                            historyVersion);
                    service.sendStatusAsync(System.currentTimeMillis(), application.getServiceNumber(),
                            application.getStatus().getApplicationForFoodState(),
                            application.getStatus().getDeclineReason());
                }

                transaction.commit();
                transaction = null;
            }

            if (null != transaction && transaction.isActive()) {
                transaction.commit();
                transaction = null;
            }
            logger.info("End of processing applications for food for 1060 status");
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public Date getTriggerDateByProductionCalendar(Session session, Date statusCreatedDate, Integer daysCount) {
        Integer _daysCounter = daysCount;
        Date _startDate = CalendarUtils.truncateToDayOfMonth(statusCreatedDate);
        Date specialDaysMonth = CalendarUtils.addMonth(_startDate, 1);

        Criteria productionCalendarCriteria = session.createCriteria(ProductionCalendar.class);
        productionCalendarCriteria.add(Restrictions.between("day", _startDate, specialDaysMonth));
        List<ProductionCalendar> dates = productionCalendarCriteria.list();
        do {
            _startDate = CalendarUtils.addOneDay(_startDate);

            Date endDateStart = CalendarUtils.startOfDay(_startDate);
            Date endDateEnd = CalendarUtils.endOfDay(_startDate);
            Boolean isWeekend = false;

            for (ProductionCalendar pc : dates) {
                if (CalendarUtils.betweenDate(pc.getDay(), endDateStart, endDateEnd)) {
                    isWeekend = true;
                    break;
                }
            }

            if (!isWeekend) {
                _daysCounter--;
            }
        } while(_daysCounter >= 0);
        return _startDate;
    }

    /*private Date getTriggerDateForOrg(Session session, Org org, Date statusCreatedDate, Integer daysCount, Long idOfClientGroup) {
        Integer _daysCounter = daysCount;

        Date _startDate = CalendarUtils.truncateToDayOfMonth(statusCreatedDate);
        Date specialDaysMonth = CalendarUtils.addMonth(_startDate, 1);

        Criteria specialDaysCriteria = session.createCriteria(SpecialDate.class);
        specialDaysCriteria.add(Restrictions.eq("idOfOrg", org.getIdOfOrg()));
        //specialDaysCriteria.add(Restrictions.eq("isWeekend", Boolean.TRUE));
        specialDaysCriteria.add(Restrictions.eq("deleted", Boolean.FALSE));
        specialDaysCriteria.add(Restrictions.between("date", _startDate, specialDaysMonth));
        specialDaysCriteria.add(Restrictions.or(Restrictions.eq("idOfClientGroup", idOfClientGroup),
                Restrictions.isNull("idOfClientGroup")));
        specialDaysCriteria.setProjection(Projections.projectionList()
                .add(Projections.property("date"))
                .add(Projections.property("idOfClientGroup"))
                .add(Projections.property("isWeekend")));

        List specialDates = specialDaysCriteria.list();

        do {
            _startDate = CalendarUtils.addOneDay(_startDate);

            Date endDateStart = CalendarUtils.startOfDay(_startDate);
            Date endDateEnd = CalendarUtils.endOfDay(_startDate);
            Boolean isWeekend = null;

            //check special dates
            for (Object specialDate : specialDates) {
                Object[] vals = (Object[]) specialDate;
                if (null == specialDate)
                    continue;
                if (CalendarUtils.betweenDate((Date) vals[0], endDateStart, endDateEnd)) {
                    isWeekend = (Boolean) vals[2];
                    break;
                }
            }

            if (null == isWeekend) {
                //check weekend
                isWeekend = !CalendarUtils.isWorkDateWithoutParser(false, _startDate);
            }

            if (!isWeekend) {
                _daysCounter--;
            }
        } while(_daysCounter >= 0);

        return _startDate;
    }*/

    public void scheduleSync() throws Exception {
        String syncSchedule = RuntimeContext
                .getInstance().getConfigProperties().getProperty(CRON_EXPRESSION_PROPERTY, "");
        if (syncSchedule.equals("")) {
            return;
        }
        try {
            logger.info("Scheduling application for food processing service job: " + syncSchedule);
            JobDetail job = new JobDetail("ApplicationForFoodProcessing", Scheduler.DEFAULT_GROUP,
                    ApplicationForFoodProcessingJob.class);

            SchedulerFactory sfb = new StdSchedulerFactory();
            Scheduler scheduler = sfb.getScheduler();
            if (!syncSchedule.equals("")) {
                CronTrigger trigger = new CronTrigger("ApplicationForFoodProcessing", Scheduler.DEFAULT_GROUP);
                trigger.setCronExpression(syncSchedule);
                if (scheduler.getTrigger("ApplicationForFoodProcessing", Scheduler.DEFAULT_GROUP) != null) {
                    scheduler.deleteJob("ApplicationForFoodProcessing", Scheduler.DEFAULT_GROUP);
                }
                scheduler.scheduleJob(job, trigger);
            }
            scheduler.start();
        } catch(Exception e) {
            logger.error("Failed to schedule application for food processing service job:", e);
        }
    }

    public static class ApplicationForFoodProcessingJob implements Job {
        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            try {
                RuntimeContext.getAppContext().getBean(ApplicationForFoodProcessingService.class).run();
            } catch (JobExecutionException e) {
                throw e;
            } catch (Exception e) {
                logger.error("Failed to run application for food processing service job:", e);
            }
        }
    }
}
