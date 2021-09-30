/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;

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
    public static String[] VALUES_ENTER_EVENTS = {
            "amountEntereventsTimeMonday", "amountEntereventsTimeTuesday", "amountEntereventsTimeWednesday",
            "amountEntereventsTimeThursday", "amountEntereventsTimeFriday", "amountEntereventsTimeSaturday",
            "amountEntereventsTimeSunday", "startTimeMonday", "startTimeTuesday", "startTimeWednesday",
            "startTimeThursday", "startTimeFriday", "startTimeSaturday", "startTimeSunday", "endTimeMonday",
            "endTimeTuesday", "endTimeWednesday", "endTimeThursday", "endTimeFriday", "endTimeSaturday",
            "endTimeSunday"};
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

    public static String VALUE_GUARDIAN_NAME = "guardian_name";
    public static String VALUE_GUARDIAN_SURNAME = "guardian_surname";
    public static String VALUE_DATE = "date";
    public static String VALUE_DAY_OF_WEEK = "DayWeeks";
    public static String VALUE_AMOUNT_MONDAY = "ComplexMonday";
    public static String VALUE_AMOUNT_TUESDAY = "ComplexTuesday";
    public static String VALUE_AMOUNT_WEDNESDAY = "ComplexWednesday";
    public static String VALUE_AMOUNT_THURSDAY = "ComplexThursday";
    public static String VALUE_AMOUNT_FRIDAY = "ComplexFriday";
    public static String VALUE_AMOUNT_SATURDAY = "ComplexSaturday";
    public static String VALUE_AMOUNT_SUNDAY = "ComplexSunday";

    public static String VALUE_DELETED_PREORDER_DATE_GUARDIAN = "DeletedPreorderDateGuardian";
    public static String VALUE_DELETED_PREORDER_DATE_OTHER = "DeletedPreorderDateOther";

    public static String GUARDIAN = "Guardian";
    public static String OTHER = "Other";
    public static String REGULAR = "Regular";
    public static String DELETED_PREORDER_DATE = "DeletedPreorderDate";
    public static String END_DATE_PREORDER = "EndDatePreorder";
    public static String COMPLEX_NAME_PREORDER = "ComplexNamePreorder";
    public static String DISH_NAME_PREORDER = "DishNamePreorder";

    public static String VALUE_PREORDER_DATE = "PreorderDate";
    public static String VALUE_PREORDER_SUMM = "PreorderSumm";
    public static String VALUE_PREORDER_LACKS_SUMM = "PreorderLacksSumm";

    public static final String CLIENT_GENDER_KEY = "gender";
    public static final String CLIENT_GENDER_VALUE_MALE = "male";
    public static final String CLIENT_GENDER_VALUE_FEMALE = "female";

    final static String JOB_NAME_DAILY = "NotificationSummaryDaily";
    final static String JOB_NAME_WEEKLY = "NotificationSummaryWeekly";

    final static Integer MILLIS_IN_DAY = 60 * 60 * 24 * 1000;
    private HashMap<Long, List<Long>> idRegularPreorerSending = new HashMap<>();

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
        String syncScheduleDaily = RuntimeContext.getInstance().getConfigProperties()
                .getProperty("ecafe.processor.notification.summary.daily.cronExpression", "");
        String syncScheduleWeekly = RuntimeContext.getInstance().getConfigProperties()
                .getProperty("ecafe.processor.notification.summary.weekly.cronExpression", "");
        if (syncScheduleDaily.equals("") && syncScheduleWeekly.equals("")) {
            return;
        }
        try {
            logger.info("Scheduling notification summary calculation service job: " + syncScheduleDaily);
            JobDetail jobDetailDaily = new JobDetail(JOB_NAME_DAILY, Scheduler.DEFAULT_GROUP,
                    NotificationJobDaily.class);
            JobDetail jobDetailWeekly = new JobDetail(JOB_NAME_WEEKLY, Scheduler.DEFAULT_GROUP,
                    NotificationJobWeekly.class);

            SchedulerFactory sfb = new StdSchedulerFactory();
            Scheduler scheduler = sfb.getScheduler();
            if (!syncScheduleDaily.equals("")) {
                CronTrigger triggerDaily = new CronTrigger(JOB_NAME_DAILY, Scheduler.DEFAULT_GROUP);
                triggerDaily.setCronExpression(syncScheduleDaily);
                if (scheduler.getTrigger(JOB_NAME_DAILY, Scheduler.DEFAULT_GROUP) != null) {
                    scheduler.deleteJob(JOB_NAME_DAILY, Scheduler.DEFAULT_GROUP);
                }
                scheduler.scheduleJob(jobDetailDaily, triggerDaily);
            }
            if (!syncScheduleWeekly.equals("")) {
                CronTrigger triggerWeekly = new CronTrigger(JOB_NAME_WEEKLY, Scheduler.DEFAULT_GROUP);
                triggerWeekly.setCronExpression(syncScheduleWeekly);
                if (scheduler.getTrigger(JOB_NAME_WEEKLY, Scheduler.DEFAULT_GROUP) != null) {
                    scheduler.deleteJob(JOB_NAME_WEEKLY, Scheduler.DEFAULT_GROUP);
                }
                scheduler.scheduleJob(jobDetailWeekly, triggerWeekly);
            }
            scheduler.start();
        } catch (Exception e) {
            logger.error("Failed to schedule notification summary calculation service job:", e);
        }
    }

    public void run(Date startDate, Date endDate, Long notyfyType, boolean clickedOnButton) {
        if (!isOn() && !clickedOnButton) {
            return;
        }
        try {
            logger.info("Start summary calculation notification");
            final EventNotificationService notificationService = RuntimeContext.getAppContext()
                    .getBean(EventNotificationService.class);
            List<ClientEE> clients = null;
            long pause = 1000 * 60 * 15; //будет 3 попытки с паузой в 15 минут
            for (int attempt = 0; attempt < 3; attempt++) {
                try {
                    logger.info(String.format("SummaryCalculationService attempt %s generate params", attempt));
                    idRegularPreorerSending.clear();
                    clients = generateNotificationParams(startDate, endDate, notyfyType);
                    break;
                } catch (Exception e) {
                    logger.error("Error execute generateNotificationParams. Pause 15 min", e);
                    Thread.sleep(pause);
                }
            }

            if (clients == null) {
                return;
            }

            for (ClientEE clientEE : clients) {
                if (clientEE.getNotInform()) {
                    continue;
                }
                if (clickedOnButton) {
                    clientEE.setValues(attachValue(clientEE.getValues(), "TEST", "true"));
                }
                try {
                    String type = "";
                    int notificationType = 0;
                    if (notyfyType
                            .equals(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_DAY.getValue())) {
                        type = EventNotificationService.NOTIFICATION_SUMMARY_BY_DAY;
                        notificationType = ClientSms.TYPE_SUMMARY_DAILY_NOTIFICATION;
                    } else if (notyfyType
                            .equals(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_WEEK.getValue())) {
                        type = EventNotificationService.NOTIFICATION_SUMMARY_BY_WEEK;
                        notificationType = ClientSms.TYPE_SUMMARY_WEEKLY_NOTIFICATION;
                    }

                    //установка недостающих параметров date и
                    Date currentDate = new Date(System.currentTimeMillis());
                    clientEE.setValues(
                            attachValue(clientEE.getValues(), VALUE_DATE, CalendarUtils.dateToString(currentDate)));

                    Client client = entityManager.find(Client.class, clientEE.getIdOfClient());
                    if (clientEE.getGuardians().size() == 0) {
                        clientEE.setValues(attachValue(clientEE.getValues(), VALUE_GUARDIAN_NAME, ""));
                        clientEE.setValues(attachValue(clientEE.getValues(), VALUE_GUARDIAN_SURNAME, ""));
                        notificationService.sendNotificationSummary(client, null, type, clientEE.getValues(),
                                new Date(System.currentTimeMillis()), notificationType);
                    } else {
                        for (String ww : clientEE.getGuardians()) {
                            String[] arr = ww.split("\\|");
                            Client guardian = entityManager.find(Client.class, Long.parseLong(arr[0]));
                            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_GUARDIAN_NAME, arr[1]));
                            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_GUARDIAN_SURNAME, arr[2]));
                            notificationService.sendNotificationSummary(guardian, client, type, clientEE.getValues(),
                                    new Date(System.currentTimeMillis()), notificationType);
                        }
                    }
                } catch (Exception e) {
                    //если ошибка по одному клиенту, не прерываем весь процесс
                    logger.error("Error sending summary notification for client: ", e);
                    List <Long> idPre = idRegularPreorerSending.get(clientEE.idOfClient);
                    for (Long id: idPre)
                    {
                        DAOService.getInstance().setFlagSendedNotification(id, false);
                    }
                }
            }
            logger.info("End summary calculation notification");
        } catch (Exception e) {
            logger.error("Error process summary notification: ", e);
        }
    }

    public void runDaily() {
        Date today = new Date(System.currentTimeMillis());
        Date endDate = CalendarUtils.endOfDay(today);
        Date startDate = CalendarUtils.truncateToDayOfMonth(today);
        run(startDate, endDate, ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_DAY.getValue(), false);
    }

    public void runWeekly() {
        Date today = new Date(System.currentTimeMillis());
        Date[] dates = CalendarUtils.getCurrentWeekBeginAndEnd(today);
        Date startDate = CalendarUtils.truncateToDayOfMonth(dates[0]);
        Date endDate = CalendarUtils.endOfDay(dates[1]);
        run(startDate, endDate, ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_WEEK.getValue(), false);
    }

    public static boolean isOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getConfigProperties()
                .getProperty("ecafe.processor.notification.summary.node", "1");
        if (StringUtils.isBlank(instance) || StringUtils.isBlank(reqInstance) || !instance.trim()
                .equals(reqInstance.trim())) {
            return false;
        }
        return true;
    }

    private String getDayOfWeek(String day) {
        if (day.equals("Пн")) {
            return "Понедельник";
        } else if (day.equals("Вт")) {
            return "Вторник";
        } else if (day.equals("Ср")) {
            return "Среда";
        } else if (day.equals("Чт")) {
            return "Четверг";
        } else if (day.equals("Пт")) {
            return "Пятница";
        } else if (day.equals("Сб")) {
            return "Суббота";
        } else if (day.equals("Вс")) {
            return "Воскресенье";
        } else {
            return "";
        }
    }

    public List<ClientEE> generateNotificationParams(Date startDate, Date endDate, Long notifyType) {
        String[] result = new String[4 * 2];
        String sDate = CalendarUtils.dateToString(startDate);
        result[0] = "Startdate";
        result[1] = sDate;
        String eDate = CalendarUtils.dateToString(endDate);
        result[2] = "Enddate";
        result[3] = eDate;
        Date currentDate = new Date(System.currentTimeMillis());
        result[4] = VALUE_DATE;
        result[5] = CalendarUtils.dateToString(currentDate);
        result[6] = VALUE_DAY_OF_WEEK;
        result[7] = getDayOfWeek(CalendarUtils.dayInWeekToString(currentDate));

        //Подсчет данных по событиям проходов. Первый запрос - старая таблица уведомлений, второй - новая
        String query_ee = "SELECT c.idofclient, p.surname, p.firstname, "
                + "coalesce(e.evtdatetime, -1) AS evtdatetime, coalesce(e.passdirection, -1) AS passdirection, "
                + "coalesce(e.guardianid, -1) AS guardianId, coalesce(e.childpasschecker, -1) AS childpasschecker, "
                + "coalesce(e.childpasscheckerid, -1) AS childpasscheckerId, o.shortnameinfoservice, o.organizationtype, o.idoforg, c.gender "
                + "FROM cf_clientsnotificationsettings n INNER JOIN cf_clients c ON c.idofclient = n.idofclient AND n.notifytype = :notifyType "
                + "INNER JOIN cf_persons p ON c.idofperson = p.idofperson "
                + "INNER JOIN cf_orgs o ON c.idoforg = o.idoforg "
                + "LEFT OUTER JOIN cf_enterevents e ON c.idofclient = e.idofclient "
                + "AND e.evtdatetime BETWEEN :startTime AND :endTime "
                + "WHERE c.idofclientgroup NOT BETWEEN :group_employees AND :group_displaced " + "UNION "
                + "SELECT c.idofclient, p.surname, p.firstname, "
                + "coalesce(e.evtdatetime, -1) AS evtdatetime, coalesce(e.passdirection, -1) AS passdirection, "
                + "coalesce(e.guardianid, -1) AS guardianId, coalesce(e.childpasschecker, -1) AS childpasschecker, "
                + "coalesce(e.childpasscheckerid, -1) AS childpasscheckerId, o.shortnameinfoservice, o.organizationtype, o.idoforg, c.gender "
                + "FROM cf_client_guardian_notificationsettings n "
                + "INNER JOIN cf_client_guardian cg ON n.idofclientguardian = cg.idofclientguardian AND n.notifytype = :notifyType "
                + "INNER JOIN cf_clients c ON c.idofclient = cg.idofchildren "
                + "INNER JOIN cf_persons p ON c.idofperson = p.idofperson "
                + "INNER JOIN cf_orgs o ON c.idoforg = o.idoforg "
                + "LEFT OUTER JOIN cf_enterevents e ON c.idofclient = e.idofclient "
                + "AND e.evtdatetime BETWEEN :startTime AND :endTime "
                + "WHERE c.idofclientgroup NOT BETWEEN :group_employees AND :group_displaced " + "ORDER BY 1, 4";
        Query equery = entityManager.createNativeQuery(query_ee);
        equery.setParameter("notifyType", notifyType);
        equery.setParameter("startTime", startDate.getTime());
        equery.setParameter("endTime", endDate.getTime());
        equery.setParameter("group_employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
        equery.setParameter("group_displaced", ClientGroup.Predefined.CLIENT_DISPLACED.getValue());
        List list = equery.getResultList();
        Long idOfClient = -1L;
        List<ClientEE> clients = new ArrayList<ClientEE>();
        ClientEE clientEE = null;
        for (Object obj : list) {
            Object[] row = (Object[]) obj;
            Long id = ((BigInteger) row[0]).longValue();
            String surname = (String) row[1];
            String firstname = (String) row[2];
            if (!id.equals(idOfClient)) {
                clientEE = new ClientEE();
                clientEE.setValues(result.clone());
                clientEE.setValues(attachGenderToValues((Integer) row[11], clientEE.getValues()));
                idOfClient = id;
                clients.add(clientEE);
            }
            clientEE.setIdOfClient(id);
            clientEE.setSurname(surname);
            clientEE.setFirstname(firstname);
            Long evtDateTime = ((BigInteger) row[3]).longValue();
            if (evtDateTime > -1L) {
                EE ee = new EE();
                ee.setEvtDateTime(evtDateTime);
                ee.setPassDirection(((Integer) row[4]).intValue());
                ee.setGuardianId(((BigInteger) row[5]).longValue());
                if (ee.getGuardianId().equals(-1L)) {
                    ee.setGuardianId(null);
                }
                ee.setChildPassChecker(((Integer) row[6]).intValue());
                ee.setChildPassCheckerId(((BigInteger) row[7]).longValue());
                if (ee.getChildPassCheckerId().equals(-1L)) {
                    ee.setChildPassCheckerId(null);
                }
                clientEE.getEeList().add(ee);
            }
            clientEE.setOrgName((String) row[8]);
            clientEE.setOrgType(OrganizationType.fromInteger(((Integer) row[9]).intValue()).toString());
            clientEE.setOrgId(((BigInteger) row[10]).longValue());
            clientEE.setOrgNum(Org.extractOrgNumberFromName(clientEE.getOrgName()));
        }

        attachEEValues(clients, notifyType, startDate, endDate);

        //Подсчет значений для детализации меню в случае уведомления по итогам дня
        Map menuMap = new TreeMap<Long, String>();
        if (notifyType.equals(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_DAY.getValue())) {
            String query_menu = "SELECT DISTINCT c.idofclient, od.qty, od.rprice, od.menudetailname, o.idoforder "
                    + "FROM cf_clients c INNER JOIN cf_orders o ON c.idofclient = o.idofclient "
                    + "INNER JOIN cf_orderdetails od ON o.idoforder = od.idoforder AND o.idoforg = od.idoforg "
                    + "WHERE (exists(SELECT * FROM cf_clientsnotificationsettings n WHERE c.idofclient = n.idofclient AND n.notifytype = :notifyType) OR exists (SELECT * FROM cf_client_guardian cg "
                    + "INNER JOIN cf_client_guardian_notificationsettings nn ON cg.idofclientguardian = nn.idofclientguardian AND nn.notifytype = :notifyType WHERE cg.idofchildren = c.idofclient)) "
                    + "AND o.createddate BETWEEN :startTime AND :endTime  AND c.idofclientgroup NOT BETWEEN :group_employees AND :group_displaced";
            Query mquery = entityManager.createNativeQuery(query_menu);
            mquery.setParameter("notifyType", notifyType);
            mquery.setParameter("startTime", startDate.getTime());
            mquery.setParameter("endTime", endDate.getTime());
            mquery.setParameter("group_employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            mquery.setParameter("group_displaced", ClientGroup.Predefined.CLIENT_DISPLACED.getValue());
            List mlist = mquery.getResultList();

            //String menu_name = "";
            for (Object obj : mlist) {
                Object[] row = (Object[]) obj;
                Long id = ((BigInteger) row[0]).longValue();
                Integer qty = ((Integer) row[1]).intValue();
                Long rprice = ((BigInteger) row[2]).longValue();
                Long sum = qty * rprice;
                String menu = (String) row[3];
                String menu_name = (String) menuMap.get(id);
                menu_name = (menu_name == null ? "" : menu_name);
                menu_name += "%" + menu + " " + (qty > 1 ? String.format("(%s шт.) ", qty) : "") + sum / 100 + " руб.";
                if (sum % 100 != 0) {
                    menu_name += " " + sum % 100 + " коп.";
                }
                menu_name += "%";
                menuMap.put(id, menu_name);
            }
        }

        //Подсчет данных по балансам
        String query_balance =
                "SELECT distinct c.idofclient, p.surname, p.firstname, c.expenditurelimit, c.balance, coalesce(query1.sum1, 0) as sum1, coalesce(query2.sum2, 0) AS sum2, "
                        + "c.contractid, coalesce(query3.count1, 0) as count1, coalesce(query4.sum3, 0) as sum3, coalesce(query5.count2, 0) as count2, coalesce(query5.sum4, 0) as sum4, c.gender "
                        + (notifyType
                        .equals(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_DAY.getValue()) ? ""
                        : ", coalesce(query6.countw1, 0) as countw1, coalesce(query7.countw2, 0) as countw2, coalesce(query8.countw3, 0) as countw3, coalesce(query9.countw4, 0) as countw4, "
                                + "coalesce(query10.countw5, 0) as countw5, coalesce(query11.countw6, 0) as countw6, coalesce(query12.countw7, 0) as countw7 ")
                        + "FROM cf_clients c INNER JOIN cf_persons p ON c.idofperson = p.idofperson "
                        + "left outer JOIN " + "(SELECT c.idofclient, sum(t1.transactionsum) AS sum1 FROM cf_clients c "
                        + "LEFT JOIN cf_transactions t1 ON t1.idofclient = c.idofclient AND t1.transactionDate BETWEEN :startTime AND :curTime "
                        + "GROUP BY c.idofclient) AS query1 ON c.idofclient = query1.idofclient " + "left outer JOIN "
                        + "(SELECT c.idofclient, sum(-t2.transactionsum) AS sum2 FROM cf_clients c "
                        + "LEFT JOIN cf_transactions t2 ON t2.idofclient = c.idofclient AND t2.transactionDate BETWEEN :startTime AND :endTime AND (t2.sourcetype = :transactionType1 "
                        + "OR t2.sourcetype = :transactionType2) "
                        + "GROUP BY c.idofclient) AS query2 ON c.idofclient = query2.idofclient " + "left outer JOIN "
                        + "(SELECT c.idofclient, count(distinct extract(day from TO_TIMESTAMP(o.createddate / 1000))) AS count1 FROM cf_clients c "
                        + "LEFT JOIN cf_orders o ON o.idofclient = c.idofclient AND o.createddate BETWEEN :startTime AND :endTime AND o.ordertype IN (:orderTypes) "
                        + "AND o.state = :orderState "
                        + "GROUP BY c.idofclient) AS query3 ON c.idofclient = query3.idofclient " + "left outer JOIN "
                        + "(SELECT c.idofclient, sum(t.transactionsum) AS sum3 FROM cf_clients c "
                        + "LEFT OUTER JOIN cf_transactions t ON t.idofclient = c.idofclient AND t.transactionDate >= :endTime AND t.transactionDate <= :curTime "
                        + "GROUP BY c.idofclient) AS query4 ON c.idofclient = query4.idofclient " + "left outer JOIN "
                        + "(SELECT c.idofclient, count(idofclientpayment) as count2, sum(paysum) as sum4 FROM cf_clients c "
                        + "LEFT OUTER JOIN cf_transactions t ON t.idofclient = c.idofclient AND t.transactionDate BETWEEN :startTime AND :endTime "
                        + "LEFT OUTER JOIN cf_clientpayments p ON p.idoftransaction = t.idoftransaction AND p.createddate BETWEEN :startTime AND :endTime "
                        + "GROUP BY c.idofclient) AS query5 ON c.idofclient = query5.idofclient " + (notifyType
                        .equals(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_DAY.getValue()) ? ""
                        : "left outer join " + "(SELECT c.idofclient, count(o.idoforder) AS countw1 FROM cf_clients c "
                                + "LEFT JOIN cf_orders o ON o.idofclient = c.idofclient AND o.createddate BETWEEN :startTime AND :endTime AND o.ordertype IN (:orderTypes) "
                                + "AND o.state = :orderState and extract(dow from TO_TIMESTAMP(o.createddate / 1000)) = 1 "
                                + "GROUP BY c.idofclient) AS query6 ON c.idofclient = query6.idofclient "
                                + "left outer join "
                                + "(SELECT c.idofclient, count(o.idoforder) AS countw2 FROM cf_clients c "
                                + "LEFT JOIN cf_orders o ON o.idofclient = c.idofclient AND o.createddate BETWEEN :startTime AND :endTime AND o.ordertype IN (:orderTypes) "
                                + "AND o.state = :orderState and extract(dow from TO_TIMESTAMP(o.createddate / 1000)) = 2 "
                                + "GROUP BY c.idofclient) AS query7 ON c.idofclient = query7.idofclient "
                                + "left outer join "
                                + "(SELECT c.idofclient, count(o.idoforder) AS countw3 FROM cf_clients c "
                                + "LEFT JOIN cf_orders o ON o.idofclient = c.idofclient AND o.createddate BETWEEN :startTime AND :endTime AND o.ordertype IN (:orderTypes) "
                                + "AND o.state = :orderState and extract(dow from TO_TIMESTAMP(o.createddate / 1000)) = 3 "
                                + "GROUP BY c.idofclient) AS query8 ON c.idofclient = query8.idofclient "
                                + "left outer join "
                                + "(SELECT c.idofclient, count(o.idoforder) AS countw4 FROM cf_clients c "
                                + "LEFT JOIN cf_orders o ON o.idofclient = c.idofclient AND o.createddate BETWEEN :startTime AND :endTime AND o.ordertype IN (:orderTypes) "
                                + "AND o.state = :orderState and extract(dow from TO_TIMESTAMP(o.createddate / 1000)) = 4 "
                                + "GROUP BY c.idofclient) AS query9 ON c.idofclient = query9.idofclient "
                                + "left outer join "
                                + "(SELECT c.idofclient, count(o.idoforder) AS countw5 FROM cf_clients c "
                                + "LEFT JOIN cf_orders o ON o.idofclient = c.idofclient AND o.createddate BETWEEN :startTime AND :endTime AND o.ordertype IN (:orderTypes) "
                                + "AND o.state = :orderState and extract(dow from TO_TIMESTAMP(o.createddate / 1000)) = 5 "
                                + "GROUP BY c.idofclient) AS query10 ON c.idofclient = query10.idofclient "
                                + "left outer join "
                                + "(SELECT c.idofclient, count(o.idoforder) AS countw6 FROM cf_clients c "
                                + "LEFT JOIN cf_orders o ON o.idofclient = c.idofclient AND o.createddate BETWEEN :startTime AND :endTime AND o.ordertype IN (:orderTypes) "
                                + "AND o.state = :orderState and extract(dow from TO_TIMESTAMP(o.createddate / 1000)) = 6 "
                                + "GROUP BY c.idofclient) AS query11 ON c.idofclient = query11.idofclient "
                                + "left outer join "
                                + "(SELECT c.idofclient, count(o.idoforder) AS countw7 FROM cf_clients c "
                                + "LEFT JOIN cf_orders o ON o.idofclient = c.idofclient AND o.createddate BETWEEN :startTime AND :endTime AND o.ordertype IN (:orderTypes) "
                                + "AND o.state = :orderState and extract(dow from TO_TIMESTAMP(o.createddate / 1000)) = 7 "
                                + "GROUP BY c.idofclient) AS query12 ON c.idofclient = query12.idofclient ")
                        + "WHERE (exists(select * from cf_clientsnotificationsettings n where c.idofclient = n.idofclient and n.notifytype = :notifyType) or exists (select * from cf_client_guardian cg "
                        + "inner join cf_client_guardian_notificationsettings nn on cg.idofclientguardian = nn.idofclientguardian and nn.notifytype = :notifyType where cg.idofchildren = c.idofclient)) "
                        + "and c.idofclientgroup not between :group_employees and :group_displaced";
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
        bquery.setParameter("group_employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
        bquery.setParameter("group_displaced", ClientGroup.Predefined.CLIENT_DISPLACED.getValue());
        List blist = bquery.getResultList();
        Long order1 = null, order2 = null, order3 = null, order4 = null, order5 = null, order6 = null, order7 = null;
        for (Object obj : blist) {
            Object[] row = (Object[]) obj;
            Long id = ((BigInteger) row[0]).longValue();
            String surname = (String) row[1];
            String firstname = (String) row[2];
            Long limit = ((BigInteger) row[3]).longValue();
            Long balance = ((BigInteger) row[4]).longValue();
            Long byTransStart = ((BigDecimal) row[5]).longValue();
            Long orderSum = ((BigDecimal) row[6]).longValue();
            Long contractId = ((BigInteger) row[7]).longValue();
            Long amountComplexDate = ((BigInteger) row[8]).longValue();
            Long byTransEnd = ((BigDecimal) row[9]).longValue();
            Long quantityAmount = ((BigInteger) row[10]).longValue();
            Long paymentSum = ((BigDecimal) row[11]).longValue();
            if (notifyType.equals(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_WEEK.getValue())) {
                order1 = ((BigInteger) row[13]).longValue();
                order2 = ((BigInteger) row[14]).longValue();
                order3 = ((BigInteger) row[15]).longValue();
                order4 = ((BigInteger) row[16]).longValue();
                order5 = ((BigInteger) row[17]).longValue();
                order6 = ((BigInteger) row[18]).longValue();
                order7 = ((BigInteger) row[19]).longValue();
            }
            clientEE = findClientEEByClientId(clients, id);
            if (clientEE == null) {
                clientEE = new ClientEE();
                clientEE.setIdOfClient(id);
                clientEE.setSurname(surname);
                clientEE.setFirstname(firstname);
                clientEE.setValues(result.clone());
                clientEE.setValues(attachGenderToValues((Integer) row[12], clientEE.getValues()));
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
            String menu = (String) menuMap.get(id);
            balances.setMenu_detail(menu == null ? "" : menu);
            Long balanceOnDays = null;
            if (orderSum > 0) {
                balanceOnDays = balances.getBalance() / orderSum;
                if (balanceOnDays < 0) {
                    balanceOnDays = 0L;
                }
            }
            balances.setBalanceOnDays(balanceOnDays == null ? null : balanceOnDays.intValue());
            balances.setOrder1(order1);
            balances.setOrder2(order2);
            balances.setOrder3(order3);
            balances.setOrder4(order4);
            balances.setOrder5(order5);
            balances.setOrder6(order6);
            balances.setOrder6(order7);
            clientEE.setBalances(balances);
        }

        attachBalanceValues(clients, notifyType);

        //подсчет данных по предзаказам
        if (notifyType.equals(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_DAY.getValue())) {
            String preorders_query =
                    "SELECT DISTINCT pc.preorderdate, pc.state, pc.idofclient, p.surname, p.firstname, c.gender,"
                            + " rp.state AS stateReg, rp.lastupdate, rp.enddate, rp.itemcode, rp.itemname, rp.idofcomplex, rp.createddate, rp.idofregularpreorder "
                            + " FROM cf_preorder_complex pc "
                            + " JOIN cf_regular_preorders rp ON rp.idofregularpreorder = pc.idofregularpreorder "

                            + " JOIN cf_clients c ON c.idofclient = pc.idofclient JOIN cf_persons p ON p.idofperson = c.idofperson "
                            + " JOIN cf_clientsnotificationsettings n ON c.idofclient = n.idofclient AND n.notifytype = :notifyType "
                            + " WHERE pc.preorderdate > :date AND pc.deletedstate = 1 AND rp.sendeddailynotification is not TRUE "
                            + " AND NOT exists(SELECT pc1.idofpreordercomplex FROM cf_preorder_complex pc1 "
                            + "WHERE pc1.idofclient = pc.idofclient AND pc1.deletedstate = 0 AND pc1.preorderdate = pc.preorderdate AND pc1.amount > 0) "
                            + " UNION "
                            + "SELECT DISTINCT pmd.preorderdate, pmd.state, pmd.idofclient, p.surname, p.firstname, c.gender,"
                            + " rp.state AS stateReg, rp.lastupdate, rp.enddate, rp.itemcode, rp.itemname, rp.idofcomplex, rp.createddate, rp.idofregularpreorder "
                            + " FROM cf_preorder_menudetail pmd "
                            + " JOIN cf_regular_preorders rp ON rp.idofregularpreorder = pmd.idofregularpreorder "
                            + " JOIN cf_clients c ON c.idofclient = pmd.idofclient JOIN cf_persons p ON p.idofperson = c.idofperson "
                            + " JOIN cf_clientsnotificationsettings n ON c.idofclient = n.idofclient AND n.notifytype = :notifyType "
                            + " WHERE pmd.preorderdate > :date AND pmd.deletedstate = 1 AND rp.sendeddailynotification is not TRUE "
                            + " AND NOT exists(SELECT pmd1.idofpreordermenudetail FROM cf_preorder_menudetail pmd1 "
                            + "WHERE pmd1.idofclient = pmd.idofclient AND pmd1.deletedstate = 0 AND pmd1.preorderdate = pmd.preorderdate AND pmd1.amount > 0) "
                            + " UNION "
                            + "SELECT DISTINCT pc.preorderdate, pc.state, pc.idofclient, p.surname, p.firstname, c.gender, "
                            + " rp.state AS stateReg, rp.lastupdate, rp.enddate, rp.itemcode, rp.itemname, rp.idofcomplex, rp.createddate, rp.idofregularpreorder "
                            + "FROM cf_client_guardian_notificationsettings n JOIN cf_client_guardian cg ON n.idofclientguardian = cg.idofclientguardian AND n.notifytype = :notifyType "
                            + "JOIN cf_clients c ON c.idofclient = cg.idofchildren "
                            + "JOIN cf_preorder_complex pc ON c.idofclient = pc.idofclient "
                            + " JOIN cf_regular_preorders rp ON rp.idofregularpreorder = pc.idofregularpreorder "
                            + "JOIN cf_persons p ON p.idofperson = c.idofperson "
                            + "WHERE pc.preorderdate > :date AND pc.deletedstate = 1 AND rp.sendeddailynotification is not TRUE "
                            + "AND NOT exists(SELECT pc1.idofpreordercomplex FROM cf_preorder_complex pc1 "
                            + "WHERE pc1.idofclient = pc.idofclient AND pc1.deletedstate = 0 AND pc1.preorderdate = pc.preorderdate AND pc1.amount > 0) "
                            + "UNION "


                            + "SELECT DISTINCT pmd.preorderdate, pmd.state, pmd.idofclient, p.surname, p.firstname, c.gender, "
                            + " rp.state AS stateReg, rp.lastupdate, rp.enddate, rp.itemcode, rp.itemname, rp.idofcomplex, rp.createddate, rp.idofregularpreorder "
                            + "FROM cf_client_guardian_notificationsettings n JOIN cf_client_guardian cg ON n.idofclientguardian = cg.idofclientguardian AND n.notifytype = :notifyType "
                            + "JOIN cf_clients c ON c.idofclient = cg.idofchildren "
                            + "JOIN cf_preorder_menudetail pmd ON pmd.idofclient = c.idofclient "
                            + " JOIN cf_regular_preorders rp ON rp.idofregularpreorder = pmd.idofregularpreorder "
                            + "JOIN cf_persons p ON p.idofperson = c.idofperson "
                            + "WHERE pmd.preorderdate > :date AND pmd.deletedstate = 1 AND rp.sendeddailynotification is not TRUE "
                            + "AND NOT exists(SELECT pmd1.idofpreordermenudetail FROM cf_preorder_menudetail pmd1 "
                            + "WHERE pmd1.idofclient = pmd.idofclient AND pmd1.deletedstate = 0 AND pmd1.preorderdate = pmd.preorderdate AND pmd1.amount > 0) "
                            + "ORDER BY 3,1";
            Query pQuery = entityManager.createNativeQuery(preorders_query);
            pQuery.setParameter("date", System.currentTimeMillis());
            pQuery.setParameter("notifyType", notifyType);
            List plist = pQuery.getResultList();

            List<PreorderData> preorderData = new ArrayList<>();
            List<PreorderRegularData> regularData = new ArrayList<>();

            for (Object obj : plist) {
                Object[] row = (Object[]) obj;
                Long idRegPreor =  ((BigInteger) row[13]).longValue();
                Long idClient = ((BigInteger) row[2]).longValue();
                List <Long> idPre = idRegularPreorerSending.get(idClient);
                if (idPre == null || !idPre.contains(idRegPreor)) {
                    DAOService.getInstance().setFlagSendedNotification(idRegPreor, true);
                    if (idPre == null)
                        idPre = new ArrayList<>();
                    idPre.add(idRegPreor);
                    idRegularPreorerSending.put(idClient, idPre);
                }
                PreorderRegularData currPreorderRegularData = new PreorderRegularData();
                currPreorderRegularData.setStateReg((Integer) row[6]);
                currPreorderRegularData.setLastUpdate(new Date(((BigInteger) row[7]).longValue()));
                currPreorderRegularData.setEndDateReg(new Date(((BigInteger) row[8]).longValue()));
                currPreorderRegularData.setItemCode((String) row[9]);
                currPreorderRegularData.setItemname((String) row[10]);
                currPreorderRegularData.setComplexId(((Integer) row[11]).longValue());
                currPreorderRegularData.setCreateDate(new Date(((BigInteger) row[12]).longValue()));
                regularData.add(currPreorderRegularData);

                PreorderData currPreorderData = new PreorderData();
                currPreorderData.setDate(new Date(((BigInteger) row[0]).longValue()));
                currPreorderData.setState((Integer) row[1]);
                currPreorderData.setId(idClient);
                currPreorderData.setSurname((String) row[3]);
                currPreorderData.setFirstname((String) row[4]);
                currPreorderData.setGender((Integer) row[5]);

                boolean added = false;
                String addedId;
                if (currPreorderRegularData.getItemCode() == null)
                    addedId = currPreorderRegularData.getComplexId().toString() + "_Complex";
                else
                    addedId = currPreorderRegularData.getItemCode() + "_Regular";


                for (PreorderData preorderData1 : preorderData) {
                    //Если такой клиент уже есть
                    if (preorderData1.getId() == currPreorderData.getId()) {
                        //Если такого блюда у клиента не было
                        if (!preorderData1.getIds().contains(addedId))
                        {
                            preorderData1.getIds().add(addedId);
                        }
                        added = true;
                        break;
                    }
                }
                if (!added) {
                    currPreorderData.getIds().add(addedId);
                    preorderData.add(currPreorderData);
                }
            }

            for (PreorderData preorderData1 : preorderData) {
                long id = preorderData1.getId();
                String surname = preorderData1.getSurname();
                String firstname = preorderData1.getFirstname();
                clientEE = findClientEEByClientId(clients, id);
                if (clientEE == null) {
                    clientEE = new ClientEE();
                    clientEE.setIdOfClient(id);
                    clientEE.setSurname(surname);
                    clientEE.setFirstname(firstname);
                    clientEE.setValues(result.clone());
                    clientEE.setValues(attachGenderToValues(preorderData1.getGender(), clientEE.getValues()));
                    clients.add(clientEE);
                }

                for (String idReg : preorderData1.getIds()) {
                    PreorderRegularData currPreorderRegularData = new PreorderRegularData();
                    for (PreorderRegularData preorderRegularData: regularData)
                    {
                        String addedId;
                        if (preorderRegularData.getItemCode() == null)
                            addedId = preorderRegularData.getComplexId().toString() + "_Complex";
                        else
                            addedId = preorderRegularData.getItemCode() + "_Regular";
                        if (addedId.equals(idReg))
                        {
                            currPreorderRegularData = preorderRegularData;
                            break;
                        }
                    }
                   //PreorderRegularData currPreorderRegularData = regularData.get(idReg.intValue());
                    Integer stateReq = currPreorderRegularData.getStateReg();

                    NotifyPreorderDailyDetail notifyPreorderDailyDetail = new NotifyPreorderDailyDetail();
                    notifyPreorderDailyDetail.setDeleteDate(currPreorderRegularData.lastUpdate);
                    notifyPreorderDailyDetail.setEndDate(currPreorderRegularData.endDateReg);
                    if (currPreorderRegularData.getItemCode() == null) {
                        notifyPreorderDailyDetail.setComplexName(currPreorderRegularData.itemname);
                    } else {
                        try {
                            ComplexInfo complexInfo = DAOReadonlyService.getInstance()
                                    .getComplexInfo(entityManager.find(Client.class, clientEE.getIdOfClient()),
                                            currPreorderRegularData.getComplexId().intValue(), currPreorderRegularData.getCreateDate());
                            notifyPreorderDailyDetail.setComplexName(complexInfo.getComplexName());
                            notifyPreorderDailyDetail.setDishName(currPreorderRegularData.itemname);
                        } catch (Exception e) {
                            notifyPreorderDailyDetail.setComplexName(null);
                        }
                    }

                    if (stateReq.equals(PreorderState.OK.getCode())) {
                        clientEE.getPreorders().getDeletedPreorderDateGuardian().add(notifyPreorderDailyDetail);
                    } else {
                        clientEE.getPreorders().getDeletedPreorderDateOther().add(notifyPreorderDailyDetail);
                    }
                }
            }
            attachPreorderDailyValues(clients);
        }

        if (notifyType.equals(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_WEEK.getValue())) {
            String preorders_query =
                    "SELECT pc.preorderdate, cast(pc.amount * pc.complexprice AS BIGINT), c.idofclient, c.balance, p.surname, p.firstname, c.gender "
                            + "FROM cf_preorder_complex pc JOIN cf_clients c ON pc.idofclient = c.idofclient "
                            + "JOIN cf_persons p ON p.idofperson = c.idofperson "
                            + "JOIN cf_clientsnotificationsettings n ON c.idofclient = n.idofclient AND n.notifytype = :notifyType "
                            + "WHERE pc.deletedstate = 0 AND pc.amount > 0 AND pc.preorderdate BETWEEN :startDate AND :endDate "
                            + "UNION "
                            + "SELECT pmd.preorderdate, cast(sum(pmd.amount * pmd.menudetailprice) AS BIGINT), pmd.idofclient, c.balance, p.surname, p.firstname, c.gender "
                            + "FROM cf_preorder_menudetail pmd JOIN cf_clients c ON pmd.idofclient = c.idofclient "
                            + "JOIN cf_clientsnotificationsettings n ON c.idofclient = n.idofclient AND n.notifytype = :notifyType "
                            + "JOIN cf_persons p ON p.idofperson = c.idofperson "
                            + "WHERE pmd.deletedstate = 0 AND pmd.amount > 0 AND pmd.preorderdate BETWEEN :startDate AND :endDate "
                            + "GROUP BY pmd.idofclient, p.surname, p.firstname, c.balance, pmd.preorderdate, c.gender "
                            + "UNION "
                            + "SELECT pc.preorderdate, cast(pc.amount * pc.complexprice AS BIGINT), c.idofclient, c.balance, p.surname, p.firstname, c.gender "
                            + "FROM cf_client_guardian_notificationsettings n JOIN cf_client_guardian cg ON n.idofclientguardian = cg.idofclientguardian AND n.notifytype = :notifyType "
                            + "JOIN cf_clients c ON c.idofclient = cg.idofchildren "
                            + "JOIN cf_preorder_complex pc ON pc.idofclient = c.idofclient "
                            + "JOIN cf_persons p ON p.idofperson = c.idofperson "
                            + "WHERE pc.deletedstate = 0 AND pc.amount > 0 AND pc.preorderdate BETWEEN :startDate AND :endDate "
                            + "UNION "
                            + "SELECT pmd.preorderdate, cast(sum(pmd.amount * pmd.menudetailprice) AS BIGINT), pmd.idofclient, c.balance, p.surname, p.firstname, c.gender "
                            + "FROM cf_client_guardian_notificationsettings n JOIN cf_client_guardian cg ON n.idofclientguardian = cg.idofclientguardian AND n.notifytype = :notifyType "
                            + "JOIN cf_clients c ON c.idofclient = cg.idofchildren "
                            + "JOIN cf_preorder_menudetail pmd ON pmd.idofclient = c.idofclient "
                            + "JOIN cf_persons p ON p.idofperson = c.idofperson "
                            + "WHERE pmd.deletedstate = 0 AND pmd.amount > 0 AND pmd.preorderdate BETWEEN :startDate AND :endDate "
                            + "GROUP BY pmd.idofclient, p.surname, p.firstname, c.balance, pmd.preorderdate, c.gender "
                            + "ORDER BY 1, 3";
            Query pQuery = entityManager.createNativeQuery(preorders_query);
            pQuery.setParameter("startDate", System.currentTimeMillis());
            pQuery.setParameter("endDate", CalendarUtils.addDays(new Date(), 7).getTime());
            pQuery.setParameter("notifyType", notifyType);
            List pList = pQuery.getResultList();
            for (Object obj : pList) {
                Object[] row = (Object[]) obj;
                Date date = new Date(((BigInteger) row[0]).longValue());
                Long sum = ((BigInteger) row[1]).longValue();
                long id = ((BigInteger) row[2]).longValue();
                long balance = ((BigInteger) row[3]).longValue();
                String surname = (String) row[4];
                String firstname = (String) row[5];
                clientEE = findClientEEByClientId(clients, id);
                if (clientEE == null) {
                    clientEE = new ClientEE();
                    clientEE.setIdOfClient(id);
                    clientEE.setSurname(surname);
                    clientEE.setFirstname(firstname);
                    clientEE.setValues(result.clone());
                    clientEE.setValues(attachGenderToValues((Integer) row[6], clientEE.getValues()));
                    clients.add(clientEE);
                }
                clientEE.getPreorderWeekly().getPreorderDate().add(date);
                clientEE.getPreorderWeekly().setPreorderSumm(clientEE.getPreorderWeekly().getPreorderSumm() + sum);
                clientEE.getPreorderWeekly().setBalance(balance);
            }
            attachPreorderWeeklyValues(clients);
        }

        for (ClientEE cEE : clients) {
            String q =
                    "SELECT cg.disabled, cg.idofguardian, p.firstname, p.surname FROM cf_client_guardian_notificationsettings n "
                            + "INNER JOIN cf_client_guardian cg ON n.idofclientguardian = cg.idofclientguardian "
                            + "INNER JOIN cf_clients cl ON cg.idofguardian = cl.idofclient "
                            + "INNER JOIN cf_persons p ON p.idofperson = cl.idofperson WHERE cg.idofchildren = :idOfClient AND cg.deletedState = FALSE AND n.notifyType = :notifyType ";
            Query gquery = entityManager.createNativeQuery(q);
            gquery.setParameter("idOfClient", cEE.getIdOfClient());
            gquery.setParameter("notifyType", notifyType);
            List glist = gquery.getResultList();
            boolean guardExists = false;
            for (Object obj : glist) {
                Object[] row = (Object[]) obj;
                Integer disabled = ((Integer) row[0]).intValue();
                Long id = ((BigInteger) row[1]).longValue();
                String firstname = (String) row[2];
                String surname = (String) row[3];
                if (disabled < 1) {
                    guardExists = true;
                    String guardian = id.toString() + "|" + firstname + "|" + surname;
                    cEE.getGuardians().add(guardian);
                } else {
                    continue;
                }
            }
            if (glist.size() > 0 && !guardExists) {
                cEE.setNotInform(true);
            }
        }

        return clients;
    }

    private void attachPreorderDailyValues(List<ClientEE> clients) {
        for (ClientEE clientEE : clients) {
            if (!clientEE.getPreorders().getDeletedPreorderDateGuardian().isEmpty()) {
                int count = 1;
                for (NotifyPreorderDailyDetail notifyPreorderDailyDetail : clientEE.getPreorders()
                        .getDeletedPreorderDateGuardian()) {
                    clientEE.setValues(
                            setDeletedPreorderDate(clientEE.getValues(), GUARDIAN, notifyPreorderDailyDetail, count));
                    count++;
                }
            }

            if (!clientEE.getPreorders().getDeletedPreorderDateOther().isEmpty()) {
                int count = 1;
                for (NotifyPreorderDailyDetail notifyPreorderDailyDetail : clientEE.getPreorders()
                        .getDeletedPreorderDateOther()) {
                    clientEE.setValues(
                            setDeletedPreorderDate(clientEE.getValues(), OTHER, notifyPreorderDailyDetail, count));
                    count++;
                }
            }
        }
    }

    private String[] setDeletedPreorderDate(String[] values, String type,
            NotifyPreorderDailyDetail notifyPreorderDailyDetail, int count) {
        values = attachValue(values, DELETED_PREORDER_DATE + type + REGULAR + count,
                CalendarUtils.dateToString(notifyPreorderDailyDetail.getDeleteDate()));
        values = attachValue(values, END_DATE_PREORDER + type + REGULAR + count,
                CalendarUtils.dateToString(notifyPreorderDailyDetail.getEndDate()));
        values = attachValue(values, COMPLEX_NAME_PREORDER + type + REGULAR + count,
                notifyPreorderDailyDetail.getComplexName() == null ? "" : notifyPreorderDailyDetail.getComplexName());
        values = attachValue(values, DISH_NAME_PREORDER + type + REGULAR + count,
                notifyPreorderDailyDetail.getDishName() == null ? "" : notifyPreorderDailyDetail.getDishName());
        return values;
    }

    private String getPreorderDates(Set<Date> dates) {
        String result = "";
        for (Date date : dates) {
            result += CalendarUtils.dateToString(date) + ",";
        }
        return result.substring(0, result.length() - 1);
    }

    private void attachPreorderWeeklyValues(List<ClientEE> clients) {
        for (ClientEE clientEE : clients) {
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_PREORDER_DATE,
                    clientEE.getPreorderWeekly().getPreorderDate().size() == 0 ? ""
                            : getPreorderDates(clientEE.getPreorderWeekly().getPreorderDate())));
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_PREORDER_SUMM,
                    CurrencyStringUtils.copecksToRubles(clientEE.getPreorderWeekly().getPreorderSumm())));
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_PREORDER_LACKS_SUMM,
                    clientEE.getPreorderWeekly().getBalance() - clientEE.getPreorderWeekly().getPreorderSumm() > 0 ? "0"
                            : CurrencyStringUtils.copecksToRubles(
                                    clientEE.getPreorderWeekly().getPreorderSumm() - clientEE.getPreorderWeekly()
                                            .getBalance())));
        }
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
        Long seconds;
        Long hours;
        Long minutes;
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
            //clientEE.setValues(attachValue(clientEE.getValues(), VALUE_ORG_NUM, clientEE.getOrgNum()));

            int upto = 0;
            if (notifyType.equals(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_DAY.getValue())) {
                upto = 1;
            } else if (notifyType
                    .equals(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_WEEK.getValue())) {
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
                    if (ee.getEvtDateTime() > startDate.getTime() + iteration * MILLIS_IN_DAY) {
                        //перешли в следующий день
                        break;
                    }

                    if (!firstEnterFound) {
                        checkFirstEnter(ee); //здесь устанавливается время первого события входа
                    }
                    if (ee.getPassDirection() == EnterEvent.ENTRY || ee.getPassDirection() == EnterEvent.RE_ENTRY
                            || ee.getPassDirection() == EnterEvent.DETECTED_INSIDE
                            || ee.getPassDirection() == EnterEvent.CHECKED_BY_TEACHER_EXT) {
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
                                clientEE.setValues(attachValue(clientEE.getValues(), VALUE_TIME_ENTER,
                                        CalendarUtils.dateTimeToString(new Date(enterTime))));
                                clientEE.setValues(attachValue(clientEE.getValues(), VALUE_TIME_EXIT, "-"));
                            }
                            if (exitExists) {
                                clientEE.setValues(attachValue(clientEE.getValues(), VALUE_TIME_ENTER, "-"));
                                clientEE.setValues(attachValue(clientEE.getValues(), VALUE_TIME_EXIT,
                                        CalendarUtils.dateTimeToString(new Date(exitTime))));
                            }
                            clientEE.setValues(
                                    attachValue(clientEE.getValues(), VALUE_AMOUNT_ENTER_EVENTS_TIME_MAX, "+"));
                            attachEnterMethod(enterExists, session, clientEE);
                            attachExitMethod(exitExists, session, clientEE);
                            continue;
                        }
                        seconds = clientInside / 1000;
                        hours = seconds / 3600;
                        minutes = (seconds / 60) % 60;

                        clientEE.setValues(attachValue(clientEE.getValues(), VALUE_AMOUNT_ENTER_EVENTS_TIME_MAX,
                                hours.toString() + ":" + minutes.toString()));
                        clientEE.setValues(attachValue(clientEE.getValues(), VALUE_TIME_ENTER,
                                CalendarUtils.dateTimeToString(new Date(enterTime))));
                        clientEE.setValues(attachValue(clientEE.getValues(), VALUE_TIME_EXIT,
                                CalendarUtils.dateTimeToString(new Date(exitTime))));
                    }
                    attachEnterMethod(enterExists, session, clientEE);
                    attachExitMethod(exitExists, session, clientEE);
                } else if (notifyType
                        .equals(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_WEEK.getValue())) {
                    String day = getDayByIteration(iteration, 0);
                    if (clientInside != 0) {
                        seconds = clientInside / 1000;
                        hours = seconds / 3600;
                        minutes = (seconds / 60) % 60;
                        clientEE.setValues(
                                attachValue(clientEE.getValues(), day, hours.toString() + ":" + minutes.toString()));
                    } else {
                        if (enterExists || exitExists) {
                            clientEE.setValues(attachValue(clientEE.getValues(), day, "Нет информации о выходе"));
                        } else {
                            clientEE.setValues(attachValue(clientEE.getValues(), day, "Нет информации о входе"));
                        }
                    }
                    String day2 = getDayByIteration(iteration, 1);
                    String day3 = getDayByIteration(iteration, 2);
                    if (enterExists) {
                        clientEE.setValues(
                                attachValue(clientEE.getValues(), day2, CalendarUtils.timeToString(enterTime)));
                    } else {
                        clientEE.setValues(attachValue(clientEE.getValues(), day2, ""));
                    }
                    if (exitExists) {
                        clientEE.setValues(
                                attachValue(clientEE.getValues(), day3, CalendarUtils.timeToString(exitTime)));
                    } else {
                        clientEE.setValues(attachValue(clientEE.getValues(), day3, ""));
                    }
                }
            }
            if (enterExists || exitExists) {
                daysInside++;
            }
            if (notifyType.equals(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_WEEK.getValue())) {
                clientEE.setValues(
                        attachValue(clientEE.getValues(), VALUE_AMOUNT_ENTER_EVENTS_DATE, daysInside.toString()));
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
                            Person person = ((Client) session.load(Client.class, enterEmployeeId)).getPerson();
                            enterMethod = String.format("Сотрудником ОО (%s)", person.getFullName());
                        } else {
                            Person person = ((Client) session.load(Client.class, enterGuardianId)).getPerson();
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
                    Person person = ((Client) session.load(Client.class, exitEmployeeId)).getPerson();
                    exitMethod = String.format("Сотрудником ОО (%s)", person.getFullName());
                } else {
                    Person person = ((Client) session.load(Client.class, exitGuardianId)).getPerson();
                    exitMethod = String.format("Представитель (%s)", person.getFullName());
                }
            }
        }
        clientEE.setValues(attachValue(clientEE.getValues(), VALUE_METHOD_END, exitMethod));
    }

    private void attachBalanceValues(List<ClientEE> clients, Long notifyType) {
        for (ClientEE clientEE : clients) {
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_ACCOUNT,
                    clientEE.getBalances().getAccount() == null ? "" : clientEE.getBalances().getAccount().toString()));
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_BALANCE_START_DATE, getStringMoneyValue(
                    clientEE.getBalances().getBalanceStartDate() == null ? 0L
                            : clientEE.getBalances().getBalanceStartDate())));
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_BALANCE, getStringMoneyValue(
                    clientEE.getBalances().getBalance() == null ? 0L : clientEE.getBalances().getBalance())));
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_AMOUNT_BUY_ALL, getStringMoneyValue(
                    clientEE.getBalances().getAmountBuyAll() == null ? 0L : clientEE.getBalances().getAmountBuyAll())));
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_LIMIT, getStringMoneyValue(
                    clientEE.getBalances().getLimit() == null ? 0L : clientEE.getBalances().getLimit())));
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_AMOUNT_COMPLEX_DATE,
                    clientEE.getBalances().getAmountComplexDate() == null ? ""
                            : clientEE.getBalances().getAmountComplexDate().toString()));
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_QUANTITY_AMOUNT,
                    clientEE.getBalances().getQuantityAmount() == null ? ""
                            : clientEE.getBalances().getQuantityAmount().toString()));
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_PAYMENT_SUM, getStringMoneyValue(
                    clientEE.getBalances().getPaymentSum() == null ? 0L : clientEE.getBalances().getPaymentSum())));
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_MENU_DETAIL,
                    clientEE.getBalances().getMenu_detail() == null ? "" : clientEE.getBalances().getMenu_detail()));
            Date date = new Date(System.currentTimeMillis());
            if (clientEE.getBalances().getBalanceOnDays() != null) {
                date = CalendarUtils.addDays(date, clientEE.getBalances().getBalanceOnDays());
            }
            clientEE.setValues(attachValue(clientEE.getValues(), VALUE_BALANCE_DAYS,
                    clientEE.getBalances().getBalanceOnDays() == null ? "" : CalendarUtils.dateToString(date)));

            if (notifyType.equals(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_WEEK.getValue())) {
                clientEE.setValues(attachValue(clientEE.getValues(), VALUE_AMOUNT_MONDAY,
                        clientEE.getBalances().getOrder1() == null || clientEE.getBalances().getOrder1() == 0L ? "Нет"
                                : "Да"));
                clientEE.setValues(attachValue(clientEE.getValues(), VALUE_AMOUNT_TUESDAY,
                        clientEE.getBalances().getOrder2() == null || clientEE.getBalances().getOrder2() == 0L ? "Нет"
                                : "Да"));
                clientEE.setValues(attachValue(clientEE.getValues(), VALUE_AMOUNT_WEDNESDAY,
                        clientEE.getBalances().getOrder3() == null || clientEE.getBalances().getOrder3() == 0L ? "Нет"
                                : "Да"));
                clientEE.setValues(attachValue(clientEE.getValues(), VALUE_AMOUNT_THURSDAY,
                        clientEE.getBalances().getOrder4() == null || clientEE.getBalances().getOrder4() == 0L ? "Нет"
                                : "Да"));
                clientEE.setValues(attachValue(clientEE.getValues(), VALUE_AMOUNT_FRIDAY,
                        clientEE.getBalances().getOrder5() == null || clientEE.getBalances().getOrder5() == 0L ? "Нет"
                                : "Да"));
                clientEE.setValues(attachValue(clientEE.getValues(), VALUE_AMOUNT_SATURDAY,
                        clientEE.getBalances().getOrder6() == null || clientEE.getBalances().getOrder6() == 0L ? "Нет"
                                : "Да"));
                clientEE.setValues(attachValue(clientEE.getValues(), VALUE_AMOUNT_SUNDAY,
                        clientEE.getBalances().getOrder7() == null || clientEE.getBalances().getOrder7() == 0L ? "Нет"
                                : "Да"));
            }
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
        newValues[newValues.length - 2] = name;
        newValues[newValues.length - 1] = value;
        return newValues;
    }

    private void checkFirstEnter(EE ee) {
        if (ee.getPassDirection() == EnterEvent.ENTRY || ee.getPassDirection() == EnterEvent.RE_ENTRY
                || ee.getPassDirection() == EnterEvent.DETECTED_INSIDE
                || ee.getPassDirection() == EnterEvent.CHECKED_BY_TEACHER_EXT) {
            enterTime = ee.getEvtDateTime();
            enterGuardianId = ee.getGuardianId();
            enterEmployeeId = ee.getChildPassCheckerId();
            enterPassDirection = ee.getPassDirection();
            firstEnterFound = true;
        }
    }

    public final String[] attachGenderToValues(Integer gender, String[] values) {
        if (null == gender) {
            return values;
        }

        String genderString;
        switch (gender) {
            case 0:
                genderString = CLIENT_GENDER_VALUE_FEMALE;
                break;
            case 1:
                genderString = CLIENT_GENDER_VALUE_MALE;
                break;
            default:
                return values;
        }

        return attachValue(values, CLIENT_GENDER_KEY, genderString);
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
        private Boolean notInform;
        private List<String> guardians;
        private NotifyPreorderDaily preorders;
        private NotifyPreorderWeekly preorderWeekly;

        private ClientEE() {
            this.eeList = new ArrayList<EE>();
            this.setBalances(new Balances());
            this.notInform = false;
            this.guardians = new ArrayList<String>();
            this.setValues(new String[0]);
            this.preorders = new NotifyPreorderDaily();
            this.preorderWeekly = new NotifyPreorderWeekly();
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

        public Boolean getNotInform() {
            return notInform;
        }

        public void setNotInform(Boolean notInform) {
            this.notInform = notInform;
        }

        public List<String> getGuardians() {
            return guardians;
        }

        public void setGuardians(List<String> guardians) {
            this.guardians = guardians;
        }

        public NotifyPreorderDaily getPreorders() {
            return preorders;
        }

        public void setPreorders(NotifyPreorderDaily preorders) {
            this.preorders = preorders;
        }

        public NotifyPreorderWeekly getPreorderWeekly() {
            return preorderWeekly;
        }

        public void setPreorderWeekly(NotifyPreorderWeekly preorderWeekly) {
            this.preorderWeekly = preorderWeekly;
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
        private Long order1;
        private Long order2;
        private Long order3;
        private Long order4;
        private Long order5;
        private Long order6;
        private Long order7;

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

        public Long getOrder1() {
            return order1;
        }

        public void setOrder1(Long order1) {
            this.order1 = order1;
        }

        public Long getOrder2() {
            return order2;
        }

        public void setOrder2(Long order2) {
            this.order2 = order2;
        }

        public Long getOrder3() {
            return order3;
        }

        public void setOrder3(Long order3) {
            this.order3 = order3;
        }

        public Long getOrder4() {
            return order4;
        }

        public void setOrder4(Long order4) {
            this.order4 = order4;
        }

        public Long getOrder5() {
            return order5;
        }

        public void setOrder5(Long order5) {
            this.order5 = order5;
        }

        public Long getOrder6() {
            return order6;
        }

        public void setOrder6(Long order6) {
            this.order6 = order6;
        }

        public Long getOrder7() {
            return order7;
        }

        public void setOrder7(Long order7) {
            this.order7 = order7;
        }
    }

    public static class NotifyPreorderDaily {

        //private long idOfClient;
        private Set<NotifyPreorderDailyDetail> deletedPreorderDateGuardian;
        private Set<NotifyPreorderDailyDetail> deletedPreorderDateOther;

        public NotifyPreorderDaily() {
            this.deletedPreorderDateGuardian = new HashSet<>();
            this.deletedPreorderDateOther = new HashSet<>();
        }

        public Set<Date> getDateGuardian() {
            Set<Date> dates = new TreeSet<Date>();
            for (NotifyPreorderDailyDetail notifyPreorderDailyDetail : deletedPreorderDateGuardian) {
                dates.add(notifyPreorderDailyDetail.getEndDate());
            }
            return dates;
        }

        public Set<Date> getDateOther() {
            Set<Date> dates = new TreeSet<Date>();
            for (NotifyPreorderDailyDetail notifyPreorderDailyDetail : deletedPreorderDateOther) {
                dates.add(notifyPreorderDailyDetail.getEndDate());
            }
            return dates;
        }

        public Set<NotifyPreorderDailyDetail> getDeletedPreorderDateGuardian() {
            return deletedPreorderDateGuardian;
        }

        public void setDeletedPreorderDateGuardian(Set<NotifyPreorderDailyDetail> deletedPreorderDateGuardian) {
            this.deletedPreorderDateGuardian = deletedPreorderDateGuardian;
        }

        public Set<NotifyPreorderDailyDetail> getDeletedPreorderDateOther() {
            return deletedPreorderDateOther;
        }

        public void setDeletedPreorderDateOther(Set<NotifyPreorderDailyDetail> deletedPreorderDateOther) {
            this.deletedPreorderDateOther = deletedPreorderDateOther;
        }
    }

    public static class NotifyPreorderDailyDetail {

        private Date deleteDate;
        private Date endDate;
        private String complexName;
        private String dishName;


        public Date getEndDate() {
            return endDate;
        }

        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }

        public String getComplexName() {
            return complexName;
        }

        public void setComplexName(String complexName) {
            this.complexName = complexName;
        }

        public String getDishName() {
            return dishName;
        }

        public void setDishName(String dishName) {
            this.dishName = dishName;
        }

        public Date getDeleteDate() {
            return deleteDate;
        }

        public void setDeleteDate(Date deleteDate) {
            this.deleteDate = deleteDate;
        }
    }

    public static class NotifyPreorderWeekly {

        private Set<Date> preorderDate;
        private Long preorderSumm;
        private Long preorderLacksSumm;
        private Long balance;

        public NotifyPreorderWeekly() {
            this.setPreorderDate(new TreeSet<Date>());
            this.preorderSumm = 0L;
            this.preorderLacksSumm = 0L;
            this.balance = 0L;
        }

        public Set<Date> getPreorderDate() {
            return preorderDate;
        }

        public void setPreorderDate(Set<Date> preorderDate) {
            this.preorderDate = preorderDate;
        }

        public Long getPreorderSumm() {
            return preorderSumm;
        }

        public void setPreorderSumm(Long preorderSumm) {
            this.preorderSumm = preorderSumm;
        }

        public Long getPreorderLacksSumm() {
            return preorderLacksSumm;
        }

        public void setPreorderLacksSumm(Long preorderLacksSumm) {
            this.preorderLacksSumm = preorderLacksSumm;
        }

        public Long getBalance() {
            return balance;
        }

        public void setBalance(Long balance) {
            this.balance = balance;
        }
    }

    public class PreorderData {

        private Date date;
        private Integer state;
        private long id;
        private String surname;
        private String firstname;
        private Integer gender;
        private List<String> ids;

        PreorderData() {
            ids = new ArrayList<>();
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Integer getState() {
            return state;
        }

        public void setState(Integer state) {
            this.state = state;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
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

        public Integer getGender() {
            return gender;
        }

        public void setGender(Integer gender) {
            this.gender = gender;
        }

        public List<String> getIds() {
            return ids;
        }

        public void setIds(List<String> ids) {
            this.ids = ids;
        }
    }

    public class PreorderRegularData {
        private Integer stateReg;
        private Date lastUpdate;
        private Date endDateReg;
        private String itemCode;
        private String itemname;
        private Long complexId;
        private Date createDate;

        public Integer getStateReg() {
            return stateReg;
        }

        public void setStateReg(Integer stateReg) {
            this.stateReg = stateReg;
        }

        public Date getLastUpdate() {
            return lastUpdate;
        }

        public void setLastUpdate(Date lastUpdate) {
            this.lastUpdate = lastUpdate;
        }

        public Date getEndDateReg() {
            return endDateReg;
        }

        public void setEndDateReg(Date endDateReg) {
            this.endDateReg = endDateReg;
        }

        public String getItemCode() {
            return itemCode;
        }

        public void setItemCode(String itemCode) {
            this.itemCode = itemCode;
        }

        public String getItemname() {
            return itemname;
        }

        public void setItemname(String itemname) {
            this.itemname = itemname;
        }

        public Long getComplexId() {
            return complexId;
        }

        public void setComplexId(Long complexId) {
            this.complexId = complexId;
        }

        public Date getCreateDate() {
            return createDate;
        }

        public void setCreateDate(Date createDate) {
            this.createDate = createDate;
        }
    }
}
