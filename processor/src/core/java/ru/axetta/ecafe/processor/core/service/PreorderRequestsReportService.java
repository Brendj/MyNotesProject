/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ConsumerRequestDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOVersion;
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

        if (isFinishedToday())
            return;

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
            logger.info("Start generating preorder requests");
            List<Date> weekends = GoodRequestsChangeAsyncNotificationService.getInstance().getProductionCalendarDates(date);
            Map<Long, GoodRequestsChangeAsyncNotificationService.OrgItem> orgItemsLocal = GoodRequestsChangeAsyncNotificationService.getInstance().findOrgItems2(true, params); //орги с включенным флагом предзаказа

            Integer maxDays = getMaxDateToCreateRequests(currentDate, weekends, MAX_FORBIDDEN_DAYS);
            Date dateTo = CalendarUtils.addDays(currentDate, maxDays);
            List<PreorderItem> preorderItemList = loadPreorders2(date, dateTo, params); //предзаказы от завтрашнего дня до дня, на который максимум можем сгенерить заявки
            List<OrgGoodRequest> doneOrgGoodRequests = GoodRequestsChangeAsyncNotificationService.getInstance().getDoneOrgGoodRequests(dateTo, params);
            Map<Long, List<SpecialDate>> mapSpecialDates = getAllSpecialDates(orgItemsLocal, date, dateTo);
            List<ProductionCalendar> productionCalendar = DAOReadonlyService.getInstance().getProductionCalendar(date, dateTo);

            Map<Long, Map<Date, Long>> clientBalances = getClientBalancesOnDates(preorderItemList, mapSpecialDates, productionCalendar); //балансы клиентов на даты с учетом предзаказов

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
                    logger.info(String.format("Generating preorder requests. orgID=%s (№%s from %s)", idOfOrg, counterOrgs, sizeOrgs));

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
                            for (PreorderItem item : preordersByOrg) {
                                try {
                                    Org org = DAOUtils.getOrgById(session, idOfOrg);
                                    if (null == item.getIdOfGood() && !org.getUseWebArm()) {
                                        //logger.error(String.format(
                                        //        "PreorderRequestsReportService: preorder without good item was found (preorderComplex = orgID = %s, createdDate = %s)",
                                        //        item.getIdOfOrg(), item.getCreatedDate().toString()));
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
                                        logger.info("Delete preorder for not enough money " + item.toString());
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
        //проверки на актуальность предзаказов
        RuntimeContext.getAppContext().getBean(DAOService.class).getPreorderDAOOperationsImpl().relevancePreorders(params);
        //генерация предзаказов по регулярному правилу
        RuntimeContext.getAppContext().getBean(DAOService.class).getPreorderDAOOperationsImpl().generatePreordersBySchedule(params);
        //генерация заявок
        runGeneratePreorderRequests(params);
        //сервис проверок предзаказов
        if (instance.equals(firstNode)) {
            RuntimeContext.getAppContext().getBean(DAOService.class).getPreorderDAOOperationsImpl().dailyCheckPreorders();
        }
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

            String sqlQuery = "SELECT ci.idoforg, "                                                                        //0
                            + "pc.createddate, "                                                                           //1
                            + "pc.idofpreordercomplex, "                                                                   //2
                            + "pmd.idofpreordermenudetail, "                                                               //3
                            + "   CASE WHEN (pc.amount = 0) THEN (case when md.idofgood is null then pmd.idofgood else md.idofgood end) ELSE ci.idofgood END AS idofgood, "           //4
                            + "   CASE WHEN (pc.amount = 0) THEN pmd.amount ELSE pc.amount END AS amount,"                 //5
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
                            + "INNER JOIN cf_complexinfo ci ON pc.idoforgoncreate = ci.idoforg AND ci.menudate = pc.preorderdate AND ci.idofcomplex = pc.armcomplexid "
                            + "LEFT JOIN cf_preorder_menudetail pmd ON pc.idofpreordercomplex = pmd.idofpreordercomplex AND pc.amount = 0 and pmd.deletedstate = 0 "
                            + "LEFT JOIN cf_menu m ON pc.idoforgoncreate = m.idoforg AND pmd.preorderdate = m.menudate "
                            + "LEFT JOIN cf_menudetails md ON m.idofmenu = md.idofmenu AND pmd.armidofmenu = md.localidofmenu "
                            + "WHERE pc.preorderdate >= :date " + (dateTo != null ? " and pc.preorderdate <= :dateTo " : "")
                            + "   AND (pc.amount <> 0 OR pmd.amount <> 0) and pc.deletedstate = 0 "
                            + params.getNativeSQLCondition()
                    + "UNION "
                    + "SELECT pc.idoforgoncreate, "                                                                         //0
                    + "pc.createddate, "                                                                                    //1
                    + "pc.idofpreordercomplex, "                                                                            //2
                    + "pmd.idofpreordermenudetail, "                                                                        //3
                    + "pmd.idofgood, "                                                                                      //4
                    + "CASE WHEN (pc.amount = 0) THEN pmd.amount ELSE pc.amount END AS amount, "                            //5
                    + "pc.preorderdate AS prDate, "                                                                         //6
                    + "pc.complexprice, "                                                                                   //7
                    + "pc.amount AS complexamount, "                                                                        //8
                    + "pmd.menudetailprice, "                                                                               //9
                    + "pmd.amount AS menudetailamount, "                                                                    //10
                    + "c.balance, "                                                                                         //11
                    + "c.idofclient, "                                                                                      //12
                    + "c.idofclientgroup, "                                                                                 //13
                    + "pc.usedsum, "                                                                                        //14
                    + "case when (pc.amount = 0) then pmd.idofgoodsrequestposition else pc.idofgoodsrequestposition end, "   //15
                    + "pc.armcomplexid, "                                                                                   //16
                    + "pmd.idofdish "                                                                                       //17
                    + "FROM cf_preorder_complex pc INNER JOIN cf_clients c ON c.idofclient = pc.idofclient "
                    + "LEFT JOIN cf_wt_complexes wc ON wc.idofcomplex = pc.armcomplexid AND wc.deletestate = 0 "
                    + "AND pc.preorderdate BETWEEN (EXTRACT(EPOCH FROM wc.begindate) * 1000) AND (EXTRACT(EPOCH FROM wc.enddate) * 1000) "
                    + "LEFT JOIN cf_preorder_menudetail pmd ON pc.idofpreordercomplex = pmd.idofpreordercomplex AND pc.amount = 0 and pmd.deletedstate = 0 "
                    + "LEFT JOIN cf_wt_dishes wd ON wd.idofdish = pmd.idofdish AND wd.deletestate = 0 "
                    + "AND (pc.preorderdate BETWEEN (EXTRACT(EPOCH FROM wd.dateOfBeginMenuIncluding) * 1000) AND (EXTRACT(EPOCH FROM wd.dateOfEndMenuIncluding) * 1000) "
                    + "OR (wd.dateOfBeginMenuIncluding IS NULL AND (EXTRACT(EPOCH FROM wd.dateOfEndMenuIncluding) * 1000) >= pc.preorderdate) "
                    + "OR ((EXTRACT(EPOCH FROM wd.dateOfBeginMenuIncluding) * 1000) <= pc.preorderdate AND wd.dateOfEndMenuIncluding IS NULL) "
                    + "OR (wd.dateOfBeginMenuIncluding IS NULL AND wd.dateOfEndMenuIncluding IS NULL)) "
                    + "WHERE pc.preorderdate >= :date " + (dateTo != null ? " and pc.preorderdate <= :dateTo " : "")
                    + "AND (pc.amount <> 0 OR pmd.amount <> 0) and pc.deletedstate = 0 "
                    + "AND (wc.idofcomplex is not null or wd.idofdish is not null)"
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
            good = DAOService.getInstance().getGood(preorderItem.getIdOfGood());
            if (null == good)
                return null;
        }
        if (null == staff)
            return null;

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
        Query query = session
                .createQuery("from DOVersion where UPPER(distributedObjectClassName)=:distributedObjectClassName");
        query.setParameter("distributedObjectClassName", className.toUpperCase());
        List<DOVersion> doVersionList = query.list();
        DOVersion doVersion = null;
        Long version = null;
        if (doVersionList.size() == 0) {
            doVersion = new DOVersion();
            doVersion.setCurrentVersion(0L);
            version = 0L;
            doVersion.setDistributedObjectClassName(className);
            session.save(doVersion);
        } else {
            doVersion = (DOVersion) session.load(DOVersion.class, doVersionList.get(0).getIdOfDOObject());
            version = doVersion.getCurrentVersion() + 1;
            doVersion.setCurrentVersion(version);
            session.merge(doVersion);
        }
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

