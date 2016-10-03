/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
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
public class SummaryCalculationService {
    Logger logger = LoggerFactory.getLogger(SummaryCalculationService.class);
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

    public static String VALUE_AMOUNT_ENTER_EVENTS_TIME_MAX = "amountEntereventsTime";
    public static String[] VALUES_ENTER_EVENTS = {"amountEntereventsTimeMonday", "amountEntereventsTimeTuesday", "amountEntereventsTimeWednesday",
                                                  "amountEntereventsTimeThursday", "amountEntereventsTimeFriday", "amountEntereventsTimeSaturday", "amountEntereventsTimeSunday",
                                                  "startTimeMonday", "startTimeTuesday", "startTimeWednesday",
                                                  "startTimeThursday", "startTimeFriday", "startTimeSaturday", "startTimeSunday",
                                                  "endTimeMonday", "endTimeTuesday", "endTimeWednesday",
                                                  "endTimeThursday", "endTimeFriday", "endTimeSaturday", "endTimeSunday" };
    public static String VALUE_TIME_ENTER = "StartTime";
    public static String VALUE_TIME_EXIT = "EndTime";
    public static String VALUE_ACCOUNT = "account";
    public static String VALUE_BALANCE_START_DATE = "balanceStartdate";
    public static String VALUE_BALANCE = "balance";
    public static String VALUE_AMOUNT_BUY_ALL = "amountBuyAll";
    public static String VALUE_LIMIT = "limit";
    public static String VALUE_AMOUNT_COMPLEX_DATE = "amountComplexDate";
    public static String VALUE_ORG_NAME = "OrgName";
    public static String VALUE_ORG_TYPE = "OrgType";
    public static String VALUE_ORG_ID = "OrgId";
    public static String VALUE_ORG_NUM = "OrgNum";
    public static String VALUE_METHOD_START = "Method_Enterevents_StartTime";
    public static String VALUE_METHOD_END = "Method_Enterevents_EndTime";
    public static String VALUE_AMOUNT_ENTER_EVENTS_DATE = "amountEntereventsDate";
    public static String VALUE_QUANTITY_AMOUNT = "quantityamount";
    public static String VALUE_PAYMENT_SUM = "PaymentSum";
    public static String VALUE_MENU_DETAIL = "menuDetail";
    public static String VALUE_BALANCE_DAYS = "balanceOnDays";

    final static String JOB_NAME_DAILY="NotificationSummaryDaily";
    final static String JOB_NAME_WEEKLY="NotificationSummaryWeekly";

    final static Integer MILLIS_IN_DAY = 60*60*24*1000;

    public static class NotificationJobDaily implements Job {
        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            RuntimeContext.getAppContext().getBean(SummaryCalculationService.class).runDaily();
        }
    }
    public static class NotificationJobWeekly implements Job {
        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            RuntimeContext.getAppContext().getBean(SummaryCalculationService.class).runWeekly();
        }
    }

    public void scheduleSync() throws Exception {
        String syncScheduleDaily = RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.notification.summary.daily.cronExpression", "");
        String syncScheduleWeekly = RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.notification.summary.weekly.cronExpression", "");
        if (syncScheduleDaily.equals("") && syncScheduleWeekly.equals("")) {
            return;
        }
        try {
            logger.info("Scheduling notification summary calculation service job: "+syncScheduleDaily);
            JobDetail jobDetailDaily = new JobDetail(JOB_NAME_DAILY, Scheduler.DEFAULT_GROUP, NotificationJobDaily.class);
            JobDetail jobDetailWeekly = new JobDetail(JOB_NAME_WEEKLY, Scheduler.DEFAULT_GROUP, NotificationJobWeekly.class);

            SchedulerFactory sfb = new StdSchedulerFactory();
            Scheduler scheduler = sfb.getScheduler();
            if (!syncScheduleDaily.equals("")) {
                CronTrigger triggerDaily = new CronTrigger(JOB_NAME_DAILY, Scheduler.DEFAULT_GROUP);
                triggerDaily.setCronExpression(syncScheduleDaily);
                if (scheduler.getTrigger(JOB_NAME_DAILY, Scheduler.DEFAULT_GROUP)!=null) {
                    scheduler.deleteJob(JOB_NAME_DAILY, Scheduler.DEFAULT_GROUP);
                }
                scheduler.scheduleJob(jobDetailDaily, triggerDaily);
            }
            if (!syncScheduleWeekly.equals("")) {
                CronTrigger triggerWeekly = new CronTrigger(JOB_NAME_WEEKLY, Scheduler.DEFAULT_GROUP);
                triggerWeekly.setCronExpression(syncScheduleWeekly);
                if (scheduler.getTrigger(JOB_NAME_WEEKLY, Scheduler.DEFAULT_GROUP)!=null) {
                    scheduler.deleteJob(JOB_NAME_WEEKLY, Scheduler.DEFAULT_GROUP);
                }
                scheduler.scheduleJob(jobDetailWeekly, triggerWeekly);
            }
            scheduler.start();
        } catch(Exception e) {
            logger.error("Failed to schedule notification summary calculation service job:", e);
        }
    }

    public void run(Date startDate, Date endDate, Long notyfyType) {
        if (!isOn()) {
            return;
        }
        try {
            logger.info("Start summary calculation notification");
            final EventNotificationService notificationService = RuntimeContext.getAppContext().getBean(
                    EventNotificationService.class);
            List<ClientEE> clients = generateNotificationParams(startDate, endDate, notyfyType);
            EntityManager em = entityManager.getEntityManagerFactory().createEntityManager();
            Session session = em.unwrap(Session.class);

            for (ClientEE clientEE : clients) {
                Client client = (Client)session.load(Client.class, clientEE.getIdOfClient());
                String type = "";
                int notificationType = 0;
                if (notyfyType.equals(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_DAY.getValue())) {
                    type = EventNotificationService.NOTIFICATION_SUMMARY_BY_DAY;
                    notificationType = ClientSms.TYPE_SUMMARY_DAILY_NOTIFICATION;
                } else if (notyfyType.equals(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_WEEK.getValue())) {
                    type = EventNotificationService.NOTIFICATION_SUMMARY_BY_WEEK;
                    notificationType = ClientSms.TYPE_SUMMARY_WEEKLY_NOTIFICATION;
                }
                notificationService.sendNotificationSummary(client,
                        type, clientEE.getValues(),
                        new Date(System.currentTimeMillis()), notificationType);
            }
            logger.info("End summary calculation notification");
        } catch(Exception e) {
            logger.error("Error sending summary notification: ", e);
        }
    }

    public void runDaily() {
        Date today = new Date(System.currentTimeMillis());
        Date endDate = CalendarUtils.endOfDay(today);
        Date startDate = CalendarUtils.truncateToDayOfMonth(today);
        run(startDate, endDate, ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_DAY.getValue());
    }

    public void runWeekly() {
        Date today = new Date(System.currentTimeMillis());
        Date[] dates = CalendarUtils.getCurrentWeekBeginAndEnd(today);
        Date startDate = CalendarUtils.truncateToDayOfMonth(dates[0]);
        Date endDate = CalendarUtils.endOfDay(dates[1]);
        run(startDate, endDate, ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_WEEK.getValue());
    }

    public static boolean isOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getConfigProperties().getProperty("ecafe.processor.notification.summary.node", "1");
        if (StringUtils.isBlank(instance) || StringUtils.isBlank(reqInstance) || !instance.trim().equals(
                reqInstance.trim())) {
            return false;
        }
        return true;
    }

    public List<ClientEE> generateNotificationParams(Date startDate, Date endDate, Long notifyType) {
        String[] result = new String[2 * 2];
        String sDate = CalendarUtils.dateToString(startDate);
        result[0] = "Startdate";
        result[1] = sDate;
        String eDate = CalendarUtils.dateToString(endDate);
        result[2] = "Enddate";
        result[3] = eDate;

        //Подсчет данных по событиям проходов. Первый запрос - старая таблица уведомлений, второй - новая
        String query_ee = "SELECT c.idofclient, p.surname, p.firstname, "
                + "coalesce(e.evtdatetime, -1) as evtdatetime, coalesce(e.passdirection, -1) as passdirection, "
                + "coalesce(e.guardianid, -1) as guardianId, coalesce(e.childpasschecker, -1) as childpasschecker, "
                + "coalesce(e.childpasscheckerid, -1) as childpasscheckerId, o.shortnameinfoservice, o.organizationtype, o.idoforg "
                + "FROM cf_clientsnotificationsettings n inner join cf_clients c on c.idofclient = n.idofclient "
                + "INNER JOIN cf_persons p ON c.idofperson = p.idofperson "
                + "INNER JOIN cf_orgs o on c.idoforg = o.idoforg "
                + "LEFT OUTER JOIN cf_enterevents e ON c.idofclient = e.idofclient "
                + "AND e.evtdatetime BETWEEN :startTime AND :endTime "
                + "WHERE n.notifytype = :notifyType "
                + "and not exists (select * from cf_client_guardian cg2 where cg2.idofchildren = c.idofclient and cg2.deletedState = false) "
                + " UNION "
                + "SELECT c.idofclient, p.surname, p.firstname, "
                + "coalesce(e.evtdatetime, -1) as evtdatetime, coalesce(e.passdirection, -1) as passdirection, "
                + "coalesce(e.guardianid, -1) as guardianId, coalesce(e.childpasschecker, -1) as childpasschecker, "
                + "coalesce(e.childpasscheckerid, -1) as childpasscheckerId, o.shortnameinfoservice, o.organizationtype, o.idoforg "
                + "from cf_client_guardian_notificationsettings n inner join cf_client_guardian cg on cg.idofclientguardian = n.idofclientguardian "
                + "inner join cf_clients c on c.idofclient = cg.idofchildren "
                + "INNER JOIN cf_persons p ON c.idofperson = p.idofperson "
                + "INNER JOIN cf_orgs o on c.idoforg = o.idoforg "
                + "LEFT OUTER JOIN cf_enterevents e ON c.idofclient = e.idofclient "
                + "AND e.evtdatetime BETWEEN :startTime AND :endTime "
                + "WHERE n.notifytype = :notifyType "
                + "ORDER BY idofclient, surname, firstname, evtdatetime ";
        Query equery = entityManager.createNativeQuery(query_ee);
        equery.setParameter("notifyType", notifyType);
        equery.setParameter("startTime", startDate.getTime());
        equery.setParameter("endTime", endDate.getTime());
        List list = equery.getResultList();
        Long idOfClient = -1L;
        List<ClientEE> clients = new ArrayList<ClientEE>();
        ClientEE clientEE = null;
        for (Object obj : list) {
            Object[] row = (Object[]) obj;
            Long id = ((BigInteger)row[0]).longValue();
            String surname = (String)row[1];
            String firstname = (String)row[2];
            if (!id.equals(idOfClient)) {
                clientEE = new ClientEE();
                clientEE.setValues(result.clone());
                idOfClient = id;
                clients.add(clientEE);
            }
            clientEE.setIdOfClient(id);
            clientEE.setSurname(surname);
            clientEE.setFirstname(firstname);
            Long evtDateTime = ((BigInteger)row[3]).longValue();
            if (evtDateTime > -1L) {
                EE ee = new EE();
                ee.setEvtDateTime(evtDateTime);
                ee.setPassDirection(((Integer)row[4]).intValue());
                ee.setGuardianId(((BigInteger)row[5]).longValue());
                if (ee.getGuardianId().equals(-1L))
                    ee.setGuardianId(null);
                ee.setChildPassChecker(((Integer)row[6]).intValue());
                ee.setChildPassCheckerId(((BigInteger)row[7]).longValue());
                if (ee.getChildPassCheckerId().equals(-1L)) {
                    ee.setChildPassCheckerId(null);
                }
                clientEE.getEeList().add(ee);
            }
            clientEE.setOrgName((String)row[8]);
            clientEE.setOrgType(OrganizationType.fromInteger(((Integer)row[9]).intValue()).toString());
            clientEE.setOrgId(((BigInteger)row[10]).longValue());
            clientEE.setOrgNum(Org.extractOrgNumberFromName(clientEE.getOrgName()));
        }

        attachEEValues(clients, notifyType, startDate, endDate);

        //Подсчет значений для детализации меню в случае уведомления по итогам дня
        Map menuMap = new TreeMap<Long, String>();
        if (notifyType.equals(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_DAY.getValue())) {
            String query_menu =
                    "select c.idofclient, od.qty, od.rprice, od.menudetailname from cf_clients c inner join cf_orders o on c.idofclient = o.idofclient "
                    + "inner join cf_orderdetails od on o.idoforder = od.idoforder and o.idoforg = od.idoforg "
                    + "inner join cf_clientsnotificationsettings n on c.idofclient = n.idofclient "
                    + "where n.notifyType = :notifyType AND o.createddate BETWEEN :startTime AND :endTime "
                    + "and not exists (select * from cf_client_guardian cg2 where cg2.idofchildren = c.idofclient and cg2.deletedState = false) "
                    + " UNION "
                    + "select cg.idofguardian, od.qty, od.rprice, od.menudetailname "
                    + "from cf_client_guardian_notificationsettings n inner join cf_client_guardian cg on cg.idofclientguardian = n.idofclientguardian "
                    + "inner join cf_clients c on c.idofclient = cg.idofchildren "
                    + "inner join cf_orders o on o.idofclient = c.idofclient "
                    + "inner join cf_orderdetails od on o.idoforder = od.idoforder and o.idoforg = od.idoforg "
                    + "where n.notifyType = :notifyType AND o.createddate BETWEEN :startTime AND :endTime "
                    + "order by idofclient, menudetailname";
            Query mquery = entityManager.createNativeQuery(query_menu);
            mquery.setParameter("notifyType", notifyType);
            mquery.setParameter("startTime", startDate.getTime());
            mquery.setParameter("endTime", endDate.getTime());
            List mlist = mquery.getResultList();

            //String menu_name = "";
            for (Object obj : mlist) {
                Object[] row = (Object[]) obj;
                Long id = ((BigInteger)row[0]).longValue();
                //if (id.equals(21175L)) {
                //    logger.info("pp");
                //}
                Integer qty = ((Integer)row[1]).intValue();
                Long rprice = ((BigInteger)row[2]).longValue();
                Long sum = qty * rprice;
                String menu = (String)row[3];
                String menu_name = (String)menuMap.get(id);
                menu_name = (menu_name == null ? "" : menu_name);
                menu_name += "%" + menu + " " + (qty > 1 ? String.format("(%s шт.) ", qty) : "") + sum / 100 + " руб.";
                if (sum % 100 != 0) {
                    menu_name += " " + sum % 100 + " коп.";
                }
                menu_name += "%";
                menuMap.put(id, menu_name);
            }
        }
        //

        //Подсчет данных по балансам
        String query_balance = "SELECT c.idofclient, p.surname, p.firstname, c.expenditurelimit, c.balance, coalesce(query1.sum1, 0) as sum1, coalesce(query2.sum2, 0) AS sum2, "
                + "c.contractid, coalesce(query3.count1, 0) as count1, coalesce(query4.sum3, 0) as sum3, coalesce(query5.count2, 0) as count2, coalesce(query5.sum4, 0) as sum4 "
                + "FROM cf_clientsnotificationsettings n INNER JOIN cf_clients c ON c.idofclient = n.idofclient INNER JOIN cf_persons p ON c.idofperson = p.idofperson "
                + "INNER JOIN "
                + "(SELECT c.idofclient, sum(t1.transactionsum) AS sum1 FROM cf_clientsnotificationsettings n INNER JOIN cf_clients c ON c.idofclient = n.idofclient "
                + "LEFT JOIN cf_transactions t1 ON t1.idofclient = c.idofclient AND t1.transactionDate BETWEEN :startTime AND :curTime "
                + "WHERE n.notifytype = :notifyType and not exists (select * from cf_client_guardian cg2 where cg2.idofchildren = c.idofclient and cg2.deletedState = false) "
                + "GROUP BY c.idofclient) AS query1 ON c.idofclient = query1.idofclient "
                + "INNER JOIN "
                + "(SELECT c.idofclient, sum(-t2.transactionsum) AS sum2 FROM cf_clientsnotificationsettings n INNER JOIN cf_clients c ON c.idofclient = n.idofclient "
                + "LEFT JOIN cf_transactions t2 ON t2.idofclient = c.idofclient AND t2.transactionDate BETWEEN :startTime AND :endTime AND (t2.sourcetype = :transactionType1 "
                + "OR t2.sourcetype = :transactionType2) WHERE n.notifytype = :notifyType and not exists (select * from cf_client_guardian cg2 where cg2.idofchildren = c.idofclient and cg2.deletedState = false) "
                + "GROUP BY c.idofclient) AS query2 ON c.idofclient = query2.idofclient "
                + "INNER JOIN "
                + "(SELECT c.idofclient, count(distinct extract(day from TO_TIMESTAMP(o.createddate / 1000))) AS count1 FROM cf_clientsnotificationsettings n "
                + "INNER JOIN cf_clients c ON c.idofclient = n.idofclient "
                + "LEFT JOIN cf_orders o ON o.idofclient = c.idofclient AND o.createddate BETWEEN :startTime AND :endTime AND o.ordertype IN (:orderTypes) "
                + "AND o.state = :orderState WHERE n.notifytype = :notifyType and not exists (select * from cf_client_guardian cg2 where cg2.idofchildren = c.idofclient and cg2.deletedState = false) "
                + "GROUP BY c.idofclient) AS query3 ON c.idofclient = query3.idofclient "
                + "INNER JOIN "
                + "(SELECT c.idofclient, sum(t.transactionsum) AS sum3 FROM cf_clientsnotificationsettings n INNER JOIN cf_clients c ON c.idofclient = n.idofclient "
                + "LEFT OUTER JOIN cf_transactions t ON t.idofclient = c.idofclient AND t.transactionDate >= :endTime AND t.transactionDate <= :curTime "
                + "WHERE n.notifytype = :notifyType and not exists (select * from cf_client_guardian cg2 where cg2.idofchildren = c.idofclient and cg2.deletedState = false) "
                + "GROUP BY c.idofclient) AS query4 ON c.idofclient = query4.idofclient "
                + "INNER JOIN "
                + "(SELECT c.idofclient, count(idofclientpayment) as count2, sum(paysum) as sum4 FROM cf_clientsnotificationsettings n INNER JOIN cf_clients c ON c.idofclient = n.idofclient "
                + "LEFT OUTER JOIN cf_transactions t ON t.idofclient = c.idofclient AND t.transactionDate BETWEEN :startTime AND :endTime "
                + "LEFT OUTER JOIN cf_clientpayments p ON p.idoftransaction = t.idoftransaction AND p.createddate BETWEEN :startTime AND :endTime "
                + "WHERE n.notifytype = :notifyType and not exists (select * from cf_client_guardian cg2 where cg2.idofchildren = c.idofclient and cg2.deletedState = false) "
                + "GROUP BY c.idofclient) AS query5 ON c.idofclient = query5.idofclient "
                + "WHERE n.notifytype = :notifyType "
                + "and not exists (select * from cf_client_guardian cg2 where cg2.idofchildren = c.idofclient and cg2.deletedState = false) "
                + " UNION "
                + "SELECT c.idofclient, p.surname, p.firstname, c.expenditurelimit, c.balance, coalesce(query1.sum1, 0) as sum1, coalesce(query2.sum2, 0) AS sum2, "
                + "c.contractid, coalesce(query3.count1, 0) as count1, coalesce(query4.sum3, 0) as sum3, coalesce(query5.count2, 0) as count2, coalesce(query5.sum4, 0) as sum4 "
                + "FROM cf_client_guardian_notificationsettings n INNER JOIN cf_client_guardian cg on n.idofclientguardian = cg.idofclientguardian "
                + "inner join cf_clients c ON c.idofclient = cg.idofchildren INNER JOIN cf_persons p ON c.idofperson = p.idofperson "
                + "INNER JOIN "
                + "(SELECT c.idofclient, cg.idofguardian, sum(t1.transactionsum) AS sum1 FROM cf_client_guardian_notificationsettings n "
                + "inner join cf_client_guardian cg on n.idofclientguardian = cg.idofclientguardian "
                + "INNER JOIN cf_clients c ON c.idofclient = cg.idofchildren "
                + "LEFT JOIN cf_transactions t1 ON t1.idofclient = c.idofclient AND t1.transactionDate BETWEEN :startTime AND :curTime "
                + "WHERE n.notifytype = :notifyType GROUP BY c.idofclient, cg.idofguardian) AS query1 ON c.idofclient = query1.idofclient "
                + "INNER JOIN "
                + "(SELECT c.idofclient, cg.idofguardian, sum(-t2.transactionsum) AS sum2 FROM cf_client_guardian_notificationsettings n "
                + "inner join cf_client_guardian cg on n.idofclientguardian = cg.idofclientguardian "
                + "INNER JOIN cf_clients c ON c.idofclient = cg.idofchildren "
                + "LEFT JOIN cf_transactions t2 ON t2.idofclient = c.idofclient AND t2.transactionDate BETWEEN :startTime AND :endTime AND (t2.sourcetype = :transactionType1 "
                + "OR t2.sourcetype = :transactionType2) WHERE n.notifytype = :notifyType GROUP BY c.idofclient, cg.idofguardian) AS query2 ON c.idofclient = query2.idofclient "
                + "INNER JOIN "
                + "(SELECT c.idofclient, cg.idofguardian, count(distinct extract(day from TO_TIMESTAMP(o.createddate / 1000))) AS count1 FROM cf_client_guardian_notificationsettings n "
                + "inner join cf_client_guardian cg on n.idofclientguardian = cg.idofclientguardian "
                + "INNER JOIN cf_clients c ON c.idofclient = cg.idofchildren "
                + "LEFT JOIN cf_orders o ON o.idofclient = c.idofclient AND o.createddate BETWEEN :startTime AND :endTime AND o.ordertype IN (:orderTypes) "
                + "AND o.state = :orderState WHERE n.notifytype = :notifyType GROUP BY c.idofclient, cg.idofguardian) AS query3 ON c.idofclient = query3.idofclient "
                + "INNER JOIN "
                + "(SELECT c.idofclient, cg.idofguardian, sum(t.transactionsum) AS sum3 FROM cf_client_guardian_notificationsettings n "
                + "inner join cf_client_guardian cg on n.idofclientguardian = cg.idofclientguardian "
                + "INNER JOIN cf_clients c ON c.idofclient = cg.idofchildren "
                + "LEFT OUTER JOIN cf_transactions t ON t.idofclient = c.idofclient AND t.transactionDate >= :endTime AND t.transactionDate <= :curTime "
                + "WHERE n.notifytype = :notifyType GROUP BY c.idofclient, cg.idofguardian) AS query4 ON c.idofclient = query4.idofclient "
                + "INNER JOIN "
                + "(SELECT c.idofclient, cg.idofguardian, count(idofclientpayment) as count2, sum(paysum) as sum4 FROM cf_client_guardian_notificationsettings n "
                + "inner join cf_client_guardian cg on n.idofclientguardian = cg.idofclientguardian "
                + "INNER JOIN cf_clients c ON c.idofclient = cg.idofchildren "
                + "LEFT OUTER JOIN cf_transactions t ON t.idofclient = c.idofclient AND t.transactionDate BETWEEN :startTime AND :endTime "
                + "LEFT OUTER JOIN cf_clientpayments p ON p.idoftransaction = t.idoftransaction AND p.createddate BETWEEN :startTime AND :endTime "
                + "WHERE n.notifytype = :notifyType GROUP BY c.idofclient, cg.idofguardian) AS query5 ON c.idofclient = query5.idofclient "
                + "WHERE n.notifytype = :notifyType ORDER BY idofclient, surname, firstname";
        Query bquery = entityManager.createNativeQuery(query_balance);
        bquery.setParameter("notifyType", notifyType);
        bquery.setParameter("startTime", startDate.getTime());
        bquery.setParameter("endTime", endDate.getTime());
        bquery.setParameter("curTime", System.currentTimeMillis());
        bquery.setParameter("transactionType1", AccountTransaction.CLIENT_ORDER_TRANSACTION_SOURCE_TYPE);
        bquery.setParameter("transactionType2", AccountTransaction.CANCEL_TRANSACTION_SOURCE_TYPE);
        bquery.setParameter("orderState", Order.STATE_COMMITED);
        Set<Integer> oTypes = new HashSet<Integer>();
        oTypes.add(OrderTypeEnumType.PAY_PLAN.ordinal());
        oTypes.add(OrderTypeEnumType.REDUCED_PRICE_PLAN.ordinal());
        oTypes.add(OrderTypeEnumType.DAILY_SAMPLE.ordinal());
        oTypes.add(OrderTypeEnumType.REDUCED_PRICE_PLAN_RESERVE.ordinal());
        oTypes.add(OrderTypeEnumType.SUBSCRIPTION_FEEDING.ordinal());
        oTypes.add(OrderTypeEnumType.CORRECTION_TYPE.ordinal());
        bquery.setParameter("orderTypes", oTypes);
        List blist = bquery.getResultList();
        for (Object obj : blist) {
            Object[] row = (Object[]) obj;
            Long id = ((BigInteger)row[0]).longValue();
            String surname = (String)row[1];
            String firstname = (String)row[2];
            Long limit = ((BigInteger)row[3]).longValue();
            Long balance = ((BigInteger)row[4]).longValue();
            Long byTransStart = ((BigDecimal)row[5]).longValue();
            Long orderSum = ((BigDecimal)row[6]).longValue();
            Long contractId = ((BigInteger)row[7]).longValue();
            Long amountComplexDate = ((BigInteger)row[8]).longValue();
            Long byTransEnd = ((BigDecimal)row[9]).longValue();
            Long quantityAmount = ((BigInteger)row[10]).longValue();
            Long paymentSum = ((BigDecimal)row[11]).longValue();
            clientEE = findClientEEByClientId(clients, id);
            if (clientEE == null) {
                clientEE = new ClientEE();
                clientEE.setIdOfClient(id);
                clientEE.setSurname(surname);
                clientEE.setFirstname(firstname);
                clientEE.setValues(result.clone());
                clients.add(clientEE);
            }
            Balances balances = new Balances();
            balances.setAccount(contractId);
            balances.setBalance(balance - byTransEnd);
            balances.setBalanceStartDate(balance - byTransStart);
            balances.setLimit(limit);
            balances.setAmountBuyAll(orderSum);
            balances.setAmountComplexDate(amountComplexDate); // > 0 ? 1L : 0L);
            balances.setQuantityAmount(quantityAmount);
            balances.setPaymentSum(paymentSum);
            String menu = (String)menuMap.get(id);
            balances.setMenu_detail(menu == null ? "" : menu);
            Long balanceOnDays = null;
            if (orderSum > 0) {
                balanceOnDays = balances.getBalance() / orderSum;
                if (balanceOnDays < 0) balanceOnDays = 0L;
            }
            balances.setBalanceOnDays(balanceOnDays == null ? null : balanceOnDays.intValue());
            clientEE.setBalances(balances);
        }

        attachBalanceValues(clients);

        return clients;
    }

    private ClientEE findClientEEByClientId(List<ClientEE> list, Long id) {
        for (ClientEE client : list) {
            if (client.getIdOfClient().equals(id)) {
                return client;
            }
        }
        return null;
    }

    private void resetGlobals() {
        enterTime = 0L;
        enterGuardianId = null;
        enterEmployeeId = null;
        enterPassDirection = null;
        exitTime = 0L;
        exitGuardianId = null;
        exitEmployeeId = null;
        checkFirst = true;
        firstEnterFound = false;
    }

    private void attachEEValues(List<ClientEE> clients, Long notifyType, Date startDate, Date endDate) {
        EntityManager em = entityManager.getEntityManagerFactory().createEntityManager();
        Session session = em.unwrap(Session.class);
        Long seconds; Long hours; Long minutes;
        boolean enterExists = false;
        boolean exitExists = false;
        boolean inside = false;
        long eTime;
        long clientInside;
        Integer daysInside;
        for (ClientEE clientEE : clients) {
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_ORG_NAME, clientEE.getOrgName()));
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_ORG_TYPE, clientEE.getOrgType()));
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_ORG_ID, clientEE.getOrgId().toString()));
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_ORG_NUM, clientEE.getOrgNum()));

            int upto = 0;
            if (notifyType.equals(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_DAY.getValue())) {
                upto = 1;
            } else if (notifyType.equals(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_WEEK.getValue())) {
                upto = 7;
            }

            daysInside = 0;
            for (int iteration = 1; iteration <= upto; iteration++) {
                resetGlobals();
                clientInside = 0L;

                if (enterExists || exitExists) {
                    daysInside++;
                }

                eTime = 0L;
                enterExists = false;
                exitExists = false;
                inside = false;
                for (EE ee : clientEE.getEeList()) {
                    if (ee.getEvtDateTime() < startDate.getTime() + (iteration - 1) * MILLIS_IN_DAY) {
                        continue;
                    }
                    if (ee.getEvtDateTime() > startDate.getTime()  + iteration * MILLIS_IN_DAY) {
                        //перешли в следующий день
                        break;
                    }

                    if (!firstEnterFound) {
                        checkFirstEnter(ee); //здесь устанавливается время первого события входа
                    }
                    if (ee.getPassDirection() == EnterEvent.ENTRY || ee.getPassDirection() == EnterEvent.RE_ENTRY
                            || ee.getPassDirection() == EnterEvent.DETECTED_INSIDE || ee.getPassDirection() == EnterEvent.CHECKED_BY_TEACHER_EXT) {
                        enterExists = true; //существует вход в здание
                        inside = true;
                        eTime = ee.getEvtDateTime();
                    }
                    if (ee.getPassDirection() == EnterEvent.EXIT || ee.getPassDirection() == EnterEvent.RE_EXIT) {
                        exitExists = true;//существует выход из здания
                        if (inside) {
                            clientInside += (ee.getEvtDateTime() - eTime);
                            eTime = 0L;
                        }
                        inside = false;
                        exitTime = ee.getEvtDateTime();
                        exitEmployeeId = ee.getChildPassCheckerId();
                        exitGuardianId = ee.getGuardianId();
                    }
                }
                if (notifyType.equals(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_DAY.getValue())) {
                    if (!enterExists && !exitExists) {
                        clientEE.setValues(attachValue(clientEE.getValues(), VALUE_AMOUNT_ENTER_EVENTS_DATE, "0"));
                        clientEE.setValues(attachValue(clientEE.getValues(), VALUE_TIME_ENTER, "-"));
                        clientEE.setValues(attachValue(clientEE.getValues(), VALUE_TIME_EXIT, "-"));
                        clientEE.setValues(attachValue(clientEE.getValues(), VALUE_AMOUNT_ENTER_EVENTS_TIME_MAX, "-"));
                    } else {
                        clientEE.setValues(attachValue(clientEE.getValues(), VALUE_AMOUNT_ENTER_EVENTS_DATE, "1"));
                        if ((enterExists && !exitExists) || (exitExists && !enterExists)) {
                            //есть входы, но нет выхода, или наоборот - нет входа, но есть выходы (1 или более)
                            if (enterExists) {
                                clientEE.setValues(attachValue(clientEE.getValues(), VALUE_TIME_ENTER, CalendarUtils.dateTimeToString(new Date(enterTime))));
                                clientEE.setValues(attachValue(clientEE.getValues(), VALUE_TIME_EXIT, "-"));
                            }
                            if (exitExists) {
                                clientEE.setValues(attachValue(clientEE.getValues(), VALUE_TIME_ENTER, "-"));
                                clientEE.setValues(attachValue(clientEE.getValues(), VALUE_TIME_EXIT, CalendarUtils.dateTimeToString(new Date(exitTime))));
                            }
                            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_AMOUNT_ENTER_EVENTS_TIME_MAX, "+"));
                            attachEnterMethod(enterExists, session, clientEE);
                            attachExitMethod(exitExists, session, clientEE);
                            continue;
                        }
                        seconds = clientInside / 1000;
                        hours = seconds / 3600;
                        minutes = (seconds / 60) % 60;

                        clientEE.setValues(attachValue(clientEE.getValues(), VALUE_AMOUNT_ENTER_EVENTS_TIME_MAX, hours.toString() + ":" + minutes.toString()));
                        clientEE.setValues(attachValue(clientEE.getValues(), VALUE_TIME_ENTER, CalendarUtils.dateTimeToString(new Date(enterTime))));
                        clientEE.setValues(attachValue(clientEE.getValues(), VALUE_TIME_EXIT, CalendarUtils.dateTimeToString(new Date(exitTime))));
                    }
                    attachEnterMethod(enterExists, session, clientEE);
                    attachExitMethod(exitExists, session, clientEE);
                } else if (notifyType.equals(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_WEEK.getValue())) {
                    String day = getDayByIteration(iteration, 0);
                    if (clientInside != 0) {
                        seconds = clientInside / 1000;
                        hours = seconds / 3600;
                        minutes = (seconds / 60) % 60;
                        clientEE.setValues(attachValue(clientEE.getValues(), day, hours.toString() + ":" + minutes.toString()));
                    } else {
                        if (enterExists || exitExists) {
                            clientEE.setValues(attachValue(clientEE.getValues(), day, "+"));
                        } else {
                            clientEE.setValues(attachValue(clientEE.getValues(), day, "-"));
                        }
                    }
                    String day2 = getDayByIteration(iteration, 1);
                    String day3 = getDayByIteration(iteration, 2);
                    if (enterExists) {
                        clientEE.setValues(attachValue(clientEE.getValues(), day2, CalendarUtils.timeToString(enterTime)));
                    } else {
                        clientEE.setValues(attachValue(clientEE.getValues(), day2, ""));
                    }
                    if (exitExists) {
                        clientEE.setValues(attachValue(clientEE.getValues(), day3, CalendarUtils.timeToString(exitTime)));
                    } else {
                        clientEE.setValues(attachValue(clientEE.getValues(), day3, ""));
                    }
                }
            }
            if (enterExists || exitExists) {
                daysInside++;
            }
            if (notifyType.equals(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_WEEK.getValue())) {
                clientEE.setValues(attachValue(clientEE.getValues(), VALUE_AMOUNT_ENTER_EVENTS_DATE, daysInside.toString()));
            }
        }
    }

    private String getDayByIteration(int iteration, int type) {
        try {
            return VALUES_ENTER_EVENTS[7 * type + (iteration - 1)];
        } catch (Exception e) {
            return "-";
        }
        /*if (iteration == 1) {
            return VALUE_AMOUNT_ENTER_EVENTS_TIME_MONDAY;
        } else if (iteration == 2) {
            return VALUE_AMOUNT_ENTER_EVENTS_TIME_TUESDAY;
        }  else if (iteration == 3) {
            return VALUE_AMOUNT_ENTER_EVENTS_TIME_WEDNESDAY;
        } else if (iteration == 4) {
            return VALUE_AMOUNT_ENTER_EVENTS_TIME_THURSDAY;
        } else if (iteration == 5) {
            return VALUE_AMOUNT_ENTER_EVENTS_TIME_FRIDAY;
        } else if (iteration == 6) {
            return VALUE_AMOUNT_ENTER_EVENTS_TIME_SATURDAY;
        } else if (iteration == 7) {
            return VALUE_AMOUNT_ENTER_EVENTS_TIME_SUNDAY;
        } else {
            return "-";
        }*/
    }

    private void attachEnterMethod(boolean enterExists, Session session, ClientEE clientEE) {
        String enterMethod;
        if (!enterExists) {
            enterMethod = "-";
        } else {
            if (enterPassDirection == EnterEvent.DETECTED_INSIDE) {
                enterMethod = "Самостоятельно внутри здания ОО";
            } else {
                if (enterPassDirection == EnterEvent.CHECKED_BY_TEACHER_EXT) {
                    enterMethod = "Отмечен в классном журнале";
                } else {
                    if (enterEmployeeId == null && enterGuardianId == null) {
                        enterMethod = "Самостоятельно на турникете";
                    } else {
                        if (enterEmployeeId != null) {
                            Person person = ((Client)session.load(Client.class, enterEmployeeId)).getPerson();
                            enterMethod = String.format("Сотрудником ОО (%s)", person.getFullName());
                        } else {
                            Person person = ((Client)session.load(Client.class, enterGuardianId)).getPerson();
                            enterMethod = String.format("Представитель (%s)", person.getFullName());
                        }
                    }
                }
            }
        }
        clientEE.setValues(attachValue(clientEE.getValues(), VALUE_METHOD_START, enterMethod));
    }

    private void attachExitMethod(boolean exitExists, Session session, ClientEE clientEE) {
        String exitMethod;
        if (!exitExists) {
            exitMethod = "-";
        } else {
            if (exitEmployeeId == null && exitGuardianId == null) {
                exitMethod = "Самостоятельно на турникете";
            } else {
                if (exitEmployeeId != null) {
                    Person person = ((Client)session.load(Client.class, exitEmployeeId)).getPerson();
                    exitMethod = String.format("Сотрудником ОО (%s)", person.getFullName());
                } else {
                    Person person = ((Client)session.load(Client.class, exitGuardianId)).getPerson();
                    exitMethod = String.format("Представитель (%s)", person.getFullName());
                }
            }
        }
        clientEE.setValues(attachValue(clientEE.getValues(), VALUE_METHOD_END, exitMethod));
    }

    private void attachBalanceValues(List<ClientEE> clients) {
        for (ClientEE clientEE : clients) {
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_ACCOUNT,
                            clientEE.getBalances().getAccount() == null ? "" : clientEE.getBalances().getAccount().toString()));
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_BALANCE_START_DATE,
                    getStringMoneyValue(clientEE.getBalances().getBalanceStartDate() == null ? 0L : clientEE.getBalances().getBalanceStartDate())));
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_BALANCE,
                    getStringMoneyValue(clientEE.getBalances().getBalance() == null ? 0L : clientEE.getBalances().getBalance())));
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_AMOUNT_BUY_ALL,
                    getStringMoneyValue(clientEE.getBalances().getAmountBuyAll() == null ? 0L : clientEE.getBalances().getAmountBuyAll())));
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_LIMIT,
                    getStringMoneyValue(clientEE.getBalances().getLimit() == null ? 0L : clientEE.getBalances().getLimit())));
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_AMOUNT_COMPLEX_DATE,
                    clientEE.getBalances().getAmountComplexDate() == null ? "" : clientEE.getBalances().getAmountComplexDate().toString()));
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_QUANTITY_AMOUNT,
                    clientEE.getBalances().getQuantityAmount() == null ? "" : clientEE.getBalances().getQuantityAmount().toString()));
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_PAYMENT_SUM,
                    getStringMoneyValue(clientEE.getBalances().getPaymentSum() == null ? 0L : clientEE.getBalances().getPaymentSum())));
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_MENU_DETAIL,
                    clientEE.getBalances().getMenu_detail() == null ? "" : clientEE.getBalances().getMenu_detail()));
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_BALANCE_DAYS,
                            clientEE.getBalances().getBalanceOnDays() == null ? "" : clientEE.getBalances().getBalanceOnDays().toString()));
        }
    }

    private String getStringMoneyValue(Long money) {
        Long rub = money / 100;
        Long cop = money % 100;
        String cop_str = cop.toString();
        while (cop_str.length() < 2) {
            cop_str = "0" + cop_str;
        }
        return rub.toString() + "," + cop_str;
    }

    private String[] attachValue(String[] values, String name, String value) {
        String[] newValues = new String[values.length + 2];
        System.arraycopy(values, 0, newValues, 0, values.length);
        newValues[newValues.length-2] = name;
        newValues[newValues.length-1] = value;
        return newValues;
    }

    private void checkFirstEnter(EE ee) {
        if (ee.getPassDirection() == EnterEvent.ENTRY || ee.getPassDirection() == EnterEvent.RE_ENTRY ||
                ee.getPassDirection() == EnterEvent.DETECTED_INSIDE || ee.getPassDirection() == EnterEvent.CHECKED_BY_TEACHER_EXT) {
            enterTime = ee.getEvtDateTime();
            enterGuardianId = ee.getGuardianId();
            enterEmployeeId = ee.getChildPassCheckerId();
            enterPassDirection = ee.getPassDirection();
            firstEnterFound = true;
        }
    }

    public static class ClientEE {
        private Long idOfClient;
        private String orgName;
        private String orgType;
        private Long orgId;
        private String orgNum;
        private String surname;
        private String firstname;
        private final List<EE> eeList;
        private Balances balances;
        private String[] values;

        private ClientEE() {
            this.eeList = new ArrayList<EE>();
            this.setBalances(new Balances());
            this.setValues(new String[0]);
        }

        public Long getIdOfClient() {
            return idOfClient;
        }

        public void setIdOfClient(Long idOfClient) {
            this.idOfClient = idOfClient;
        }

        public String getOrgName() {
            return orgName;
        }

        public void setOrgName(String orgName) {
            this.orgName = orgName;
        }

        public String getOrgType() {
            return orgType;
        }

        public void setOrgType(String orgType) {
            this.orgType = orgType;
        }

        public Long getOrgId() {
            return orgId;
        }

        public void setOrgId(Long orgId) {
            this.orgId = orgId;
        }

        public String getOrgNum() {
            return orgNum;
        }

        public void setOrgNum(String orgNum) {
            this.orgNum = orgNum;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public List<EE> getEeList() {
            return eeList;
        }

        public Balances getBalances() {
            return balances;
        }

        public void setBalances(Balances balances) {
            this.balances = balances;
        }

        public String[] getValues() {
            return values;
        }

        public void setValues(String[] values) {
            this.values = values;
        }
    }

    public static class EE {
        private Long evtDateTime;
        private Integer passDirection;
        private Long guardianId;
        private Integer childPassChecker;
        private Long childPassCheckerId;

        public Long getEvtDateTime() {
            return evtDateTime;
        }

        public void setEvtDateTime(Long evtDateTime) {
            this.evtDateTime = evtDateTime;
        }

        public Integer getPassDirection() {
            return passDirection;
        }

        public void setPassDirection(Integer passDirection) {
            this.passDirection = passDirection;
        }

        public Long getGuardianId() {
            return guardianId;
        }

        public void setGuardianId(Long guardianId) {
            this.guardianId = guardianId;
        }

        public Integer getChildPassChecker() {
            return childPassChecker;
        }

        public void setChildPassChecker(Integer childPassChecker) {
            this.childPassChecker = childPassChecker;
        }

        public Long getChildPassCheckerId() {
            return childPassCheckerId;
        }

        public void setChildPassCheckerId(Long childPassCheckerId) {
            this.childPassCheckerId = childPassCheckerId;
        }
    }

    public static class Balances {
        private Long limit;
        private Long account;
        private Long balanceStartDate;
        private Long balance;
        private Long amountBuyAll;
        private Long amountComplexDate;
        private Long quantityAmount;
        private Long paymentSum;
        private String menu_detail;
        private Integer balanceOnDays;

        public Long getLimit() {
            return limit;
        }

        public void setLimit(Long limit) {
            this.limit = limit;
        }

        public Long getAccount() {
            return account;
        }

        public void setAccount(Long account) {
            this.account = account;
        }

        public Long getBalanceStartDate() {
            return balanceStartDate;
        }

        public void setBalanceStartDate(Long balanceStartDate) {
            this.balanceStartDate = balanceStartDate;
        }

        public Long getBalance() {
            return balance;
        }

        public void setBalance(Long balance) {
            this.balance = balance;
        }

        public Long getAmountBuyAll() {
            return amountBuyAll;
        }

        public void setAmountBuyAll(Long amountBuyAll) {
            this.amountBuyAll = amountBuyAll;
        }

        public Long getAmountComplexDate() {
            return amountComplexDate;
        }

        public void setAmountComplexDate(Long amountComplexDate) {
            this.amountComplexDate = amountComplexDate;
        }

        public Long getQuantityAmount() {
            return quantityAmount;
        }

        public void setQuantityAmount(Long quantityAmount) {
            this.quantityAmount = quantityAmount;
        }

        public Long getPaymentSum() {
            return paymentSum;
        }

        public void setPaymentSum(Long paymentSum) {
            this.paymentSum = paymentSum;
        }

        public String getMenu_detail() {
            return menu_detail;
        }

        public void setMenu_detail(String menu_detail) {
            this.menu_detail = menu_detail;
        }

        public Integer getBalanceOnDays() {
            return balanceOnDays;
        }

        public void setBalanceOnDays(Integer balanceOnDays) {
            this.balanceOnDays = balanceOnDays;
        }
    }
}
