/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ConsumerRequestDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DocumentState;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.UnitScale;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequest;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.Staff;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.sync.handlers.requests.supplier.RequestsSupplierDetailTypeEnum;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.*;

@Component("PreorderRequestsReportService")
@Scope("singleton")
public class PreorderRequestsReportService extends RecoverableService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PreorderRequestsReportService.class);

    public final String CRON_EXPRESSION_PROPERTY = "ecafe.processor.report.PreorderRequestsReport.cronExpression";
    public static final String NODE_PROPERTY = "ecafe.processor.report.PreorderRequestsReport.node";
    public static final String STATUS_FILENAME_PROPERTY = "ecafe.processor.report.PreorderRequestsReport.status.filename";
    private final String STATUS_FILENAME_DEFAULT_VALUE = "/home/jbosser/processor/tasks/PreorderRequestsReport.";

    public static final String PREORDER_COMMENT = "- Добавлено из предзаказа -";
    private static final String TEMPLATE_FILENAME = "PreordersRequestsReport_notify.jasper";
    public final Integer PREORDER_REQUEST_TYPE = 3;
    public static final Integer MAX_FORBIDDEN_DAYS = 3;
    public static final int DAY_PREORDER_CHECK = 5;

    private Map<Long, GoodRequestsChangeAsyncNotificationService.OrgItem> orgItems = new HashMap<Long, GoodRequestsChangeAsyncNotificationService.OrgItem>();

    public void run() throws Exception {
        if (!isOn())
            return;

        updateStatusFile(new Date(), Status.RUNNING);
        runTask();
        updateStatusFile(new Date(), Status.FINISHED);
    }

    @Override
    public void recoveryRun() throws Exception {
        if (!isOn())
            return;
        logger.info("Start recoveryRun for generate preorder requests");
        SchedulerFactory sfb = new StdSchedulerFactory();
        Scheduler scheduler = sfb.getScheduler();
        Trigger trigger = scheduler.getTrigger("PreorderRequestsReport", Scheduler.DEFAULT_GROUP);
        if (trigger != null) {
            Date date = trigger.getNextFireTime();
            if (CalendarUtils.isDateToday(date) && date.after(new Date())) {
                logger.info("Do not execute recoveryRun for generate preorder requests. Wrong run time");
                return;
            }
        }

        if (isFinishedToday()) {
            logger.info("Do not execute recoveryRun for generate preorder requests. Success finish found");
            return;
        }

        logger.info("Start execute recoveryRun for generate preorder requests.");
        updateStatusFile(new Date(), Status.RUNNING);
        //runGeneratePreorderRequests(new PreorderRequestsReportServiceParam(new Date()));
        runTask();
        updateStatusFile(new Date(), Status.FINISHED);
    }

    public static boolean isOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getConfigProperties().getProperty(PreorderRequestsReportService.NODE_PROPERTY, "1");
        String[] nodes = reqInstance.split(",");
        for (String node : nodes) {
            if (!StringUtils.isBlank(instance) && !StringUtils.isBlank(reqInstance) && instance.trim().equals(node.trim())) {
                return true;
            }
        }
        return false;
    }

    private void deletePreorderForNotEnoughMoney(Session session, PreorderItem item) {
        Long version = DAOUtils.nextVersionByPreorderComplex(session);
        if (item.getIdOfPreorderComplex() != null) {
            PreorderComplex.delete(session, item.getIdOfPreorderComplex(), version, PreorderState.NOT_ENOUGH_BALANCE);
        }
    }

    private void deletePreorderForChangedCalendar(Session session, PreorderItem item) {
        Long version = DAOUtils.nextVersionByPreorderComplex(session);
        if (item.getIdOfPreorderComplex() != null) {
            PreorderComplex.delete(session, item.getIdOfPreorderComplex(), version, PreorderState.CHANGED_CALENDAR);
        }
    }

    public void runGeneratePreorderRequests(PreorderRequestsReportServiceParam params) {
        try {
            Date fireTime = new Date();
            Date date = CalendarUtils.addHours(CalendarUtils.startOfDay(params.getDate()), 12);
            Date currentDate = CalendarUtils.startOfDayInUTC(date); // CalendarUtils.addHours(CalendarUtils.startOfDay(date), 3);
            if (DAOService.getInstance().getProductionCalendarByDate(currentDate) != null) return; //в выходной день заявки не формируем
            logger.info("Start generating preorder requests " + new Date().getTime());
            List<Date> weekends = GoodRequestsChangeAsyncNotificationService.getInstance().getProductionCalendarDates(date);
            Map<Long, GoodRequestsChangeAsyncNotificationService.OrgItem> orgItemsLocal = GoodRequestsChangeAsyncNotificationService.getInstance().findOrgItems2(false, params); //орги с включенным флагом предзаказа

            Integer maxDays = getMaxDateToCreateRequests(currentDate, weekends, MAX_FORBIDDEN_DAYS);
            Date dateTo = CalendarUtils.addDays(currentDate, maxDays);
            logger.info("Start get info runGeneratePreorderRequests " + new Date().getTime());
            List<PreorderItem> preorderItemList = loadPreorders2(date, dateTo, params); //предзаказы от завтрашнего дня до дня, на который максимум можем сгенерить заявки
            List<OrgGoodRequest> doneOrgGoodRequests = GoodRequestsChangeAsyncNotificationService.getInstance().getDoneOrgGoodRequests(dateTo, params);
            Map<Long, List<SpecialDate>> mapSpecialDates = getAllSpecialDates(orgItemsLocal, date, dateTo);
            List<ProductionCalendar> productionCalendar = DAOReadonlyService.getInstance().getProductionCalendar(date, dateTo);

            Map<Long, Map<Date, Long>> clientBalances = getClientBalancesOnDates(preorderItemList, mapSpecialDates, productionCalendar); //балансы клиентов на даты с учетом предзаказов
            logger.info("End get info runGeneratePreorderRequests " + new Date().getTime());
            Integer sizeOrgs = orgItemsLocal.size();
            int counterOrgs = 0;
            Session session = null;
            Transaction transaction = null;
            try {
                session = RuntimeContext.getInstance().createPersistenceSession();
                session.setFlushMode(FlushMode.COMMIT);

                for (Map.Entry<Long, GoodRequestsChangeAsyncNotificationService.OrgItem> entry : orgItemsLocal.entrySet()) {
                    counterOrgs++;
                    long idOfOrg = entry.getKey();
                    logger.info(String.format("Generating preorder requests. orgID=%s (№%s from %s)", idOfOrg, counterOrgs, sizeOrgs) + new Date().getTime());

                    List<SpecialDate> specialDates = mapSpecialDates.get(idOfOrg); //DAOReadonlyService.getInstance().getSpecialDates(CalendarUtils.addHours(currentDate, 12), dateTo, idOfOrg);

                    GoodRequestsChangeAsyncNotificationService.OrgItem orgItem = entry.getValue();

                    Long number = DAOUtils.getNextGoodRequestNumberForOrgPerDay(session, idOfOrg, new Date());
                    Staff staff = DAOUtils.getAdminStaffFromOrg(session, idOfOrg);

                    //вычисляем даты, на которые нужно генерировать заявки для текущей ОО
                    List<Date> orgDates;
                    if (params.isEmpty()) {
                        orgDates = getOrgDates(currentDate, idOfOrg, weekends);
                    } else {
                        orgDates = new ArrayList<>();
                        orgDates.add(currentDate);
                    }
                    List<String> guids = new ArrayList<String>();
                    for (Date dateWork : orgDates) {
                        guids.clear();
                        if (getOrgGoodRequestByDate(idOfOrg, dateWork, doneOrgGoodRequests) != null && params.isEmpty()) {
                            logger.info(String.format("Requests for orgID=%s on date=%s already exist", idOfOrg, CalendarUtils.dateToString(dateWork)));
                            continue;
                        }
                        Boolean isWeekend = isWeekendByProductionCalendar(currentDate, productionCalendar);
                        try {
                            transaction = session.beginTransaction();
                            List<PreorderItem> preordersByOrg = getPreorderItemsByOrg(idOfOrg, preorderItemList, dateWork); //предзаказы по ОО на дату
                            logger.info(String.format("Found %s preorders on %s", preordersByOrg.size(), CalendarUtils.dateToString(dateWork)));
                            for (PreorderItem item : preordersByOrg) {
                                try {
                                    Org org = DAOUtils.getOrgById(session, idOfOrg);
                                    if (null == item.getIdOfGood() && !org.getUseWebArm()) {
                                        logger.error("Preorder without good item was found " + item.toString());
                                        continue;
                                    }
                                    if (isWeekendBySpecialDateAndSixWorkWeek(isWeekend, dateWork, item.getIdOfClientGroup(), idOfOrg, specialDates)
                                            || isHolidayByProductionCalendar(dateWork, productionCalendar)) {
                                        deletePreorderForChangedCalendar(session, item);
                                        logger.info("Delete preorder for changed calendar " + item.toString());
                                        continue;
                                    }
                                    long balanceOnDate = getBalanceOnDate(item.getIdOfClient(), dateWork, clientBalances);
                                    if (balanceOnDate < 0L) {
                                        deletePreorderForNotEnoughMoney(session, item);
                                        logger.info("Delete preorder for not enough money " + item.toString() + " balance=" + balanceOnDate
                                                + "dateWork=" + CalendarUtils.dateToString(dateWork));
                                        continue;
                                    }
                                    String guid = createRequestFromPreorder2(session, item, fireTime, number, staff);
                                    if (null != guid) {
                                        number++;
                                        guids.add(guid);
                                    }
                                } catch (Exception e) {
                                    logger.error("Error in create request for item = " + item.toString());
                                }
                            }

                            OrgGoodRequest orgGoodRequest = new OrgGoodRequest(idOfOrg, dateWork);
                            session.save(orgGoodRequest);
                            DAOUtils.savePreorderDirectiveWithValue(session, idOfOrg, true);
                        } catch (Exception e) {
                            transaction.rollback();
                            logger.error(String.format("Error in generate request for orgID = %s: ", idOfOrg), e);
                        } finally {
                            if (transaction.isActive()) transaction.commit();
                            transaction = null;
                        }
                        /*if (guids.size() > 0) {
                            logger.info(String.format("Sending requests to orgID=%s, count=%s", idOfOrg, guids.size()));
                            Calendar calendarEnd = RuntimeContext.getInstance().getDefaultLocalCalendar(null);
                            final Date lastCreateOrUpdateDate = calendarEnd.getTime();
                            calendarEnd.add(Calendar.MINUTE, 1);
                            final Date endGenerateTime = calendarEnd.getTime();
                            RuntimeContext.getAppContext().getBean(GoodRequestsChangeAsyncNotificationService.class)
                                    .notifyOrg(orgItem, fireTime, endGenerateTime, lastCreateOrUpdateDate, dateWork);
                        }*/
                    }

                }

            } catch (Exception e) {
                logger.error("Error in generate preorder requests: ", e);
            } finally {
                HibernateUtils.rollback(transaction, logger);
                HibernateUtils.close(session, logger);
            }

        } catch (Exception e) {
            logger.error("Error in generating preorder requests: ", e);
        }
        logger.info("End generating preorder requests");
    }

    private Map<Long, List<SpecialDate>> getAllSpecialDates(Map<Long, GoodRequestsChangeAsyncNotificationService.OrgItem> orgItems, Date date, Date dateTo) {
        Map<Long, List<SpecialDate>> mapSpecialDates = new HashMap<Long, List<SpecialDate>>();
        for (Map.Entry<Long, GoodRequestsChangeAsyncNotificationService.OrgItem> entry : orgItems.entrySet()) {
            long idOfOrg = entry.getKey();
            List<SpecialDate> specialDates = DAOReadonlyService.getInstance().getSpecialDates(CalendarUtils.addHours(date, 12), dateTo, idOfOrg);
            mapSpecialDates.put(idOfOrg, specialDates);
        }
        return mapSpecialDates;
    }

    public Boolean isWeekendBySpecialDate(Date date, Long idOfClientGroup, List<SpecialDate> specialDates) {
        Boolean isWeekend = null;
        if(specialDates != null){
            for (SpecialDate specialDate : specialDates) {
                if (CalendarUtils.betweenOrEqualDate(specialDate.getDate(), date, CalendarUtils.addDays(date, 1)) && !specialDate.getDeleted()) {
                    if (specialDate.getIdOfClientGroup() == null || specialDate.getIdOfClientGroup().equals(idOfClientGroup))
                        isWeekend = specialDate.getIsWeekend();
                    if (specialDate.getIdOfClientGroup() != null && specialDate.getIdOfClientGroup().equals(idOfClientGroup))
                        break;
                }
            }
        }
        return isWeekend;
    }

    public boolean isWeekendByProductionCalendar(Date date, List<ProductionCalendar> productionCalendar) {
        for (ProductionCalendar pc : productionCalendar) {
            if (pc.getDay().equals(date)) return true;
        }
        return false;
    }

    public Boolean isWeekendBySpecialDateAndSixWorkWeek(boolean isWeekendByProductionCalendar, Date currentDate, Long idOfClientGroup, Long idOfOrg, List<SpecialDate> specialDates) {
        boolean isWeekend = isWeekendByProductionCalendar;
        Boolean isWeekendSD = isWeekendBySpecialDate(currentDate, idOfClientGroup, specialDates); //выходной по данным таблицы SpecialDates
        int day = CalendarUtils.getDayOfWeek(currentDate);
        if (isWeekendSD == null) { //нет данных по дню в КУД
            if (day == Calendar.SATURDAY) {
                String groupName = DAOReadonlyService.getInstance().getClientGroupName(idOfOrg, idOfClientGroup);
                isWeekend = !DAOReadonlyService.getInstance().isSixWorkWeek(idOfOrg, groupName);
            }
        } else {
            isWeekend = isWeekendSD;
        }
        return isWeekend;
    }
    /**
    * Максимальная дата, на которую нужно построить заявки при количестве дней запрета редактирования предзаказа = 3
    * (3 - максимально возможное количество дней по системе)
    * */
    public Integer getMaxDateToCreateRequests(Date date, List<Date> weekends, Integer maxForbiddenDays) {
        Date d = date;
        int countDays = 0;
        int i = 0;
        while (i < maxForbiddenDays) {
            d = CalendarUtils.addDays(d, 1);
            countDays++;
            if (!weekends.contains(d)) {
                i++;
            }
        }
        return countDays;
    }

    /**
    * Предзаказы по ОО idOfOrg на дату
    * */
    private List<PreorderItem> getPreorderItemsByOrg(long idOfOrg, List<PreorderItem> items, Date date) {
        List<PreorderItem> result = new ArrayList<PreorderItem>();
        for (PreorderItem item : items) {
            if (item.getIdOfOrg().equals(idOfOrg) && item.getPreorderDate().equals(date) && item.getIdOfGoodsRequestPosition() == null) result.add(item);
        }
        return result;
    }

    /**
    * Возвращаем даты, на которые нужно генерировать заявки для ОО idOfOrg с учетом ее forbiddenDays
    * */
    private List<Date> getOrgDates(Date date, long idOfOrg, List<Date> weekends) {
        Integer forbiddenDaysCount = DAOUtils.getPreorderFeedingForbiddenDays(idOfOrg);
        if (forbiddenDaysCount == null) forbiddenDaysCount = PreorderComplex.DEFAULT_FORBIDDEN_DAYS;
        Integer addDays = getMaxDateToCreateRequests(date, weekends, forbiddenDaysCount);
        List<Date> dates = new ArrayList<Date>();
        Date dateToAdd = CalendarUtils.addDays(date, addDays);
        dates.add(dateToAdd);
        Date prevDate = CalendarUtils.addDays(dateToAdd, -1);
        while (weekends.contains(prevDate)) {
            dates.add(prevDate);
            prevDate = CalendarUtils.addDays(prevDate, -1);
        }
        return dates;
    }

    private OrgGoodRequest getOrgGoodRequestByDate(long idOfOrg, Date date, List<OrgGoodRequest> list) {
        for (OrgGoodRequest orgGoodRequest : list) {
            if (orgGoodRequest.getIdOfOrg().equals(idOfOrg) && orgGoodRequest.getDay().equals(date)) {
                return orgGoodRequest;
            }
        }
        return null;
    }

    public Map<Long, Map<Date, Long>> getClientBalancesOnDates(List<PreorderItem> preorderItemList, Map<Long,
            List<SpecialDate>> mapSpecialDates, List<ProductionCalendar> productionCalendar) {
        Map<Long, Map<Date, Long>> result = new TreeMap<Long, Map<Date, Long>>();

        for (PreorderItem item : preorderItemList) {
            Map<Date, Long> map = result.get(item.getIdOfClient());
            List<SpecialDate> specialDates = mapSpecialDates.get(item.getIdOfOrg());
            boolean isWeekend = isWeekendByProductionCalendar(item.getPreorderDate(), productionCalendar);
            if (specialDates != null
                && (
                        isWeekendBySpecialDateAndSixWorkWeek(isWeekend, item.getPreorderDate(), item.getIdOfClientGroup(), item.getIdOfOrg(), specialDates)
                            || isHolidayByProductionCalendar(item.getPreorderDate(), productionCalendar)
                    )
                ) continue;
            if (map == null) {
                map = new TreeMap<Date, Long>();
                map.put(item.getPreorderDate(), item.getClientBalance());
            }
            long balance = map.get(item.getPreorderDate()) == null ? getActualBalance(map) : map.get(item.getPreorderDate());
            map.put(item.getPreorderDate(), balance - item.getComplexPrice() + item.getUsedSum());
            logger.info("idOfClient=" + item.getIdOfClient() + " date=" + CalendarUtils.dateToString(item.getPreorderDate()) +
                         " balance=" + balance + " preorderSum=" + item.getComplexPrice() + " usedSum=" + item.getUsedSum() + " new_balance=" + (
                        balance - item.getComplexPrice() + item.getUsedSum()));
            result.put(item.getIdOfClient(), map);
        }
        return result;
    }

    public boolean isHolidayByProductionCalendar(Date date, List<ProductionCalendar> productionCalendar) {
        for (ProductionCalendar pc : productionCalendar) {
            if (pc.getDay().equals(date) && pc.getFlag().equals(ProductionCalendar.HOLIDAY)) return true;
        }
        return false;
    }

    private long getActualBalance(Map<Date, Long> map) {
        long balance = 0L;
        for (Map.Entry<Date, Long> entry : map.entrySet()) {
            balance = entry.getValue();
        }
        return balance;
    }

    public long getBalanceOnDate(long idOfClient, Date date, Map<Long, Map<Date, Long>> clientBalances) {
        try {
            return clientBalances.get(idOfClient).get(date);
        } catch (Exception e) {
            return -1L;
        }
    }

    public void runTask() throws Exception {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getConfigProperties().getProperty(PreorderRequestsReportService.NODE_PROPERTY, "1");
        String[] nodes = reqInstance.split(",");
        PreorderRequestsReportServiceParam params = new PreorderRequestsReportServiceParam(new Date());
        for (int i = 0; i < nodes.length; i++) {
            if (instance.equals(nodes[i])) {
                params.setModBy(i);
                params.setServersAmount(nodes.length);
                break;
            }
        }
        runTask(params, instance, nodes[0]);
    }

    public void runTask(PreorderRequestsReportServiceParam params, String instance, String firstNode) throws Exception {
        logger.info("Start runTask for generate preorder requests");
        logger.info("Time before checkOrgContragentBinding " + new Date().getTime());
        // проверка на актуальность связок с контрагентами для нового меню
        checkOrgContragentBinding(params);
        logger.info("Time after checkOrgContragentBinding " + new Date().getTime());
        logger.info("Time before relevancePreorders " + new Date().getTime());
        //проверки на актуальность предзаказов
        RuntimeContext.getAppContext().getBean(DAOService.class).getPreorderDAOOperationsImpl().relevancePreorders(params);
        logger.info("Time after relevancePreorders " + new Date().getTime());
        logger.info("Time before generatePreordersBySchedule " + new Date().getTime());
        //генерация предзаказов по регулярному правилу
        RuntimeContext.getAppContext().getBean(DAOService.class).getPreorderDAOOperationsImpl().generatePreordersBySchedule(params);
        logger.info("Time after generatePreordersBySchedule " + new Date().getTime());
        logger.info("Time before runGeneratePreorderRequests " + new Date().getTime());
        //генерация заявок
        runGeneratePreorderRequests(params);
        logger.info("Time after runGeneratePreorderRequests " + new Date().getTime());
        //сервис проверок предзаказов
        if (instance.equals(firstNode)) {
            logger.info("Time before dailyCheckPreorders " + new Date().getTime());
            RuntimeContext.getAppContext().getBean(DAOService.class).getPreorderDAOOperationsImpl().dailyCheckPreorders();
            logger.info("Time after dailyCheckPreorders " + new Date().getTime());
        }
        //
        logger.info("Time before sendNotification" + new Date().getTime());
        //Запуск рассылки отправлений об отмене предзаказа
        PreorderCancelNotificationService.sendNotification.manualStart();
        logger.info("Time after sendNotification" + new Date().getTime());
        //
        logger.info("Finish runTask for gererate preorder requests");
    }

    public void checkOrgContragentBinding(PreorderRequestsReportServiceParam params) {
        logger.info("Start checking org and contragent bindings");
        logger.info("start findOrgSetForContragent " + new Date().getTime());
        Map<Long, Set<Long>> contragentMap = GoodRequestsChangeAsyncNotificationService.getInstance()
                .findOrgSetForContragent(params);
        logger.info("end findOrgSetForContragent " + new Date().getTime());
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            session.setFlushMode(FlushMode.COMMIT);
            transaction = session.beginTransaction();
            for (Long idOfContragent : contragentMap.keySet()) {
                logger.info("start findWtOrgGroupIdsExcludingContragent " + new Date().getTime());
                List<Long> orgGroups = findWtOrgGroupIdsExcludingContragent(session, idOfContragent);
                logger.info("end findWtOrgGroupIdsExcludingContragent " + new Date().getTime());
                logger.info("start findWtMenuIdsExcludingContragent " + new Date().getTime());
                List<Long> menus = findWtMenuIdsExcludingContragent(session, idOfContragent);
                logger.info("end findWtMenuIdsExcludingContragent " + new Date().getTime());
                logger.info("start findWtComplexIdsExcludingContragent " + new Date().getTime());
                List<Long> complexes = findWtComplexIdsExcludingContragent(session, idOfContragent);
                logger.info("start findWtComplexIdsExcludingContragent " + new Date().getTime());
                for (Long idOfOrg : contragentMap.get(idOfContragent)) {
                    logger.info("start deleteWtOrgGroupBindings " + new Date().getTime());
                    if (deleteWtOrgGroupBindings(session, orgGroups, idOfOrg)) {
                        logger.info(String.format("Deleted old orgGroup bindings for idOfOrg = %s", idOfOrg));
                    }
                    logger.info("end deleteWtOrgGroupBindings " + new Date().getTime());
                    logger.info("start deleteWtMenuBindings " + new Date().getTime());
                    if (deleteWtMenuBindings(session, menus, idOfOrg)) {
                        logger.info(String.format("Deleted old menu bindings for idOfOrg = %s", idOfOrg));
                    }
                    logger.info("end deleteWtMenuBindings " + new Date().getTime());
                    logger.info("start deleteWtComplexBindings " + new Date().getTime());
                    if (deleteWtComplexBindings(session, complexes, idOfOrg)) {
                        logger.info(String.format("Deleted old complex bindings for idOfOrg = %s", idOfOrg));
                    }
                    logger.info("end deleteWtComplexBindings " + new Date().getTime());
                }
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            HibernateUtils.rollback(transaction, logger);
            logger.error("Error in checking org and contragent bindings requests: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        logger.info("End checking org and contragent bindings");
    }

    private boolean deleteWtComplexBindings(Session session, List<Long> complexIds, Long idOfOrg) {
        if (complexIds.size() == 0) {
            return false;
        }
        //
        Query insQuery = session.createSQLQuery("insert into cf_wt_org_relation_aud"
                + "(idofevent, idofcomplex, idoforg, deletestate, createdate, idofuser, versionofcomplex) "
                + "select nextval('cf_wt_org_relation_aud_seq'), idofcomplex, :idOfOrg, 1, cast(now() as timestamp) as createdate, "
                + "(select idofuser from cf_users where username = 'admin') as idofuser, "
                + "(select coalesce(max(versionofcomplex), 0) + 1 from cf_wt_org_relation_aud) as versionofcomplex "
                + " from cf_wt_complexes_org where idoforg = :idOfOrg and idofcomplex in (:complexIds)");
        insQuery.setParameter("idOfOrg", idOfOrg);
        insQuery.setParameterList("complexIds", complexIds);
        insQuery.executeUpdate();
        //
        Query sqlQuery = session.createSQLQuery("delete from cf_wt_complexes_org "
                + "where idoforg = :idOfOrg and idofcomplex in (:complexIds)");
        sqlQuery.setParameter("idOfOrg", idOfOrg);
        sqlQuery.setParameterList("complexIds", complexIds);
        return sqlQuery.executeUpdate() > 0;
    }

    private List<Long> findWtComplexIdsExcludingContragent(Session session, Long idOfContragent) {
        List<Long> res = new ArrayList<>();
        Query query = session.createQuery("select c.idOfComplex from WtComplex c "
                + "where c.contragent.idOfContragent <> :idOfContragent");
        query.setParameter("idOfContragent", idOfContragent);
        List list = query.list();
        for (Object o : list) {
            res.add(Long.valueOf(o.toString()));
        }
        return res;
    }

    private boolean deleteWtMenuBindings(Session session, List<Long> menuIds, Long idOfOrg) {
        if (menuIds.size() == 0) {
            return false;
        }
        //
        Query insQuery = session.createSQLQuery("insert into cf_wt_org_relation_aud"
                + "(idofevent, idofmenu, idoforg, deletestate, createdate, idofuser, versionofmenu) "
                + "select nextval('cf_wt_org_relation_aud_seq'), idofmenu, :idOfOrg, 1, cast(now() as timestamp) as createdate, "
                + "(select idofuser from cf_users where username = 'admin') as idofuser, "
                + "(select coalesce(max(versionofmenu), 0) + 1 from cf_wt_org_relation_aud) as versionofmenu "
                + " from cf_wt_menu_org where idoforg = :idOfOrg and idofmenu in (:menuIds)");
        insQuery.setParameter("idOfOrg", idOfOrg);
        insQuery.setParameterList("menuIds", menuIds);
        insQuery.executeUpdate();
        //
        Query sqlQuery = session.createSQLQuery("delete from cf_wt_menu_org "
                + "where idoforg = :idOfOrg and idofmenu in (:menuIds)");
        sqlQuery.setParameter("idOfOrg", idOfOrg);
        sqlQuery.setParameterList("menuIds", menuIds);
        return sqlQuery.executeUpdate() > 0;
    }

    private List<Long> findWtMenuIdsExcludingContragent(Session session, Long idOfContragent) {
        List<Long> res = new ArrayList<>();
        Query query = session.createQuery("select m.idOfMenu from WtMenu m "
                + "where m.contragent.idOfContragent <> :idOfContragent");
        query.setParameter("idOfContragent", idOfContragent);
        List list = query.list();
        for (Object o : list) {
            res.add(Long.valueOf(o.toString()));
        }
        return res;
    }

    private boolean deleteWtOrgGroupBindings(Session session, List<Long> orgGroupIds, Long idOfOrg) {
        if (orgGroupIds.size() == 0) {
            return false;
        }
        //
        Query insQuery = session.createSQLQuery("insert into cf_wt_org_relation_aud"
                + "(idofevent, idoforggroup, idoforg, deletestate, createdate, idofuser, versionoforggroup) "
                + "select nextval('cf_wt_org_relation_aud_seq'), idoforggroup, :idOfOrg, 1, cast(now() as timestamp) as createdate, "
                + "(select idofuser from cf_users where username = 'admin') as idofuser, "
                + "(select coalesce(max(versionoforggroup), 0) + 1 from cf_wt_org_relation_aud) as versionoforggroup "
                + " from cf_wt_org_group_relations where idoforg = :idOfOrg and idoforggroup in (:orgGroupIds)");
        insQuery.setParameter("idOfOrg", idOfOrg);
        insQuery.setParameterList("orgGroupIds", orgGroupIds);
        insQuery.executeUpdate();
        //
        Query sqlQuery = session.createSQLQuery("delete from cf_wt_org_group_relations "
                + "where idoforg = :idOfOrg and idoforggroup in (:orgGroupIds)");
        sqlQuery.setParameter("idOfOrg", idOfOrg);
        sqlQuery.setParameterList("orgGroupIds", orgGroupIds);
        return sqlQuery.executeUpdate() > 0;
    }

    private List<Long> findWtOrgGroupIdsExcludingContragent(Session session, Long idOfContragent) {
        List<Long> res = new ArrayList<>();
        Query query = session.createQuery("select og.idOfOrgGroup from WtOrgGroup og "
                + "where og.contragent.idOfContragent <> :idOfContragent");
        query.setParameter("idOfContragent", idOfContragent);
        List list = query.list();
        for (Object o : list) {
            res.add(Long.valueOf(o.toString()));
        }
        return res;
    }


    public String checkIsExistFile() throws Exception {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + TEMPLATE_FILENAME;
        if(!(new File(templateFilename)).exists()){
            throw new Exception(String.format("Не найден файл шаблона '%s'", TEMPLATE_FILENAME));
        }
        return templateFilename;
    }

    private List<PreorderItem> loadPreorders2(Date dateFrom, Date dateTo, PreorderRequestsReportServiceParam params) {
        Session session = null;
        Transaction transaction = null;
        List<PreorderItem> preorderItemList = new ArrayList<PreorderItem>();
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            String sqlQuery = "SELECT pc.idoforgoncreate, "                                                                //0
                            + "pc.createddate, "                                                                           //1
                            + "pc.idofpreordercomplex, "                                                                   //2
                            + "pmd.idofpreordermenudetail, "                                                               //3
                            + "   CASE WHEN (pc.amount = 0) THEN pmd.idofgood ELSE ci.idofgood END AS idofgood, "           //4
                            + "   CASE WHEN (pc.amount = 0) THEN pmd.amount ELSE pc.amount END AS amount, "                 //5
                            + "   pc.preorderdate AS prDate, "                                                             //6
                            + "pc.complexprice, "                                                                          //7
                            + "pc.amount AS complexamount, "                                                               //8
                            + "pmd.menudetailprice, "                                                                      //9
                            + "pmd.amount AS menudetailamount, "                                                           //10
                            + "c.balance, "                                                                                //11
                            + "c.idofclient, "                                                                             //12
                            + "c.idofclientgroup, "                                                                        //13
                            + "pc.usedsum, "                                                                                //14
                            + "case when (pc.amount = 0) then pmd.idofgoodsrequestposition else pc.idofgoodsrequestposition end, " //15
                            + "pc.armcomplexid, "                                                                           //16
                            + "pmd.idofdish "                                                                               //17
                            + "FROM cf_preorder_complex pc INNER JOIN cf_clients c ON c.idofclient = pc.idofclient "
                            + "LEFT JOIN cf_complexinfo ci ON pc.idoforgoncreate = ci.idoforg AND ci.menudate = pc.preorderdate AND ci.idofcomplex = pc.armcomplexid "
                            + "LEFT JOIN cf_preorder_menudetail pmd ON pc.idofpreordercomplex = pmd.idofpreordercomplex AND pc.amount = 0 and pmd.deletedstate = 0 "
                            + "WHERE pc.preorderdate >= :date " + (dateTo != null ? " and pc.preorderdate <= :dateTo " : "")
                            + "   AND (pc.amount <> 0 OR pmd.amount <> 0) and pc.deletedstate = 0 "
                            + params.getNativeSQLCondition()
                    + "order by prDate ";

            Query query = session.createSQLQuery(sqlQuery);
            query.setParameter("date", CalendarUtils.startOfDayInUTC(dateFrom).getTime());
            if (dateTo != null) query.setParameter("dateTo", dateTo.getTime());
            List data = query.list();
            for (Object entry : data) {
                Object o[] = (Object[]) entry;
                Long idOfOrg = (null != o[0]) ? ((BigInteger) o[0]).longValue() : null;
                Date createdDate = (null != o[1]) ? new Date(((BigInteger) o[1]).longValue()) : null;
                Long idOfPreorderComplex = (null != o[2]) ? ((BigInteger) o[2]).longValue() : null;
                Long idOfPreorderMenuDetail = (null != o[3]) ? ((BigInteger) o[3]).longValue() : null;
                Long idOfGood = (null != o[4]) ? ((BigInteger) o[4]).longValue() : null;
                Integer amount = (Integer) o[5];
                Long idOfGoodsRequest = (null != o[15]) ? ((BigInteger) o[15]).longValue() : null;
                Date preorderDate = (null != o[6]) ? new Date(((BigInteger) o[6]).longValue()) : null;

                Long complexPrice = (null != o[7]) ? ((BigInteger) o[7]).longValue() : 0L;
                Integer complexAmount = (null != o[8]) ? (Integer) o[8] : 0;
                Long menuDetailPrice = (null != o[9]) ? ((BigInteger) o[9]).longValue() : 0L;
                Integer menuDetailAmount = (null != o[10]) ? (Integer) o[10] : 0;
                Long clientBalance = (null != o[11]) ? ((BigInteger) o[11]).longValue() : 0L;
                Long idOfClient = (null != o[12]) ? ((BigInteger) o[12]).longValue() : null;
                Boolean isDeleted = false; //(Boolean) o[14];
                Boolean isComplex = !complexAmount.equals(0);
                Long idOfClientGroup = (null != o[13]) ? ((BigInteger) o[13]).longValue() : null;
                Long usedSum = (null != o[14]) ? ((BigInteger) o[14]).longValue() : 0L;
                Integer complexId = (null != o[16]) ? (Integer) o [16] : null;
                Long idOfDish = (null != o[17]) ? ((BigInteger) o [17]).longValue() : null;

                preorderItemList
                        .add(new PreorderItem(idOfPreorderComplex, idOfPreorderMenuDetail, idOfOrg, idOfGood, amount,
                                createdDate, idOfGoodsRequest, preorderDate,
                                complexPrice * complexAmount + menuDetailPrice * menuDetailAmount, clientBalance,
                                idOfClient, isDeleted, isComplex, idOfClientGroup, usedSum, complexId, idOfDish));
            }
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return preorderItemList;
    }


    private String createRequestFromPreorder2(Session session, PreorderItem preorderItem, Date fireTime, Long num, Staff staff) {
        //  Формируем номер по маске {idOfOrg}-{yyMMdd}-ЗВК-{countToDay}
        Date now = new Date(System.currentTimeMillis());
        String number = "" + preorderItem.getIdOfOrg() + "-" + new SimpleDateFormat("yyMMdd").format(now) + "-ЗВК-" + num;

        Good good = null;
        Org org = DAOUtils.getOrgById(session, preorderItem.getIdOfOrg());
        if (!org.getUseWebArm()) {
            good = DAOReadonlyService.getInstance().getGood(preorderItem.getIdOfGood());
            if (null == good) {
                logger.info(String.format("Not found good for preorder id = %s", preorderItem.getIdOfPreorderComplex()));
                return null;
            }
        }
        if (null == staff) {
            logger.info(String.format("Not found staff admin for org id = %s, preorder id = %s",
                    org.getIdOfOrg(), preorderItem.getIdOfPreorderComplex()));
            return null;
        }

        GoodRequest goodRequest = new GoodRequest();
        goodRequest.setOrgOwner(preorderItem.getIdOfOrg());
        goodRequest.setDateOfGoodsRequest(preorderItem.getCreatedDate());
        goodRequest.setDoneDate(preorderItem.getPreorderDate());
        goodRequest.setNumber(number);
        goodRequest.setState(DocumentState.FOLLOW);
        goodRequest.setDeletedState(false);
        goodRequest.setCreatedDate(fireTime);
        goodRequest.setComment(PREORDER_COMMENT);
        goodRequest.setRequestType(PREORDER_REQUEST_TYPE);
        goodRequest.setStaff(staff);
        goodRequest.setGuidOfStaff(staff.getGuid());
        goodRequest = save(session, goodRequest, GoodRequest.class.getSimpleName());

        GoodRequestPosition pos = new GoodRequestPosition();
        pos.setGoodRequest(goodRequest);
        pos.setGood(good);
        pos.setDeletedState(false);
        pos.setOrgOwner(preorderItem.getIdOfOrg());
        if (!org.getUseWebArm()) {
            pos.setUnitsScale(good.getUnitsScale());
            pos.setNetWeight(good.getNetWeight());
            pos.setTotalCount(preorderItem.getAmount() * 1000L);
        } else {
            pos.setUnitsScale(UnitScale.UNITS);
            pos.setNetWeight(0L);
            pos.setTotalCount(preorderItem.getAmount().longValue());
        }
        pos.setCreatedDate(fireTime);
        pos.setDailySampleCount(0L);
        pos.setTempClientsCount(0L);
        pos.setNotified(false);
        pos.setComplexId(preorderItem.getComplexId());
        pos.setIdOfDish(preorderItem.getIdOfDish());
        pos.setFeedingType(RequestsSupplierDetailTypeEnum.REQUEST_TYPE_PREORDER.ordinal());
        pos = save(session, pos, GoodRequestPosition.class.getSimpleName());

        if (preorderItem.getComplex()) {
            PreorderComplex complex = (PreorderComplex) session.get(PreorderComplex.class, preorderItem.getIdOfPreorderComplex());
            complex.setIdOfGoodsRequestPosition(pos.getGlobalId());
            session.update(complex);
        } else {
            PreorderMenuDetail detail = (PreorderMenuDetail) session.get(PreorderMenuDetail.class,
                    preorderItem.getIdOfPreorderMenuDetail());
            detail.setIdOfGoodsRequestPosition(pos.getGlobalId());
            session.update(detail);
        }

        logger.info("Created good request number=" + number + " " + preorderItem.toString());

        return pos.getGuid();
    }

    public <T extends ConsumerRequestDistributedObject> T save(Session session, T object, String className) {
        Long version = DAOUtils.getDistributedObjectVersion(session, className);
        object.setGlobalVersion(version);

        if(object.getGlobalId()==null){
            object.setGlobalVersionOnCreate(version);
            session.save(object);
        } else {
            object = (T) session.merge(object);
        }
        return object;
    }

    public void scheduleSync() {
        String syncSchedule = RuntimeContext.getInstance().getConfigProperties().getProperty(CRON_EXPRESSION_PROPERTY, "");
        if (syncSchedule.equals("")) {
            return;
        }
        try {
            logger.info("Scheduling PreorderRequestsReport service job: " + syncSchedule);
            JobDetail job = new JobDetail("PreorderRequestsReport", Scheduler.DEFAULT_GROUP, PreorderRequestsReportJob.class);

            SchedulerFactory sfb = new StdSchedulerFactory();
            Scheduler scheduler = sfb.getScheduler();
            if (!syncSchedule.equals("")) {
                CronTrigger trigger = new CronTrigger("PreorderRequestsReport", Scheduler.DEFAULT_GROUP);
                trigger.setCronExpression(syncSchedule);
                if (scheduler.getTrigger("PreorderRequestsReport", Scheduler.DEFAULT_GROUP) != null) {
                    scheduler.deleteJob("PreorderRequestsReport", Scheduler.DEFAULT_GROUP);
                }
                scheduler.scheduleJob(job, trigger);
            }
            scheduler.start();
        } catch(Exception e) {
            logger.error("Failed to schedule PreorderRequestsReport service job:", e);
        }

        scheduleSyncRecovery();
    }

    public void scheduleSyncRecovery() {
        Date fireTime = getFireTime();
        try {
            logger.info("Scheduling PreorderRequestsReportRecovery service job: " + fireTime.toString());
            JobDetail job = new JobDetail("PreorderRequestsReportRecovery", Scheduler.DEFAULT_GROUP, PreorderRequestsReportRecoveryJob.class);

            SchedulerFactory sfb = new StdSchedulerFactory();
            Scheduler scheduler = sfb.getScheduler();

            SimpleTrigger trigger = new SimpleTrigger("PreorderRequestsReportRecovery", fireTime);
            if (scheduler.getTrigger("PreorderRequestsReportRecovery", Scheduler.DEFAULT_GROUP) != null) {
                scheduler.deleteJob("PreorderRequestsReportRecovery", Scheduler.DEFAULT_GROUP);
            }
            scheduler.scheduleJob(job, trigger);
            scheduler.start();
        } catch(Exception e) {
            logger.error("Failed to schedule PreorderRequestsReportRecovery service job:", e);
        }
    }

    public static class PreorderItem {
        private Long idOfPreorderComplex;
        private Long idOfPreorderMenuDetail;
        private Long idOfOrg;
        private Long idOfGood;
        private Integer amount;
        private Date createdDate;
        private Long idOfGoodsRequestPosition;
        private Date preorderDate;
        private Long complexPrice;
        private Long clientBalance;
        private Long idOfClient;
        private Boolean isDeleted;
        private Boolean isComplex;
        private Long idOfClientGroup;
        private Long usedSum;
        private Integer complexId;
        private Long idOfDish;

        public PreorderItem(Long idOfPreorderComplex, Long idOfPreorderMenuDetail, Long idOfOrg, Long idOfGood, Integer amount,
                Date createdDate, Long idOfGoodsRequestPosition, Date preorderDate, Long complexPrice, Long clientBalance,
                Long idOfClient, Boolean isDeleted, Boolean isComplex, Long idOfClientGroup, Long usedSum, Integer complexId,
                Long idOfDish) {
            this.idOfPreorderComplex = idOfPreorderComplex;
            this.idOfPreorderMenuDetail = idOfPreorderMenuDetail;
            this.idOfOrg = idOfOrg;
            this.idOfGood = idOfGood;
            this.amount = amount;
            this.createdDate = createdDate;
            this.idOfGoodsRequestPosition = idOfGoodsRequestPosition;
            this.preorderDate = preorderDate;
            this.complexPrice = complexPrice;
            this.clientBalance = clientBalance;
            this.idOfClient = idOfClient;
            this.isDeleted = isDeleted;
            this.isComplex = isComplex;
            this.idOfClientGroup = idOfClientGroup;
            this.usedSum = usedSum;
            this.complexId = complexId;
            this.idOfDish = idOfDish;
        }

        public PreorderItem() {

        }

        public Long getIdOfPreorderComplex() {
            return idOfPreorderComplex;
        }

        public void setIdOfPreorderComplex(Long idOfPreorderComplex) {
            this.idOfPreorderComplex = idOfPreorderComplex;
        }

        public Long getIdOfPreorderMenuDetail() {
            return idOfPreorderMenuDetail;
        }

        public void setIdOfPreorderMenuDetail(Long idOfPreorderMenuDetail) {
            this.idOfPreorderMenuDetail = idOfPreorderMenuDetail;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(Long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public Long getIdOfGood() {
            return idOfGood;
        }

        public void setIdOfGood(Long idOfGood) {
            this.idOfGood = idOfGood;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }

        public Date getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(Date createdDate) {
            this.createdDate = createdDate;
        }

        public Long getIdOfGoodsRequestPosition() {
            return idOfGoodsRequestPosition;
        }

        public void setIdOfGoodsRequestPosition(Long idOfGoodsRequestPosition) {
            this.idOfGoodsRequestPosition = idOfGoodsRequestPosition;
        }

        public Date getPreorderDate() {
            return preorderDate;
        }

        public void setPreorderDate(Date preorderDate) {
            this.preorderDate = preorderDate;
        }

        public Long getComplexPrice() {
            return complexPrice;
        }

        public void setComplexPrice(Long complexPrice) {
            this.complexPrice = complexPrice;
        }

        public Long getClientBalance() {
            return clientBalance;
        }

        public void setClientBalance(Long clientBalance) {
            this.clientBalance = clientBalance;
        }

        public Long getIdOfClient() {
            return idOfClient;
        }

        public void setIdOfClient(Long idOfClient) {
            this.idOfClient = idOfClient;
        }

        public Boolean getDeleted() {
            return isDeleted;
        }

        public void setDeleted(Boolean deleted) {
            isDeleted = deleted;
        }

        public Boolean getComplex() {
            return isComplex;
        }

        public void setComplex(Boolean complex) {
            isComplex = complex;
        }

        public Long getIdOfClientGroup() {
            return idOfClientGroup;
        }

        public void setIdOfClientGroup(Long idOfClientGroup) {
            this.idOfClientGroup = idOfClientGroup;
        }

        public Long getUsedSum() {
            return usedSum;
        }

        public void setUsedSum(Long usedSum) {
            this.usedSum = usedSum;
        }

        public Integer getComplexId() {
            return complexId;
        }

        public void setComplexId(Integer complexId) {
            this.complexId = complexId;
        }

        public Long getIdOfDish() {
            return idOfDish;
        }

        public void setIdOfDish(Long idOfDish) {
            this.idOfDish = idOfDish;
        }

        @Override
        public String toString() {
            return "{PreorderItem: idOfPreorderComplex = " + idOfPreorderComplex + ", idOfPreorderMenuDetail = " + idOfPreorderMenuDetail
                    + ", idOfOrg = " + idOfOrg + ", preorderDate = " + CalendarUtils.dateToString(preorderDate) + ", idOfClient = " + idOfClient + "}";
        }
    }

    @PostConstruct
    private void init() {
        setStatusFileNameProperty(STATUS_FILENAME_PROPERTY);
        setStatusFileNameDefaultValue(STATUS_FILENAME_DEFAULT_VALUE);
    }

    public static class PreorderRequestsReportJob implements Job {
        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            try {
                RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class).run();
            } catch (JobExecutionException e) {
                throw e;
            } catch (Exception e) {
                logger.error("Failed to run PreorderRequestsReport service job:", e);
            }
        }
    }

    public static class PreorderRequestsReportRecoveryJob implements Job {
        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            try {
                RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class).recoveryRun();
            } catch (JobExecutionException e) {
                throw e;
            } catch (Exception e) {
                logger.error("Failed to run PreorderRequestsReportRecovery service job:", e);
            }
        }
    }

    public static class DatesForPreorder {
        public Date startDate;
        public Date endDate;
    }

    public static class PreorderItemForDelete {
        public Long idOfClient;
        public Date preorderDate;

        public PreorderItemForDelete(Long idOfClient, Date preorderDate) {
            this.idOfClient = idOfClient;
            this.preorderDate = preorderDate;
        }
    }

    public static class ClientBalances {
        public Long idOfClient;
        public Date date;
        public Long balance;

        public ClientBalances(Long idOfClient, Date date, Long balance) {
            this.idOfClient = idOfClient;
            this.date = date;
            this.balance = balance;
        }
    }
}

