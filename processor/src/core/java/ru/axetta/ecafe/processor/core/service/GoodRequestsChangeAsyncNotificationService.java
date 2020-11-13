/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;


import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.Staff;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.GoodRequestsNewReport;
import ru.axetta.ecafe.processor.core.report.PreorderRequestsReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.12.13
 */
@Service
public class GoodRequestsChangeAsyncNotificationService {

    private final static Logger LOGGER = LoggerFactory.getLogger(GoodRequestsChangeAsyncNotificationService.class);
    private Map<Long, ContragentItem> contragentItems;
    private Map<Long, OrgItem> orgItems = new HashMap<Long, OrgItem>();
    private RuntimeContext runtimeContext;
    private int maxNumDays;
    private boolean enableNotify;
    private boolean isHideMissedCol;
    @Autowired
    private EventNotificationService eventNotificationService;
    @PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManager;

    public static GoodRequestsChangeAsyncNotificationService getInstance() {
        return RuntimeContext.getAppContext().getBean(GoodRequestsChangeAsyncNotificationService.class);
    }

    @PostConstruct
    public void init() {
        runtimeContext = RuntimeContext.getInstance();
        maxNumDays = runtimeContext.getOptionValueInt(Option.OPTION_MAX_NUM_DAYS_NOTIFICATION_GOOD_REQUEST_CHANGE);
        enableNotify = runtimeContext.getOptionValueBool(Option.OPTION_ENABLE_NOTIFICATION_GOOD_REQUEST_CHANGE);
        isHideMissedCol = runtimeContext
                .getOptionValueBool(Option.OPTION_HIDE_MISSED_COL_NOTIFICATION_GOOD_REQUEST_CHANGE);
        refreshAllInformation();
    }

    @Async
    public void notifyOrg(final Long idOfOrg, final Date beginGenerateTime, final Date endGenerateTime,
            final Date lastCreateOrUpdateDate, List<String> guids, boolean isWtMenu) {
        if (!enableNotify) {
            return;
        }
        if (orgItems.containsKey(idOfOrg)) {
            Calendar localCalendar = runtimeContext.getDefaultLocalCalendar(null);
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            /* проверим есть ли измененые заявки на неделю */

            Date minDone = new Date();
            Date maxDone = new Date();
            try {
                try {
                    //persistenceSession = runtimeContext.createReportPersistenceSession();
                    persistenceSession = runtimeContext.createPersistenceSession();
                    persistenceTransaction = persistenceSession.beginTransaction();

                    /**
                     * Запрос:
                     *  Select max(gr.doneDate), min(gr.doneDate) from cf_Goods_Requests_Positions as pos
                     *  inner join cf_Goods_Requests gr on gr.idofGoodRequest = pos.idofGoodRequest
                     *  where pos.guid in (:guid)
                     */

                    Criteria requestCriteria = persistenceSession.createCriteria(GoodRequestPosition.class);
                    requestCriteria.createAlias("goodRequest", "gr");    // join
                    requestCriteria.add(Restrictions.in("guid", guids));  // where
                    requestCriteria.setProjection(Projections.projectionList()  // внутри селекта
                            .add(Projections.max("gr.doneDate")).add(Projections.min("gr.doneDate")));
                    //Не показываем заявки, которые сохранены, но были отклонены по причине неверной даты (у них GlobalVersion = 0)
                    requestCriteria.add(Restrictions.not(Restrictions.eq("globalVersion", 0L)));
                    requestCriteria.add(Restrictions.not(Restrictions.eq("gr.globalVersion", 0L)));

                    List list = requestCriteria.list();


                    if (list != null && !list.isEmpty()) {
                        Object[] objects = (Object[]) list.get(0);
                        maxDone = (Date) objects[0];
                        minDone = (Date) objects[1];
                        if (minDone != null) {
                            minDone = CalendarUtils.truncateToDayOfMonth(minDone);
                        }
                    }
                    persistenceTransaction.commit();
                    persistenceTransaction = null;
                } finally {
                    HibernateUtils.rollback(persistenceTransaction, LOGGER);
                    HibernateUtils.close(persistenceSession, LOGGER);
                }
            } catch (Exception e) {
                LOGGER.error("Failed export report : ", e);
            }

            if (maxDone == null || minDone == null) {
                return;
            }

            LOGGER.debug("Current day: ... Max day: ... Min day: ... ");

            class DateInterval {

                final Date beginDate;
                final Date endDate;

                DateInterval(Date beginDate, Date endDate) {
                    this.beginDate = beginDate;
                    this.endDate = endDate;
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

            LOGGER.debug(String.valueOf(intervals.size()));
            String message = "";
            message += "IdOfOrg: " + idOfOrg;
            message += " current time " + (new Date());
            message += " beginGenTime " + beginGenerateTime;
            message += " endGenerateTime " + endGenerateTime;
            LOGGER.debug(message);
            OrgItem item = orgItems.get(idOfOrg);
            String templateFilename = checkIsExistFile("_notify.jasper");
            if (StringUtils.isEmpty(templateFilename)) {
                LOGGER.debug("IdOfOrg: " + idOfOrg + " template not found");
                return;
            }
            GoodRequestsNewReport.Builder builder = new GoodRequestsNewReport.Builder(templateFilename);
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
            properties.setProperty(GoodRequestsNewReport.P_HIDE_PREORDERS, Boolean.toString(true));
            properties.setProperty(GoodRequestsNewReport.P_PREORDERS_ONLY, Boolean.toString(false));
            properties.setProperty(GoodRequestsNewReport.P_IS_EMAIL_NOTIFY, Boolean.toString(true));
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
                            //persistenceSession = runtimeContext.createReportPersistenceSession();
                            persistenceSession = runtimeContext.createPersistenceSession();
                            persistenceTransaction = persistenceSession.beginTransaction();
                            reportJob = builder
                                    .build(persistenceSession, interval.beginDate, interval.endDate, localCalendar,
                                            isWtMenu, true);
                            //reportJob = builder.build(persistenceSession, startDate, endDate, localCalendar);
                            persistenceTransaction.commit();
                            persistenceTransaction = null;
                        } finally {
                            HibernateUtils.rollback(persistenceTransaction, LOGGER);
                            HibernateUtils.close(persistenceSession, LOGGER);
                        }
                    } catch (Exception e) {
                        LOGGER.error("Failed export report : ", e);
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
                            LOGGER.error("Failed build report ", e);
                        }
                    } else {
                        LOGGER.debug("IdOfOrg: " + idOfOrg + " reportJob is null");
                    }
                    if (StringUtils.isNotEmpty(htmlReport)) {
                        boolean modifyTypeEdit = htmlReport.contains("#FF6666");
                        boolean modifyTypeCreate = htmlReport.contains("#92D050");
                        String reportType;
                        if (modifyTypeCreate && modifyTypeEdit) {
                            reportType = "О";
                        } else if (modifyTypeCreate) {
                            reportType = "Н";
                        } else if (modifyTypeEdit) {
                            reportType = "К";
                        } else {
                            continue;
                        }

                        String[] values = {
                                "address", item.address, "shortOrgName", item.shortName, "reportValues", htmlReport,
                                "reportType", reportType};
                        List<String> strings = Arrays
                                .asList(StringUtils.split(item.getDefaultSupplier().requestNotifyMailList, ";"));
                        Set<String> addresses = new HashSet<String>(strings);

                        /* Закладываем почтовые ящики ответсвенных по питанию в школе если таковые имеются */
                        try {
                            try {
                                persistenceSession = runtimeContext.createReportPersistenceSession();
                                persistenceTransaction = persistenceSession.beginTransaction();
                                addEmailFromClient(persistenceSession, idOfOrg, addresses);
                                persistenceTransaction.commit();
                                persistenceTransaction = null;
                            } finally {
                                HibernateUtils.rollback(persistenceTransaction, LOGGER);
                                HibernateUtils.close(persistenceSession, LOGGER);
                            }
                        } catch (Exception e) {
                            LOGGER.error("Find email from clients : ", e);
                        }

                        try {
                            try {
                                persistenceSession = runtimeContext.createReportPersistenceSession();
                                persistenceTransaction = persistenceSession.beginTransaction();
                                addEmailFromUser(persistenceSession, idOfOrg, addresses);
                                persistenceTransaction.commit();
                                persistenceTransaction = null;
                            } finally {
                                HibernateUtils.rollback(persistenceTransaction, LOGGER);
                                HibernateUtils.close(persistenceSession, LOGGER);
                            }
                        } catch (Exception e) {
                            LOGGER.error("Find email from user : ", e);
                        }
                        LOGGER.debug("addresses " + addresses.toString());
                        //boolean sended = false;
                        for (String address : addresses) {
                            if (StringUtils.trimToNull(address) != null) {
                                eventNotificationService.sendEmailAsync(address,
                                        EventNotificationService.NOTIFICATION_GOOD_REQUEST_CHANGE, values);
                            }
                        }
                        //eventNotificationService.sendEmailAsync("ziganshin@axetta.ru",
                        //        EventNotificationService.NOTIFICATION_GOOD_REQUEST_CHANGE, values);
                        //eventNotificationService.sendEmailAsync("petrova@axetta.ru",
                        //        EventNotificationService.NOTIFICATION_GOOD_REQUEST_CHANGE, values);
                    } else {
                        LOGGER.debug("IdOfOrg: " + idOfOrg + " email text is empty");
                    }
                }
            }
        } else {
            LOGGER.debug("this org not notify: " + idOfOrg);
        }
    }

    @Async
    public void notifyOrg(OrgItem orgItem, final Date beginGenerateTime, final Date endGenerateTime,
            final Date lastCreateOrUpdateDate, Date requestDate) {
        LOGGER.info("Start notifyOrg method idOfOrg=" + orgItem.getIdOfOrg());
        Calendar localCalendar = runtimeContext.getDefaultLocalCalendar(null);

        String templateFilename;
        try {
            templateFilename = RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class).checkIsExistFile();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return;
        }
        PreorderRequestsReport.Builder builder = new PreorderRequestsReport.Builder(templateFilename);
        Properties properties = new Properties();
        properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG, Long.toString(orgItem.getIdOfOrg()));
        if (orgItem.getIdOfSourceMenu() != null) {
            properties.setProperty(ReportPropertiesUtils.P_ID_OF_MENU_SOURCE_ORG,
                    Long.toString(orgItem.getIdOfSourceMenu()));
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
        //TODO
        properties.setProperty(GoodRequestsNewReport.P_HIDE_PREORDERS, Boolean.toString(false));
        properties.setProperty(GoodRequestsNewReport.P_PREORDERS_ONLY, Boolean.toString(true));
        builder.setReportProperties(properties);
        BasicReportJob reportJob = null;
        /* создаем отчет */
        String htmlReport = "";

        LOGGER.info("Start build report");
        Session session = null;
        Transaction transaction = null;
        try {
            session = runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();
            reportJob = builder
                    .build(session, CalendarUtils.startOfDay(requestDate), CalendarUtils.endOfDay(requestDate), localCalendar);
            transaction.commit();
            transaction = null;
            LOGGER.info("Successful end build report");
        } catch (Exception e) {
            LOGGER.error("Failed build report : ", e);
        } finally {
            HibernateUtils.rollback(transaction, LOGGER);
            HibernateUtils.close(session, LOGGER);
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
                LOGGER.error("Failed export report ", e);
            }
        } else {
            LOGGER.error("IdOfOrg: " + orgItem.getIdOfOrg() + " reportJob is null");
        }
        if (StringUtils.isNotEmpty(htmlReport)) {
            String reportType = "НП";

            String[] values = {
                    "address", orgItem.getAddress(), "shortOrgName", orgItem.getShortName(), "reportValues", htmlReport,
                    "reportType", reportType};
            List<String> strings = Arrays
                    .asList(StringUtils.split(orgItem.getDefaultSupplier().getRequestNotifyMailList(), ";"));
            Set<String> addresses = new HashSet<String>(strings);

            /* Закладываем почтовые ящики ответсвенных по питанию в школе если таковые имеются */
            try {
                session = runtimeContext.createReportPersistenceSession();
                transaction = session.beginTransaction();
                GoodRequestsChangeAsyncNotificationService.addEmailFromClient(session, orgItem.getIdOfOrg(), addresses);
                transaction.commit();
                transaction = null;
            } catch (Exception e) {
                LOGGER.error("Find email from clients : ", e);
            } finally {
                HibernateUtils.rollback(transaction, LOGGER);
                HibernateUtils.close(session, LOGGER);
            }

            try {
                session = runtimeContext.createReportPersistenceSession();
                transaction = session.beginTransaction();
                GoodRequestsChangeAsyncNotificationService.addEmailFromUser(session, orgItem.getIdOfOrg(), addresses);
                transaction.commit();
                transaction = null;
            } catch (Exception e) {
                LOGGER.error("Find email from user : ", e);
            } finally {
                HibernateUtils.rollback(transaction, LOGGER);
                HibernateUtils.close(session, LOGGER);
            }
            LOGGER.info("addresses " + addresses.toString());
            for (String address : addresses) {
                if (StringUtils.trimToNull(address) != null) {
                    LOGGER.info("Send email async");
                    RuntimeContext.getAppContext().getBean(EventNotificationService.class).sendEmailAsync(address,
                            EventNotificationService.NOTIFICATION_GOOD_REQUEST_CHANGE, values);
                }
            }
        } else {
            LOGGER.info("IdOfOrg: " + orgItem.getIdOfOrg() + " email text is empty");
        }
        LOGGER.info("End notifyOrg method idOfOrg=" + orgItem.getIdOfOrg());
    }

    private String checkIsExistFile(String suffix) {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateShortFileName = GoodRequestsNewReport.class.getSimpleName() + suffix;
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFileName;
        if (!(new File(templateFilename)).exists()) {
            return null;
        }
        return templateFilename;
    }

    public void refreshAllInformation() {
        updateContragentItems();
        updateOrgItems();
    }

    public void updateContragentItems() {
        contragentItems = getInstance().findContragentItems();
    }

    public void updateContragentItem(Session session, Contragent contragent) throws Exception {
        final Long idOfContragent = contragent.getIdOfContragent();
        ContragentItem item = contragentItems.get(idOfContragent);
        if (item == null) {
            item = new ContragentItem(idOfContragent, contragent.getRequestNotifyMailList());
            contragentItems.put(idOfContragent, item);
        } else {
            item.setRequestNotifyMailList(contragent.getRequestNotifyMailList());
        }
    }

    public void updateOrgItems() {
        orgItems = getInstance().findOrgItems();
    }

    public BasicReportJob buildReport(Session persistenceSession, GoodRequestsNewReport.Builder builder, Date startDate,
            Date endDate, Calendar localCalendar) {
        BasicReportJob reportJob = null;
        try {
            reportJob = builder.build(persistenceSession, startDate, endDate, localCalendar);
        } catch (Exception e) {
            LOGGER.error("Failed export report : ", e);
        }
        return reportJob;
    }

    public static void addEmailFromClient(Session persistenceSession, Long idOfOrg, Set<String> addresses) {
        DetachedCriteria staffClientQuery = DetachedCriteria.forClass(Staff.class);
        staffClientQuery.add(Restrictions.eq("orgOwner", idOfOrg));
        staffClientQuery.add(Restrictions.eq("idOfRole", 0L));
        staffClientQuery.setProjection(Property.forName("idOfClient"));
        Criteria subCriteria = staffClientQuery.getExecutableCriteria(persistenceSession);
        Integer countResult = subCriteria.list().size();
        if (countResult > 0) {
            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Property.forName("idOfClient").in(staffClientQuery));
            clientCriteria.setProjection(Property.forName("email"));
            List<String> address = clientCriteria.list();
            for (String addres : address) {
                if (StringUtils.isNotEmpty(addres)) {
                    for (String email : StringUtils.split(addres, ";")) {
                        addresses.add(email.trim());
                    }
                }
            }
            //addresses.addAll(address);
        }
    }

    public static void addEmailFromUser(Session persistenceSession, Long idOfOrg, Set<String> addresses) {
        Criteria criteria = persistenceSession.createCriteria(UserOrgs.class);
        criteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
        criteria.add(Restrictions.eq("userNotificationType", UserNotificationType.GOOD_REQUEST_CHANGE_NOTIFY));
        List list = criteria.list();
        for (Object o : list) {
            UserOrgs userOrgs = (UserOrgs) o;
            if (userOrgs.getUser() != null && StringUtils.isNotEmpty(userOrgs.getUser().getEmail())) {
                for (String email : StringUtils.split(userOrgs.getUser().getEmail(), ";")) {
                    addresses.add(email.trim());
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public Map<Long, ContragentItem> findContragentItems() {
        Map<Long, ContragentItem> items = new HashMap<Long, ContragentItem>();
        Query query = entityManager
                .createQuery("select c.idOfContragent, c.requestNotifyMailList from Contragent c where c.classId=2");
        List res = query.getResultList();
        for (Object obj : res) {
            Object[] row = (Object[]) obj;
            String requestNotifyMailList = row[1] == null ? "" : row[1].toString();
            Long idOfContragent = Long.valueOf(row[0].toString());
            if (!items.containsKey(idOfContragent)) {
                items.put(idOfContragent, new ContragentItem(idOfContragent, requestNotifyMailList));
            }
        }
        return items;
    }

    @Transactional(readOnly = true)
    public Map<Long, OrgItem> findOrgItems() {
        Map<Long, OrgItem> items = new HashMap<Long, OrgItem>();
        Query query = entityManager.createQuery(
                "select o.idOfOrg, o.shortName, o.officialName, o.defaultSupplier.id, o.address, sm.idOfOrg from Org o join o.sourceMenuOrgs sm");
        List res = query.getResultList();
        for (Object obj : res) {
            Object[] row = (Object[]) obj;
            Long idOfOrg = Long.valueOf(row[0].toString());
            Long idOfSourceMenu = null;
            try {
                idOfSourceMenu = Long.valueOf(row[5].toString());
            } catch (Exception e) {
                LOGGER.error(String.format("Organization (idOfOrg=%d) has not source menu organization", idOfOrg));
            }
            if (!items.containsKey(idOfOrg)) {
                Long idOfContragent = Long.valueOf(row[3].toString());
                if (contragentItems.containsKey(idOfContragent)) {
                    ContragentItem contragentItem = contragentItems.get(idOfContragent);
                    items.put(idOfOrg, new OrgItem(idOfOrg, row[1].toString(), row[2].toString(), contragentItem,
                            row[4].toString(), idOfSourceMenu));
                }
            }
        }
        return items;
    }

    @Transactional(readOnly = true)
    public Map<Long, OrgItem> findOrgItems2(boolean onlyWithPreorders, PreorderRequestsReportServiceParam params) {
        updateContragentItems();
        Map<Long, OrgItem> items = new HashMap<Long, OrgItem>();
        String str_query = "select o.idOfOrg, o.shortName, o.officialName, o.defaultSupplier.id, o.address, sm.idOfOrg from Org o join o.sourceMenuOrgs sm";
        if (onlyWithPreorders) str_query += " where o.preordersEnabled = true";
        str_query += params.getOrgJPACondition("o");
        str_query += " order by o.idOfOrg";
        Query query = entityManager.createQuery(str_query);
        List res = query.getResultList();
        for (Object obj : res) {
            Object[] row = (Object[]) obj;
            Long idOfOrg = Long.valueOf(row[0].toString());
            Long idOfSourceMenu = null;
            try {
                idOfSourceMenu = Long.valueOf(row[5].toString());
            } catch (Exception e) {
                LOGGER.error(String.format("Organization (idOfOrg=%d) has not source menu organization", idOfOrg));
            }
            if (!items.containsKey(idOfOrg)) {
                Long idOfContragent = Long.valueOf(row[3].toString());
                if (contragentItems.containsKey(idOfContragent)) {
                    ContragentItem contragentItem = contragentItems.get(idOfContragent);
                    items.put(idOfOrg, new OrgItem(idOfOrg, row[1].toString(), row[2].toString(), contragentItem,
                            row[4].toString(), idOfSourceMenu));
                }
            }
        }
        return items;
    }


    public List<Date> getProductionCalendarDates(Date date) {
        return entityManager.createQuery("select pc.day from ProductionCalendar pc where pc.day between :date1 and :date2")
                .setParameter("date1", date)
                .setParameter("date2", CalendarUtils.addDays(date, 30))
                .getResultList();
    }

    public List<OrgGoodRequest> getDoneOrgGoodRequests(Date date, PreorderRequestsReportServiceParam params) {
        return entityManager.createQuery("select ogr from OrgGoodRequest ogr where ogr.day between :startDate and :endDate "
                + params.getOrgJPACondition("ogr")
                + "order by ogr.day")
        .setParameter("startDate", CalendarUtils.endOfDay(new Date()))
        .setParameter("endDate", date)
        .getResultList();
    }

    protected static class OrgItem {

        private long idOfOrg;
        private String shortName;
        private String officialName;
        private ContragentItem defaultSupplier;
        private Long idOfSourceMenu;
        private String address;

        public OrgItem(long idOfOrg, String shortName, String officialName, ContragentItem defaultSupplier,
                String address, Long idOfSourceMenu) {
            this.idOfOrg = idOfOrg;
            this.shortName = shortName;
            this.officialName = officialName;
            this.defaultSupplier = defaultSupplier;
            this.address = address;
            this.idOfSourceMenu = idOfSourceMenu;
        }

        public long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public String getShortName() {
            return shortName;
        }

        public void setShortName(String shortName) {
            this.shortName = shortName;
        }

        public String getOfficialName() {
            return officialName;
        }

        public void setOfficialName(String officialName) {
            this.officialName = officialName;
        }

        public ContragentItem getDefaultSupplier() {
            return defaultSupplier;
        }

        public void setDefaultSupplier(ContragentItem defaultSupplier) {
            this.defaultSupplier = defaultSupplier;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Long getIdOfSourceMenu() {
            return idOfSourceMenu;
        }

        public void setIdOfSourceMenu(Long idOfSourceMenu) {
            this.idOfSourceMenu = idOfSourceMenu;
        }
    }

    protected static class ContragentItem {

        private long idOfContragent;
        private String requestNotifyMailList;

        public ContragentItem(long idOfContragent, String requestNotifyMailList) {
            this.idOfContragent = idOfContragent;
            this.requestNotifyMailList = requestNotifyMailList;
        }

        public long getIdOfContragent() {
            return idOfContragent;
        }

        public void setIdOfContragent(long idOfContragent) {
            this.idOfContragent = idOfContragent;
        }

        public String getRequestNotifyMailList() {
            return requestNotifyMailList;
        }

        public void setRequestNotifyMailList(String requestNotifyMailList) {
            this.requestNotifyMailList = requestNotifyMailList;
        }
    }

}
