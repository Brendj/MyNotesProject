/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.IPreorderDAOOperations;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ConsumerRequestDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOVersion;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DocumentState;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequest;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPositionTemp;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestTemp;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SettingsIds;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.Staff;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SubscriberFeedingSettingSettingValue;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.GoodRequestsNewReport;
import ru.axetta.ecafe.processor.core.report.PreorderRequestsReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.*;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

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
    private IPreorderDAOOperations preorderDAOOperations;
    public final Integer PREORDER_REQUEST_TYPE = 3;
    private static final Integer MAX_FORBIDDEN_DAYS = 3;

    private Calendar localCalendar;
    private Date startDate;
    private Date endDate;

    private int maxNumDays;
    private Map<Long, GoodRequestsChangeAsyncNotificationService.OrgItem> orgItems = new HashMap<Long, GoodRequestsChangeAsyncNotificationService.OrgItem>();
    private boolean isHideMissedCol;

    public void run() throws Exception {
        if (!isOn())
            return;

        updateStatusFile(new Date(), Status.RUNNING);
        runTask();
        updateStatusFile(new Date(), Status.FINISHED);
    }

    public void run2() throws Exception {
        if (!RuntimeContext.getInstance().actionIsOnByNode("ecafe.processor.report.PreorderRequestsReport2.node"))
            return;

        runGeneratePreorderRequests(new Date());
    }

    @Override
    public void recoveryRun() throws Exception {
        if (!isOn())
            return;

        if (isFinishedToday())
            return;

        updateStatusFile(new Date(), Status.RUNNING);
        runTaskNextTimePerDay();
        updateStatusFile(new Date(), Status.FINISHED);
    }

    public static boolean isOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getConfigProperties().getProperty(PreorderRequestsReportService.NODE_PROPERTY, "1");
        if (StringUtils.isBlank(instance) || StringUtils.isBlank(reqInstance) || !instance.trim().equals(
                reqInstance.trim())) {
            return false;
        }
        return true;
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

    public void runGeneratePreorderRequests(Date date) {
        try {
            Date fireTime = new Date();
            Date currentDate = CalendarUtils.startOfDayInUTC(date); // CalendarUtils.addHours(CalendarUtils.startOfDay(date), 3);
            if (DAOService.getInstance().getProductionCalendarByDate(currentDate) != null) return; //в выходной день заявки не формируем
            logger.info("Start generating preorder requests");
            List<Date> weekends = GoodRequestsChangeAsyncNotificationService.getInstance().getProductionCalendarDates(date);
            Map<Long, GoodRequestsChangeAsyncNotificationService.OrgItem> orgItemsLocal = GoodRequestsChangeAsyncNotificationService.getInstance().findOrgItems2(true); //орги с включенным флагом предзаказа

            Integer maxDays = getMaxDateToCreateRequests(currentDate, weekends, MAX_FORBIDDEN_DAYS);
            Date dateTo = CalendarUtils.addDays(currentDate, maxDays);
            List<PreorderItem> preorderItemList = loadPreorders2(dateTo); //предзаказы от завтрашнего дня до дня, на который максимум можем сгенерить заявки
            List<OrgGoodRequest> doneOrgGoodRequests = GoodRequestsChangeAsyncNotificationService.getInstance().getDoneOrgGoodRequests(dateTo);

            Map<Long, Map<Date, Long>> clientBalances = getClientBalancesOnDates(preorderItemList); //балансы клиентов на даты с учетом предзаказов

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

                    GoodRequestsChangeAsyncNotificationService.OrgItem orgItem = entry.getValue();

                    Long number = DAOUtils.getNextGoodRequestNumberForOrgPerDay(session, idOfOrg, new Date());
                    Staff staff = DAOUtils.getAdminStaffFromOrg(session, idOfOrg);

                    List<Date> orgDates = getOrgDates(currentDate, idOfOrg, weekends); //вычислены даты, на которые нужно генерировать заявки для текущей ОО
                    List<String> guids = new ArrayList<String>();
                    for (Date dateWork : orgDates) {
                        if (getOrgGoodRequestByDate(idOfOrg, dateWork, doneOrgGoodRequests) != null) {
                            logger.info(String.format("Requests for orgID=%s on date=%s already exist", idOfOrg, CalendarUtils.dateToString(dateWork)));
                            continue;
                        }
                        try {
                            transaction = session.beginTransaction();
                            List<PreorderItem> preordersByOrg = getPreorderItemsByOrg(idOfOrg, preorderItemList, dateWork); //предзаказы по ОО на дату
                            for (PreorderItem item : preordersByOrg) {
                                try {
                                    if (null == item.getIdOfGood()) {
                                        logger.error(String.format(
                                                "PreorderRequestsReportService: preorder without good item was found (preorderComplex = orgID = %s, createdDate = %s)",
                                                item.getIdOfOrg(), item.getCreatedDate().toString()));
                                        continue;
                                    }
                                    long balanceOnDate = getBalanceOnDate(item.getIdOfClient(), dateWork, clientBalances);
                                    if (balanceOnDate < 0L) {
                                        //deletePreorderForNotEnoughMoney(session, item);
                                        logger.info("Delete preorder " + item.toString());
                                    } else {
                                        String guid = createRequestFromPreorder2(session, item, fireTime, number, staff);
                                        if (null != guid) {
                                            number++;
                                            guids.add(guid);
                                        }
                                    }
                                } catch (Exception e) {
                                    logger.error("Error in create request for item = " + item.toString());
                                }
                            }


                            //notifyOrg(bla-bla);
                            OrgGoodRequest orgGoodRequest = new OrgGoodRequest(idOfOrg, dateWork);
                            session.save(orgGoodRequest);
                        } catch (Exception e) {
                            transaction.rollback();
                            logger.error(String.format("Error in generate request for orgID = %s: ", idOfOrg), e);
                        } finally {
                            if (transaction.isActive()) transaction.commit();
                            transaction = null;
                        }
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
            if (item.getIdOfOrg().equals(idOfOrg) && item.getPreorderDate().equals(date)) result.add(item);
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

    private Map<Long, Map<Date, Long>> getClientBalancesOnDates(List<PreorderItem> preorderItemList) {
        Map<Long, Map<Date, Long>> result = new HashMap<Long, Map<Date, Long>>();
        for (PreorderItem item : preorderItemList) {
            Map<Date, Long> map = result.get(item.getIdOfClient());
            if (map == null) {
                map = new HashMap<Date, Long>();
                map.put(item.getPreorderDate(), item.getClientBalance());
            }
            map.put(item.getPreorderDate(), map.get(item.getPreorderDate()) - item.getAmount() * item.getComplexPrice() + item.getUsedSum());
            result.put(item.getIdOfClient(), map);
        }
        return result;
    }

    private long getBalanceOnDate(long idOfClient, Date date, Map<Long, Map<Date, Long>> clientBalances) {
        try {
            return clientBalances.get(idOfClient).get(date);
        } catch (Exception e) {
            return 0L;
        }
    }

    public void runTask() throws Exception {
        //генерация предзаказов по регулярному правилу
        RuntimeContext.getAppContext().getBean(DAOService.class).getPreorderDAOOperationsImpl().generatePreordersBySchedule();
        runTaskNextTimePerDay();
    }

    public void runTaskNextTimePerDay() throws Exception {
        logger.info("Start preorder request report gen");
        updateDate();

        orgItems = GoodRequestsChangeAsyncNotificationService.getInstance().findOrgItems();

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        maxNumDays = runtimeContext.getOptionValueInt(Option.OPTION_MAX_NUM_DAYS_NOTIFICATION_GOOD_REQUEST_CHANGE);
        isHideMissedCol = runtimeContext
                .getOptionValueBool(Option.OPTION_HIDE_MISSED_COL_NOTIFICATION_GOOD_REQUEST_CHANGE);

        Date fireTime = new Date();

        int dayNum = CalendarUtils.getDayOfWeek(fireTime);
        if (Calendar.SATURDAY == dayNum || Calendar.SUNDAY == dayNum)
            return;

        List<PreorderItemForDelete> preorderItemForDeleteList = new ArrayList<PreorderItemForDelete>();
        Map<Long, List<String>> guidListForOrg = new HashMap<Long, List<String>>();

        Session session = null;
        Transaction transaction = null;
        try {
            List<PreorderItem> preorderItemList = loadPreorders(); //все актуальные предзаказы на завтра и дальше
            session = runtimeContext.createPersistenceSession();
            session.setFlushMode(FlushMode.COMMIT);
            Map<Long, Integer> forbiddenDaysMap = new HashMap<Long, Integer>();
            Map<Long, Map<Long, DatesForPreorder>> datesMap = new HashMap<Long, Map<Long, DatesForPreorder>>();
            Map<Long, Map<Long, List<Date>>> weekends = new HashMap<Long, Map<Long, List<Date>>>();
            int size = preorderItemList.size();
            int counter = 0;
            for (PreorderItem item : preorderItemList) {
                counter++;
                logger.info(String.format("Generating %s request from %s", counter, size));
                try {
                    transaction = session.beginTransaction();
                    boolean itemWasDeleted = false;
                    for (PreorderItemForDelete itemForDelete : preorderItemForDeleteList) {
                        if (itemForDelete.idOfClient.equals(item.idOfClient) && itemForDelete.preorderDate.equals(item.preorderDate)) {
                            deletePreorderForNotEnoughMoney(session, item);
                            itemWasDeleted = true;
                            break;
                        }
                    }
                    if (itemWasDeleted) {
                        transaction.commit();
                        transaction = null;
                        continue;
                    }

                    if (null == item.getIdOfGood()) {
                        logger.warn(String.format(
                                "PreorderRequestsReportService: preorder without good item was found (preorderComplex = orgID = %s, createdDate = %s)",
                                item.getIdOfOrg(), item.getCreatedDate().toString()));
                        transaction.commit();
                        transaction = null;
                        continue;
                    }

                    if (checkPreorderDateForWeekend(session, item, weekends) && null == item.getIdOfGoodsRequestPosition()) {
                        deletePreorderForChangedCalendar(session, item);
                        transaction.commit();
                        transaction = null;
                        continue;
                    }

                    try {
                        Date startDate = new Date();
                        Date endDate = new Date();
                        Integer forbiddenDaysCount = forbiddenDaysMap.get(item.getIdOfOrg());
                        if (forbiddenDaysCount == null) {
                            forbiddenDaysCount = DAOUtils.getPreorderFeedingForbiddenDays(item.getIdOfOrg());
                            if (forbiddenDaysCount == null)
                                forbiddenDaysCount = PreorderComplex.DEFAULT_FORBIDDEN_DAYS;
                            forbiddenDaysMap.put(item.getIdOfOrg(), forbiddenDaysCount);
                        }
                        if (null != forbiddenDaysCount && forbiddenDaysCount != 0)
                            forbiddenDaysCount -= 1;

                        DatesForPreorder dates = null;
                        Map<Long, DatesForPreorder> orgMap = datesMap.get(item.getIdOfOrg());
                        if (null != orgMap) {
                            dates = orgMap.get(item.getIdOfClientGroup());
                        }
                        if (dates == null) {
                            if (!getPreorderDates(session, item.getIdOfOrg(), forbiddenDaysCount, startDate, endDate, item.getIdOfClientGroup())) {
                                transaction.commit();
                                transaction = null;
                                continue;
                            }
                            dates = new DatesForPreorder();
                            dates.startDate = startDate;
                            dates.endDate = endDate;
                            if (null == orgMap) {
                                datesMap.put(item.getIdOfOrg(), new HashMap<Long, DatesForPreorder>());
                            }
                            datesMap.get(item.getIdOfOrg()).put(item.getIdOfClientGroup(), dates);
                        }
                        startDate = dates.startDate;
                        endDate = dates.endDate;

                        if (!CalendarUtils.betweenDate(item.getPreorderDate(), startDate, endDate)) {
                            transaction.commit();
                            transaction = null;
                            continue;
                        }

                        if (null == item.getIdOfGoodsRequestPosition()) {
                            Long preordersPrice = DAOUtils.getAllPreordersPriceByClient(session, item.idOfClient,
                                    CalendarUtils.startOfDay(fireTime), endDate, item.getIdOfPreorderComplex(), item.getIdOfPreorderMenuDetail());

                            if ((item.clientBalance - item.complexPrice - preordersPrice) < 0L) {
                                logger.warn(String.format(
                                        "PreorderRequestsReportService: not enough money balance to create request (idOfClient=%d, "
                                                + "idOfPreorderComplex=%d, idOfPreorderMenuDetail=%d)", item.idOfClient,
                                        item.idOfPreorderComplex, item.idOfPreorderMenuDetail));
                                deletePreorderForNotEnoughMoney(session, item);
                                preorderItemForDeleteList.add(new PreorderItemForDelete(item.getIdOfClient(), item.getPreorderDate()));
                                transaction.commit();
                                transaction = null;
                                continue;
                            }
                            if (null == item.getDeleted() || !item.getDeleted()) {
                                String guid = createRequestFromPreorder(session, item, fireTime);
                                if (null != guid) {
                                    if (!guidListForOrg.containsKey(item.getIdOfOrg())) {
                                        guidListForOrg.put(item.getIdOfOrg(), new ArrayList<String>());
                                    }
                                    guidListForOrg.get(item.getIdOfOrg()).add(guid);
                                }
                            }

                        } else {
                            String guid = updateRequestFromPreorder(session, item, fireTime);
                            if (null != guid) {
                                if (!guidListForOrg.containsKey(item.getIdOfOrg())) {
                                    guidListForOrg.put(item.getIdOfOrg(), new ArrayList<String>());
                                }
                                guidListForOrg.get(item.getIdOfOrg()).add(guid);
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("PreorderRequestsReportService: could not create GoodRequest");
                    }
                    transaction.commit();
                    transaction = null;
                } finally {
                    HibernateUtils.rollback(transaction, logger);
                }
            }
        } finally {
            HibernateUtils.close(session, logger);
        }
        logger.info("End preorder request report gen");
        Calendar calendarEnd = RuntimeContext.getInstance().getDefaultLocalCalendar(null);
        final Date lastCreateOrUpdateDate = calendarEnd.getTime();
        calendarEnd.add(Calendar.MINUTE, 1);
        final Date endGenerateTime = calendarEnd.getTime();

        for (Long orgOwner : guidListForOrg.keySet()) {
            List<String> guids = guidListForOrg.get(orgOwner);
            if (null != guids && !guids.isEmpty())
                notifyOrg(orgOwner, fireTime, endGenerateTime, lastCreateOrUpdateDate, guids);
        }
    }

    private Boolean getPreorderDates(Session session, Long orgOwner, Integer forbiddenDaysCount, Date startDate,
            Date endDate, Long idOfClientGroup) {
        //Long idOfSourceOrg = DAOUtils.findMenuExchangeSourceOrg(session, orgOwner);
        Date _startDate = CalendarUtils.truncateToDayOfMonth(new Date());
        Date specialDaysMonth = CalendarUtils.addMonth(_startDate, 1);

        Boolean isSixWorkWeek = false; //SubscriberFeedingSettingSettingValue.SIX_WORK_WEEK
        ECafeSettings eCafeSettings = DAOUtils.getECafeSettingByIdOfOrgAndSettingId(session, orgOwner, SettingsIds.SubscriberFeeding);
        if (null == eCafeSettings) {
            logger.warn(String.format("Unable to find ECafeSettings for idOfOrg=%d and SettingsId=%d", orgOwner,
                    SettingsIds.SubscriberFeeding.getId()));
        } else {
            try {
                isSixWorkWeek = ((SubscriberFeedingSettingSettingValue) eCafeSettings.getSplitSettingValue()).isSixWorkWeek();
            } catch (Exception e) {
                logger.warn(String.format("Unable to parse setting values for idOfOrg=%d, SettingsId=%d, SettingValue=%s", orgOwner,
                        SettingsIds.SubscriberFeeding.getId(), eCafeSettings.getSettingValue()));
            }
        }

        if (!isSixWorkWeek) {
            Org org = (Org) session.load(Org.class, orgOwner);
            ClientGroup clientGroup = (ClientGroup) session.load(ClientGroup.class, new CompositeIdOfClientGroup(orgOwner, idOfClientGroup));
            GroupNamesToOrgs groupNamesToOrgs = DAOUtils.getGroupNamesToOrgsByOrgAndGroupName(session, org, clientGroup.getGroupName());
            if (null != groupNamesToOrgs) {
                Boolean isSixDaysWorkWeek = groupNamesToOrgs.getIsSixDaysWorkWeek();
                if (null != isSixDaysWorkWeek) {
                    isSixWorkWeek = isSixDaysWorkWeek;
                }
            }
        }

        Criteria specialDaysCriteria = session.createCriteria(SpecialDate.class);
        specialDaysCriteria.add(Restrictions.eq("idOfOrg", orgOwner));
        specialDaysCriteria.add(Restrictions.eq("deleted", Boolean.FALSE));
        specialDaysCriteria.add(Restrictions.between("date", _startDate, specialDaysMonth));
//        if (null != idOfClientGroup) {
            specialDaysCriteria.add(Restrictions.or(Restrictions.eq("idOfClientGroup", idOfClientGroup),
                    Restrictions.isNull("idOfClientGroup")));
            specialDaysCriteria.setProjection(Projections.projectionList()
                    .add(Projections.property("date"))
                    .add(Projections.property("idOfClientGroup"))
                    .add(Projections.property("isWeekend")));
        //} else {
        //    specialDaysCriteria.setProjection(Projections.projectionList().add(Projections.max("date")));
        //}

        List specialDates = specialDaysCriteria.list();

        Integer forbiddenCount = forbiddenDaysCount;

        do {
            _startDate = CalendarUtils.addOneDay(_startDate);

            Date endDateStart = CalendarUtils.startOfDay(_startDate);
            Date endDateEnd = CalendarUtils.endOfDay(_startDate);
            Boolean isWeekend = null;

            //check special dates
            for (Object specialDate : specialDates) {
                if (null == specialDate) {
                    continue;
                }
                Object[] vals = (Object[]) specialDate;
                if (CalendarUtils.betweenDate((Date) vals[0], endDateStart, endDateEnd)) {
                    isWeekend = (Boolean) vals[2];
                    break;
                }
            }

            if (null == isWeekend) {
                //check weekend
                isWeekend = !CalendarUtils.isWorkDateWithoutParser(isSixWorkWeek, _startDate);
            }

            if (!isWeekend) {
                forbiddenCount--;
            }
        } while (forbiddenCount >= 0);

        Date _endDate;
        int dayOfWeek = CalendarUtils.getDayOfWeek(_startDate);
        if (Calendar.SATURDAY == dayOfWeek) {
            _endDate = CalendarUtils.endOfDay(CalendarUtils.addDays(_startDate, 2));
        } else {
            _endDate = CalendarUtils.endOfDay(_startDate);
        }

        startDate.setTime(_startDate.getTime());
        endDate.setTime(_endDate.getTime());

        Date fireTimeStart = CalendarUtils.startOfDay(new Date());
        Date fireTimeEnd = CalendarUtils.endOfDay(new Date());
        for (Object date : specialDates) {
            Object[] vals = (Object[]) date;
            if (CalendarUtils.betweenOrEqualDate((Date) vals[0], fireTimeStart, fireTimeEnd)) {
                return false;
            }
        }
        return true;
    }

    private void updateDate() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();

        localCalendar = runtimeContext.getDefaultLocalCalendar(null);
        
        localCalendar.setTime(new Date());
        localCalendar.add(Calendar.DATE, -1);

        CalendarUtils.truncateToDayOfMonth(localCalendar);
        this.startDate = localCalendar.getTime();

        localCalendar.add(Calendar.DATE, 1);
        localCalendar.add(Calendar.SECOND, -1);

        this.endDate = localCalendar.getTime();
    }

    private String checkIsExistFile() throws Exception {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + TEMPLATE_FILENAME;
        if(!(new File(templateFilename)).exists()){
            throw new Exception(String.format("Не найден файл шаблона '%s'", TEMPLATE_FILENAME));
        }
        return templateFilename;
    }

    public String buildReportHTML(Long idOfContragent, List<Long> idOfOrgList) throws Exception {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFilename = checkIsExistFile();
        if (StringUtils.isEmpty(templateFilename)) {
            return null;
        }
        PreorderRequestsReport.Builder builder = new PreorderRequestsReport.Builder(templateFilename);
        builder.setReportProperties(buildProperties(idOfContragent, idOfOrgList));

        Session session = null;
        Transaction transaction = null;
        BasicReportJob report;
        try {
            session = runtimeContext.createReportPersistenceSession();
            transaction = session.beginTransaction();
            report = builder.build(session, startDate, endDate, localCalendar);
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        String result = "";
        if (null != report) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            JRHtmlExporter exporter = new JRHtmlExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, report.getPrint());
            exporter.setParameter(JRHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR, Boolean.TRUE);
            exporter.setParameter(JRHtmlExporterParameter.IMAGES_DIR_NAME, "./images/");
            exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "/images/");
            exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
            exporter.setParameter(JRHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, Boolean.FALSE);
            exporter.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
            exporter.exportReport();
            result = os.toString("UTF-8");
            os.close();
        }
        return result;
    }

    private Properties buildProperties(Long idOfContragent, List<Long> idOfOrgList) {
        Properties properties = new Properties();
        properties.setProperty(ReportPropertiesUtils.P_ID_OF_MENU_SOURCE_ORG, idOfContragent.toString());
        String idOfOrgString = "";
        if(idOfOrgList != null) {
            idOfOrgString = StringUtils.join(idOfOrgList.iterator(), ",");
        }
        properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG, idOfOrgString);
        properties.setProperty(GoodRequestsNewReport.P_HIDE_GENERATE_PERIOD, Boolean.toString(true));
        properties.setProperty(GoodRequestsNewReport.P_GENERATE_BEGIN_DATE, Long.toString(startDate.getTime()));
        properties.setProperty(GoodRequestsNewReport.P_GENERATE_END_DATE, Long.toString(endDate.getTime()));
        properties.setProperty(GoodRequestsNewReport.P_HIDE_MISSED_COLUMNS, Boolean.toString(false));
        properties.setProperty(GoodRequestsNewReport.P_HIDE_DAILY_SAMPLE_COUNT, Boolean.toString(false));
        properties.setProperty(GoodRequestsNewReport.P_HIDE_LAST_VALUE, Boolean.toString(false));
        //properties.setProperty(GoodRequestsNewReport.P_NAME_FILTER, nameFiler);
        //properties.setProperty(GoodRequestsNewReport.P_ORG_REQUEST_FILTER, "1");        //OrgRequestFilterConverter.OrgRequestFilterEnum.ORG_WITH_DATA("Только с данными");
        properties.setProperty(GoodRequestsNewReport.P_HIDE_TOTAL_ROW, Boolean.toString(true));
        //TODO
        properties.setProperty(GoodRequestsNewReport.P_HIDE_PREORDERS, Boolean.toString(false));
        properties.setProperty(GoodRequestsNewReport.P_PREORDERS_ONLY, Boolean.toString(true));
        return properties;
    }

    private List<PreorderItem> loadPreorders() {
        Session session = null;
        Transaction transaction = null;
        List<PreorderItem> preorderItemList = new ArrayList<PreorderItem>();
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            String sqlQuery =
                    "SELECT ci.idoforg, pc.createddate, pc.idofpreordercomplex, pmd.idofpreordermenudetail, " + "   CASE WHEN (pc.amount = 0) THEN md.idofgood ELSE ci.idofgood END AS idofgood, "
                            + "   CASE WHEN (pc.amount = 0) THEN pmd.amount ELSE pc.amount END AS amount," + "   CASE WHEN (pc.amount = 0) THEN pmd.idOfGoodsRequestPosition ELSE pc.idOfGoodsRequestPosition END AS idOfGoodsRequestPosition,"
                            + "   pc.preorderdate, pc.complexprice, pc.amount AS complexamount, pmd.menudetailprice, pmd.amount AS menudetailamount,"
                            + "   c.balance, c.idofclient, (coalesce(pc.deletedstate=1, FALSE) OR coalesce(pmd.deletedstate=1, FALSE)) AS isdeleted,"
                            + "   c.idofclientgroup " + "FROM cf_preorder_complex pc " + "INNER JOIN cf_clients c ON c.idofclient = pc.idofclient "
                            + "INNER JOIN cf_complexinfo ci ON c.idoforg = ci.idoforg AND ci.menudate = pc.preorderdate "
                            + "   AND ci.idofcomplex = pc.armcomplexid " + "LEFT JOIN cf_preorder_menudetail pmd ON pc.idofpreordercomplex = pmd.idofpreordercomplex AND pc.amount = 0 "
                            + "LEFT JOIN cf_menu m ON c.idoforg = m.idoforg AND pmd.preorderdate = m.menudate " + "LEFT JOIN cf_menudetails md ON m.idofmenu = md.idofmenu AND pmd.armidofmenu = md.localidofmenu "
                            + "WHERE pc.preorderdate > :date " + "   AND ((pc.amount <> 0 OR pmd.amount <> 0) OR (coalesce(pc.deletedstate = 1, FALSE) OR coalesce(pmd.deletedstate = 1, FALSE)))";

            Query query = session.createSQLQuery(sqlQuery);
            query.setParameter("date", CalendarUtils.endOfDay(new Date()).getTime());
            List data = query.list();
            for (Object entry : data) {
                Object o[] = (Object[]) entry;
                Long idOfOrg = (null != o[0]) ? ((BigInteger) o[0]).longValue() : null;
                Date createdDate = (null != o[1]) ? new Date(((BigInteger) o[1]).longValue()) : null;
                Long idOfPreorderComplex = (null != o[2]) ? ((BigInteger) o[2]).longValue() : null;
                Long idOfPreorderMenuDetail = (null != o[3]) ? ((BigInteger) o[3]).longValue() : null;
                Long idOfGood = (null != o[4]) ? ((BigInteger) o[4]).longValue() : null;
                Integer amount = (Integer) o[5];
                Long idOfGoodsRequest = (null != o[6]) ? ((BigInteger) o[6]).longValue() : null;
                Date preorderDate = (null != o[7]) ? new Date(((BigInteger) o[7]).longValue()) : null;

                Long complexPrice = (null != o[8]) ? ((BigInteger) o[8]).longValue() : 0L;
                Integer complexAmount = (null != o[9]) ? (Integer) o[9] : 0;
                Long menuDetailPrice = (null != o[10]) ? ((BigInteger) o[10]).longValue() : 0L;
                Integer menuDetailAmount = (null != o[11]) ? (Integer) o[11] : 0;
                Long clientBalance = (null != o[12]) ? ((BigInteger) o[12]).longValue() : 0L;
                Long idOfClient = (null != o[13]) ? ((BigInteger) o[13]).longValue() : null;
                Boolean isDeleted = (Boolean) o[14];
                Boolean isComplex = !complexAmount.equals(0);
                Long idOfClientGroup = (null != o[15]) ? ((BigInteger) o[15]).longValue() : null;
                Long usedSum = (null != o[16]) ? ((BigInteger) o[16]).longValue() : 0L;

                preorderItemList
                        .add(new PreorderItem(idOfPreorderComplex, idOfPreorderMenuDetail, idOfOrg, idOfGood, amount,
                                createdDate, idOfGoodsRequest, preorderDate,
                                complexPrice * complexAmount + menuDetailPrice * menuDetailAmount, clientBalance,
                                idOfClient, isDeleted, isComplex, idOfClientGroup, usedSum));
            }
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return preorderItemList;
    }

    private List<PreorderItem> loadPreorders2(Date dateTo) {
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
                            + "   CASE WHEN (pc.amount = 0) THEN md.idofgood ELSE ci.idofgood END AS idofgood, "           //4
                            + "   CASE WHEN (pc.amount = 0) THEN pmd.amount ELSE pc.amount END AS amount,"                 //5
                            + "   pc.preorderdate, "                                                                       //6
                            + "pc.complexprice, "                                                                          //7
                            + "pc.amount AS complexamount, "                                                               //8
                            + "pmd.menudetailprice, "                                                                      //9
                            + "pmd.amount AS menudetailamount, "                                                           //10
                            + "c.balance, "                                                                                //11
                            + "c.idofclient, "                                                                             //12
                            + "c.idofclientgroup, "                                                                        //13
                            + "pc.usedsum "                                                                                //14
                            + "FROM cf_preorder_complex pc INNER JOIN cf_clients c ON c.idofclient = pc.idofclient "
                            + "INNER JOIN cf_complexinfo ci ON c.idoforg = ci.idoforg AND ci.menudate = pc.preorderdate AND ci.idofcomplex = pc.armcomplexid "
                            + "LEFT JOIN cf_preorder_menudetail pmd ON pc.idofpreordercomplex = pmd.idofpreordercomplex AND pc.amount = 0 and pmd.deletedstate = 0 and pmd.idOfGoodsRequestPosition is null "
                            + "LEFT JOIN cf_menu m ON c.idoforg = m.idoforg AND pmd.preorderdate = m.menudate "
                            + "LEFT JOIN cf_menudetails md ON m.idofmenu = md.idofmenu AND pmd.armidofmenu = md.localidofmenu "
                            + "WHERE pc.preorderdate > :date " + (dateTo != null ? " and pc.preorderdate < :dateTo " : "")
                            + "   AND (pc.amount <> 0 OR pmd.amount <> 0) and pc.deletedstate = 0 and pc.idOfGoodsRequestPosition is null order by pc.preorderdate";

            Query query = session.createSQLQuery(sqlQuery);
            query.setParameter("date", CalendarUtils.endOfDay(new Date()).getTime());
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
                Long idOfGoodsRequest = null; //(null != o[6]) ? ((BigInteger) o[6]).longValue() : null;
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

                preorderItemList
                        .add(new PreorderItem(idOfPreorderComplex, idOfPreorderMenuDetail, idOfOrg, idOfGood, amount,
                                createdDate, idOfGoodsRequest, preorderDate,
                                complexPrice * complexAmount + menuDetailPrice * menuDetailAmount, clientBalance,
                                idOfClient, isDeleted, isComplex, idOfClientGroup, usedSum));
            }
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return preorderItemList;
    }


    private String createRequestFromPreorder(Session session, PreorderItem preorderItem, Date fireTime) {
        //  Формируем номер по маске {idOfOrg}-{yyMMdd}-ЗВК-{countToDay}
        Date now = new Date(System.currentTimeMillis());
        String number = "";
        number = "" + preorderItem.getIdOfOrg();
        number = number + "-" + new SimpleDateFormat("yyMMdd").format(now);
        number = number + "-ЗВК-";
        number = number + DAOUtils.getNextGoodRequestNumberForOrgPerDay(session, preorderItem.getIdOfOrg(), now);

        Good good = DAOService.getInstance().getGood(preorderItem.getIdOfGood());
        Staff staff = DAOUtils.getAdminStaffFromOrg(session, preorderItem.getIdOfOrg());

        if (null == good || null == staff)
            return null;

        //  Создание GoodRequest
        GoodRequest goodRequest = new GoodRequest();
        goodRequest.setOrgOwner(preorderItem.getIdOfOrg());
        goodRequest.setDateOfGoodsRequest(preorderItem.getCreatedDate());
        goodRequest.setDoneDate(preorderItem.getPreorderDate());
        goodRequest.setNumber(number);
        goodRequest.setState(DocumentState.FOLLOW);
        goodRequest.setDeletedState(false);
        goodRequest.setCreatedDate(fireTime);
        //goodRequest.setLastUpdate(fireTime);
        goodRequest.setComment(PREORDER_COMMENT);
        goodRequest.setRequestType(PREORDER_REQUEST_TYPE);
        goodRequest.setStaff(staff);
        goodRequest.setGuidOfStaff(staff.getGuid());
        goodRequest = save(session, goodRequest, GoodRequest.class.getSimpleName());

        //  Создание GoodRequestPosition
        GoodRequestPosition pos = new GoodRequestPosition();
        pos.setGoodRequest(goodRequest);
        pos.setGood(good);
        pos.setDeletedState(false);
        pos.setOrgOwner(preorderItem.getIdOfOrg());
        pos.setUnitsScale(good.getUnitsScale());
        pos.setNetWeight(good.getNetWeight());
        pos.setCreatedDate(fireTime);
        pos.setTotalCount(preorderItem.getAmount() * 1000L);
        pos.setDailySampleCount(0L);
        pos.setTempClientsCount(0L);
        pos.setNotified(false);
        //pos.setLastUpdate(fireTime);
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

        return pos.getGuid();
    }



    private String createRequestFromPreorder2(Session session, PreorderItem preorderItem, Date fireTime, Long num, Staff staff) {
        //  Формируем номер по маске {idOfOrg}-{yyMMdd}-ЗВК-{countToDay}
        Date now = new Date(System.currentTimeMillis());
        String number = "" + preorderItem.getIdOfOrg() + "-" + new SimpleDateFormat("yyMMdd").format(now) + "-ЗВК-" + num;

        Good good = DAOService.getInstance().getGood(preorderItem.getIdOfGood());

        if (null == good || null == staff)
            return null;

        //  Создание GoodRequest
        GoodRequestTemp goodRequest = new GoodRequestTemp();
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
//        goodRequest = save(session, goodRequest, GoodRequestTemp.class.getSimpleName());
        session.save(goodRequest);

        //  Создание GoodRequestPosition
        GoodRequestPositionTemp pos = new GoodRequestPositionTemp();
        pos.setGoodRequest(goodRequest);
        pos.setGood(good);
        pos.setDeletedState(false);
        pos.setOrgOwner(preorderItem.getIdOfOrg());
        pos.setUnitsScale(good.getUnitsScale());
        pos.setNetWeight(good.getNetWeight());
        pos.setCreatedDate(fireTime);
        pos.setTotalCount(preorderItem.getAmount() * 1000L);
        pos.setDailySampleCount(0L);
        pos.setTempClientsCount(0L);
        pos.setNotified(false);
//        pos = save(session, pos, GoodRequestPositionTemp.class.getSimpleName());
        session.save(pos);

        /*if (preorderItem.getComplex()) {
            PreorderComplex complex = (PreorderComplex) session.get(PreorderComplex.class, preorderItem.getIdOfPreorderComplex());
            complex.setIdOfGoodsRequestPosition(pos.getGlobalId());
            session.update(complex);
        } else {
            PreorderMenuDetail detail = (PreorderMenuDetail) session.get(PreorderMenuDetail.class,
                    preorderItem.getIdOfPreorderMenuDetail());
            detail.setIdOfGoodsRequestPosition(pos.getGlobalId());
            session.update(detail);
        }*/

        return pos.getGuid();
    }

    private String updateRequestFromPreorder(Session session, PreorderItem item, Date fireTime) {
        GoodRequestPosition pos = (GoodRequestPosition) session.load(GoodRequestPosition.class, item.getIdOfGoodsRequestPosition());
        if (null == pos) {
            logger.error("PreorderRequestsReportService: could not find GoodRequestPosition with id=" + item.getIdOfGoodsRequestPosition().toString());
            return null;
        }
        Boolean isUpdated = false;

        GoodRequest request = pos.getGoodRequest();

        if (null != item.getDeleted() && item.getDeleted()) {
            pos.setDeletedState(true);
            pos = save(session, pos, GoodRequestPosition.class.getSimpleName());
            request.setDeletedState(true);
            request = save(session, request, GoodRequest.class.getSimpleName());
            isUpdated = true;
        } else {
            if ((item.getAmount() * 1000L) != pos.getTotalCount()) {
                Long lastTotal = pos.getTotalCount();
                pos.setTotalCount(item.getAmount() * 1000L);
                pos.setLastTotalCount(lastTotal);
                pos = save(session, pos, GoodRequestPosition.class.getSimpleName());
                isUpdated = true;
            }
            if (!item.getPreorderDate().equals(request.getDoneDate())) {
                request.setDoneDate(item.getPreorderDate());
                request.setLastUpdate(fireTime);
                request = save(session, request, GoodRequest.class.getSimpleName());
                isUpdated = true;
            }
        }

        if (isUpdated) {
            return pos.getGuid();
        }
        return null;
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

    @Async
    public void notifyOrg(final Long idOfOrg, final Date beginGenerateTime, final Date endGenerateTime,
            final Date lastCreateOrUpdateDate, List<String> guids) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Calendar localCalendar = runtimeContext.getDefaultLocalCalendar(null);
        Session session = null;
        Transaction transaction = null;
        /* проверим есть ли измененые заявки на неделю */

        Date minDone = new Date();
        Date maxDone = new Date();
        try {
            try {
                session = runtimeContext.createPersistenceSession();
                transaction = session.beginTransaction();

                Criteria criteria = session.createCriteria(GoodRequestPosition.class);
                criteria.createAlias("goodRequest", "gr");
                criteria.add(Restrictions.in("guid", guids));
                criteria.setProjection(Projections.projectionList()
                        .add(Projections.max("gr.doneDate")).add(Projections.min("gr.doneDate")));
                List list = criteria.list();

                if (list != null && !list.isEmpty()) {
                    Object[] objects = (Object[]) list.get(0);
                    maxDone = (Date) objects[0];
                    minDone = (Date) objects[1];
                    if (minDone != null) {
                        minDone = CalendarUtils.truncateToDayOfMonth(minDone);
                    }
                }
                transaction.commit();
                transaction = null;
            } finally {
                HibernateUtils.rollback(transaction, logger);
                HibernateUtils.close(session, logger);
            }
        } catch (Exception e) {
            logger.error("Failed export report : ", e);
        }

        if (maxDone == null || minDone == null) {
            return;
        }

        class DateInterval {
            private final Date beginDate;
            private final Date endDate;

            DateInterval(Date beginDate, Date endDate) {
                this.beginDate = beginDate;
                this.endDate = endDate;
            }

            public Date getBeginDate() {
                return beginDate;
            }

            public Date getEndDate() {
                return endDate;
            }
        }

        List<DateInterval> intervals = new ArrayList<DateInterval>();
        //Подправить интервалы в зависимости от дня недели день начало второй недели
        Date stDate = minDone;
        Date enDate;

        CalendarUtils.truncateToDayOfMonth(localCalendar);
        String weekDay = CalendarUtils.dayInWeekToString(stDate);

        localCalendar.setTime(stDate);

        if (weekDay.equals("Вт")) {
            localCalendar.add(Calendar.DATE, -1);
            stDate = localCalendar.getTime();
        } else if (weekDay.equals("Ср")) {
            localCalendar.add(Calendar.DATE, -2);
            stDate = localCalendar.getTime();
        } else if (weekDay.equals("Чт")) {
            localCalendar.add(Calendar.DATE, -3);
            stDate = localCalendar.getTime();
        } else if (weekDay.equals("Пт")) {
            localCalendar.add(Calendar.DATE, -4);
            stDate = localCalendar.getTime();
        } else if (weekDay.equals("Сб")) {
            localCalendar.add(Calendar.DATE, -5);
            stDate = localCalendar.getTime();
        } else if (weekDay.equals("Вс")) {
            localCalendar.add(Calendar.DATE, -6);
            stDate = localCalendar.getTime();
        }

        localCalendar.add(Calendar.DATE, maxNumDays - 1);
        localCalendar.add(Calendar.MILLISECOND, -1);
        enDate = localCalendar.getTime();

        intervals.add(new DateInterval(stDate, enDate));
        Date eD = CalendarUtils.truncateToDayOfMonth(enDate);
        Date mD = CalendarUtils.truncateToDayOfMonth(maxDone);

        while (eD.before(mD)) {
            localCalendar.add(Calendar.DATE, 1);
            localCalendar.add(Calendar.MILLISECOND, -1);
            stDate = localCalendar.getTime();
            localCalendar.add(Calendar.DATE, maxNumDays - 1);
            localCalendar.add(Calendar.MILLISECOND, -1);
            enDate = localCalendar.getTime();
            eD = CalendarUtils.truncateToDayOfMonth(enDate);
            intervals.add(new DateInterval(stDate, enDate));
        }

        GoodRequestsChangeAsyncNotificationService.OrgItem item = orgItems.get(idOfOrg);
        String templateFilename;
        try {
            templateFilename = checkIsExistFile();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return;
        }
        PreorderRequestsReport.Builder builder = new PreorderRequestsReport.Builder(templateFilename);
        Properties properties = new Properties();
        properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG, Long.toString(item.getIdOfOrg()));
        if (item.getIdOfSourceMenu() != null) {
            properties.setProperty(ReportPropertiesUtils.P_ID_OF_MENU_SOURCE_ORG,
                    Long.toString(item.getIdOfSourceMenu()));
        }
        properties.setProperty(GoodRequestsNewReport.P_ORG_REQUEST_FILTER, "0");
        properties.setProperty(GoodRequestsNewReport.P_HIDE_GENERATE_PERIOD, Boolean.toString(true));
        properties.setProperty(GoodRequestsNewReport.P_GENERATE_BEGIN_DATE,
                Long.toString(beginGenerateTime.getTime()));
        properties.setProperty(GoodRequestsNewReport.P_GENERATE_END_DATE, Long.toString(endGenerateTime.getTime()));
        properties.setProperty(GoodRequestsNewReport.P_LAST_CREATE_OR_UPDATE_DATE,
                Long.toString(lastCreateOrUpdateDate.getTime()));
        properties.setProperty(GoodRequestsNewReport.P_HIDE_MISSED_COLUMNS, Boolean.toString(isHideMissedCol));
        properties.setProperty(GoodRequestsNewReport.P_HIDE_DAILY_SAMPLE_COUNT, Boolean.toString(false));
        properties.setProperty(GoodRequestsNewReport.P_HIDE_LAST_VALUE, Boolean.toString(false));
        properties.setProperty(GoodRequestsNewReport.P_HIDE_TOTAL_ROW, Boolean.toString(true));
        properties.setProperty(GoodRequestsNewReport.P_NOTIFICATION, Boolean.toString(true));
        properties.setProperty(PreorderRequestsReport.P_GUID_FILTER, StringUtils.join(guids, ","));
        //TODO
        properties.setProperty(GoodRequestsNewReport.P_HIDE_PREORDERS, Boolean.toString(false));
        properties.setProperty(GoodRequestsNewReport.P_PREORDERS_ONLY, Boolean.toString(true));
        builder.setReportProperties(properties);
        BasicReportJob reportJob = null;
        /* создаем отчет */
        String htmlReport = "";

        Collections.reverse(intervals);

        Date currentDate = new Date();

        for (DateInterval interval : intervals) {
            if (interval.endDate.after(currentDate)) {
                try {
                    try {
                        session = runtimeContext.createPersistenceSession();
                        transaction = session.beginTransaction();
                        reportJob = builder
                                .build(session, interval.beginDate, interval.endDate, localCalendar);
                        transaction.commit();
                        transaction = null;
                    } finally {
                        HibernateUtils.rollback(transaction, logger);
                        HibernateUtils.close(session, logger);
                    }
                } catch (Exception e) {
                    logger.error("Failed export report : ", e);
                }
                if (reportJob != null) {
                    try {
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        JRHtmlExporter exporter = new JRHtmlExporter();
                        exporter.setParameter(JRExporterParameter.JASPER_PRINT, reportJob.getPrint());
                        exporter.setParameter(JRHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR, Boolean.TRUE);
                        exporter.setParameter(JRHtmlExporterParameter.IMAGES_DIR_NAME, "./images/");
                        exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "/images/");
                        exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
                        exporter.setParameter(JRHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, Boolean.FALSE);
                        exporter.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
                                Boolean.TRUE);
                        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
                        exporter.exportReport();
                        htmlReport = os.toString("UTF-8");
                        os.close();
                    } catch (Exception e) {
                        logger.error("Failed build report ", e);
                    }
                } else {
                    logger.debug("IdOfOrg: " + idOfOrg + " reportJob is null");
                }
                if (StringUtils.isNotEmpty(htmlReport)) {
                    boolean modifyTypeEdit = htmlReport.contains("#FF6666");
                    boolean modifyTypeCreate = htmlReport.contains("#92D050");
                    String reportType;
                    if (modifyTypeCreate && modifyTypeEdit) {
                        reportType = "ОП";
                    } else if (modifyTypeCreate) {
                        reportType = "НП";
                    } else if (modifyTypeEdit) {
                        reportType = "КП";
                    } else {
                        continue;
                    }

                    String[] values = {
                            "address", item.getAddress(), "shortOrgName", item.getShortName(), "reportValues", htmlReport,
                            "reportType", reportType};
                    List<String> strings = Arrays
                            .asList(StringUtils.split(item.getDefaultSupplier().getRequestNotifyMailList(), ";"));
                    Set<String> addresses = new HashSet<String>(strings);

                    /* Закладываем почтовые ящики ответсвенных по питанию в школе если таковые имеются */
                    try {
                        try {
                            session = runtimeContext.createReportPersistenceSession();
                            transaction = session.beginTransaction();
                            GoodRequestsChangeAsyncNotificationService.addEmailFromClient(session, idOfOrg, addresses);
                            transaction.commit();
                            transaction = null;
                        } finally {
                            HibernateUtils.rollback(transaction, logger);
                            HibernateUtils.close(session, logger);
                        }
                    } catch (Exception e) {
                        logger.error("Find email from clients : ", e);
                    }

                    try {
                        try {
                            session = runtimeContext.createReportPersistenceSession();
                            transaction = session.beginTransaction();
                            GoodRequestsChangeAsyncNotificationService.addEmailFromUser(session, idOfOrg, addresses);
                            transaction.commit();
                            transaction = null;
                        } finally {
                            HibernateUtils.rollback(transaction, logger);
                            HibernateUtils.close(session, logger);
                        }
                    } catch (Exception e) {
                        logger.error("Find email from user : ", e);
                    }
                    logger.debug("addresses " + addresses.toString());
                    for (String address : addresses) {
                        if (StringUtils.trimToNull(address) != null) {
                            RuntimeContext.getAppContext().getBean(EventNotificationService.class).sendEmailAsync(address,
                                    EventNotificationService.NOTIFICATION_GOOD_REQUEST_CHANGE, values);
                        }
                    }
                } else {
                    logger.debug("IdOfOrg: " + idOfOrg + " email text is empty");
                }
            }
        }
    }

    private Boolean checkPreorderDateForWeekend(Session session, PreorderItem item, Map<Long, Map<Long, List<Date>>> weekends) {
        Map<Long, List<Date>> groupWeekends = weekends.get(item.getIdOfOrg());
        if (null == groupWeekends || null == groupWeekends.get(item.getIdOfClientGroup())) {
            Date _startDate = CalendarUtils.truncateToDayOfMonth(new Date());
            Date specialDaysMonth = CalendarUtils.addMonth(_startDate, 1);

            Criteria specialDaysCriteria = session.createCriteria(SpecialDate.class);
            specialDaysCriteria.add(Restrictions.eq("idOfOrg", item.getIdOfOrg()));
            specialDaysCriteria.add(Restrictions.eq("isWeekend", Boolean.TRUE));
            specialDaysCriteria.add(Restrictions.eq("deleted", Boolean.FALSE));
            specialDaysCriteria.add(Restrictions.between("date", _startDate, specialDaysMonth));
            if (null != item.getIdOfClientGroup()) {
                specialDaysCriteria.add(Restrictions.or(Restrictions.eq("idOfClientGroup", item.getIdOfClientGroup()),
                        Restrictions.isNull("idOfClientGroup")));
                specialDaysCriteria.setProjection(Projections.projectionList().add(Projections.property("date"))
                        .add(Projections.property("idOfClientGroup")));
            } else {
                specialDaysCriteria.setProjection(Projections.projectionList().add(Projections.max("date")));
            }

            List list = specialDaysCriteria.list();

            weekends.put(item.getIdOfOrg(), new HashMap<Long, List<Date>>());
            groupWeekends = weekends.get(item.getIdOfOrg());

            if (null == groupWeekends.get(item.getIdOfClientGroup())) {
                groupWeekends.put(item.getIdOfClientGroup(), new ArrayList<Date>());
            }

            for (Object object : list) {
                Object[] vals = (Object[]) object;
                groupWeekends.get(item.getIdOfClientGroup()).add((Date) vals[0]);
            }
        }

        if (null != groupWeekends) {
            if (null != groupWeekends.get(item.getIdOfClientGroup())) {
                for (Date date : groupWeekends.get(item.getIdOfClientGroup())) {
                    Date startDate = CalendarUtils.startOfDay(date);
                    Date endDate = CalendarUtils.endOfDay(date);
                    if (CalendarUtils.betweenOrEqualDate(item.getPreorderDate(), startDate, endDate)) {
                        return true;
                    }
                }
            }
        }
        return false;
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

        public PreorderItem(Long idOfPreorderComplex, Long idOfPreorderMenuDetail, Long idOfOrg, Long idOfGood, Integer amount,
                Date createdDate, Long idOfGoodsRequestPosition, Date preorderDate, Long complexPrice, Long clientBalance,
                Long idOfClient, Boolean isDeleted, Boolean isComplex, Long idOfClientGroup, Long usedSum) {
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

