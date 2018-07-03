/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ConsumerRequestDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOVersion;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DocumentState;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequest;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.Staff;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

@Component("PreorderRequestsReportService")
@Scope("singleton")
public class PreorderRequestsReportService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PreorderRequestsReportService.class);

    public final String CRON_EXPRESSION_PROPERTY = "ecafe.processor.report.PreorderRequestsReport.cronExpression";
    public static final String NODE_PROPERTY = "ecafe.processor.report.PreorderRequestsReport.node";
    private static final String PREORDER_COMMENT = "- Добавлено из предзаказа -";
    private static final String TEMPLATE_FILENAME = "PreordersRequestsReport_notify.jasper";

    private Calendar localCalendar;
    private Date startDate;
    private Date endDate;

    private int maxNumDays;
    private Map<Long, GoodRequestsChangeAsyncNotificationService.OrgItem> orgItems = new HashMap<Long, GoodRequestsChangeAsyncNotificationService.OrgItem>();
    private boolean isHideMissedCol;

    public void run() throws Exception {
        if (!isOn())
            return;
        runTask();
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

    public void runTask() throws Exception {
        updateDate();

        orgItems = GoodRequestsChangeAsyncNotificationService.getInstance().findOrgItems();

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        maxNumDays = runtimeContext.getOptionValueInt(Option.OPTION_MAX_NUM_DAYS_NOTIFICATION_GOOD_REQUEST_CHANGE);
        isHideMissedCol = runtimeContext
                .getOptionValueBool(Option.OPTION_HIDE_MISSED_COL_NOTIFICATION_GOOD_REQUEST_CHANGE);

        HashMap<Long, List<String>> mapPositions = new HashMap<Long, List<String>>();
        Date fireTime = new Date();

        Session session = null;
        Transaction transaction = null;
        try {
            session = runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();

            List<PreorderItem> preorderItemList = loadPreorders(session);

            for (PreorderItem item : preorderItemList) {
                if (null == item.getIdOfGood()) {
                    logger.warn(String.format("PreorderRequestsReportService: preorder without good item was found (orgID = %s, createdDate = %s)",
                            item.getIdOfOrg(), item.getCreatedDate().toString()) );
                    continue;
                }

                String guid;
                try {
                    if (null == item.getIdOfGoodsRequestPosition()) {
                        guid = createRequestFromPreorder(session, item, fireTime);
                    } else {
                        guid = updateRequestFromPreorder(session, item, fireTime);
                    }
                } catch (Exception e) {
                    logger.warn("PreorderRequestsReportService: could not create GoodRequest");
                    continue;
                }

                if (null == guid)   // инфа в позиции не поменялась
                    continue;

                Long orgOwner = item.getIdOfOrg();
                if (!mapPositions.containsKey(orgOwner)) {
                    mapPositions.put(orgOwner, new ArrayList<String>());
                }
                mapPositions.get(orgOwner).add(guid);
            }
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }

        Calendar calendarEnd = RuntimeContext.getInstance().getDefaultLocalCalendar(null);
        final Date lastCreateOrUpdateDate = calendarEnd.getTime();
        calendarEnd.add(Calendar.MINUTE, 1);
        final Date endGenerateTime = calendarEnd.getTime();
        for (Long orgOwner : mapPositions.keySet()) {
            List<String> guids = mapPositions.get(orgOwner);
            notifyOrg(orgOwner, fireTime, endGenerateTime, lastCreateOrUpdateDate, guids);
        }
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
        properties.setProperty(GoodRequestsNewReport.P_NOTIFICATION, Boolean.toString(true));
        return properties;
    }

    private List<PreorderItem> loadPreorders(Session session) {
        List<PreorderItem> preorderItemList = new ArrayList<PreorderItem>();
        String sqlQuery =
                "SELECT ci.idoforg, pc.createddate, pc.idofpreordercomplex, pmd.idofpreordermenudetail, "
              + "   CASE WHEN (pc.amount = 0) THEN md.idofgood ELSE ci.idofgood END AS idofgood, "
              + "   CASE WHEN (pc.amount = 0) THEN pmd.amount ELSE pc.amount END AS amount,"
              + "   CASE WHEN (pc.amount = 0) THEN pmd.idOfGoodsRequestPosition ELSE pc.idOfGoodsRequestPosition END AS idOfGoodsRequestPosition,"
              + "   pc.preorderdate "
              + "FROM cf_preorder_complex pc "
              + "INNER JOIN cf_clients c ON c.idofclient = pc.idofclient "
              + "INNER JOIN cf_complexinfo ci ON c.idoforg = ci.idoforg AND ci.menudate = pc.preorderdate "
              + "   AND ci.idofcomplex = pc.armcomplexid "
              + "LEFT JOIN cf_preorder_menudetail pmd ON pc.idofpreordercomplex = pmd.idofpreordercomplex "
              + "LEFT JOIN cf_menu m ON c.idoforg = m.idoforg AND pmd.preorderdate = m.menudate "
              + "LEFT JOIN cf_menudetails md ON m.idofmenu = md.idofmenu AND pmd.armidofmenu = md.localidofmenu "
              + "WHERE pc.lastupdate BETWEEN :startTime AND :endTime";
        Query query = session.createSQLQuery(sqlQuery);
        query.setParameter("startTime", startDate.getTime());
        query.setParameter("endTime", endDate.getTime());
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
            preorderItemList.add(new PreorderItem(idOfPreorderComplex, idOfPreorderMenuDetail, idOfOrg, idOfGood, amount,
                    createdDate, idOfGoodsRequest, preorderDate));
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
        goodRequest.setLastUpdate(fireTime);
        goodRequest.setComment(PREORDER_COMMENT);
        goodRequest.setRequestType(0);
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
        pos.setLastUpdate(fireTime);
        pos = save(session, pos, GoodRequestPosition.class.getSimpleName());

        if (null != preorderItem.getIdOfPreorderComplex()) {
            PreorderComplex complex = (PreorderComplex) session.get(PreorderComplex.class, preorderItem.getIdOfPreorderComplex());
            complex.setIdOfGoodsRequestPosition(pos.getGlobalId());
            session.update(complex);
        } else if (null != preorderItem.getIdOfPreorderMenuDetail()) {
            PreorderMenuDetail detail = (PreorderMenuDetail) session.get(PreorderMenuDetail.class,
                    preorderItem.getIdOfPreorderMenuDetail());
            detail.setIdOfGoodsRequestPosition(pos.getGlobalId());
            session.update(detail);
        }
        session.flush();
        return pos.getGuid();
    }

    private String updateRequestFromPreorder(Session session, PreorderItem item, Date fireTime) {
        GoodRequestPosition pos = (GoodRequestPosition) session.load(GoodRequestPosition.class, item.getIdOfGoodsRequestPosition());
        if (null == pos) {
            logger.error("PreorderRequestsReportService: could not find GoodRequestPosition with id=" + item.getIdOfGoodsRequestPosition().toString());
            return "";
        }
        Boolean isUpdated = false;

        if ((item.getAmount() * 1000L) != pos.getTotalCount()) {
            Long lastTotal = pos.getTotalCount();
            pos.setTotalCount(item.getAmount() * 1000L);
            pos.setLastTotalCount(lastTotal);
            isUpdated = true;
        }

        GoodRequest request = pos.getGoodRequest();

        if (!item.getPreorderDate().equals(request.getDoneDate())) {
            request.setDoneDate(item.getPreorderDate());
            request.setLastUpdate(fireTime);
            isUpdated = true;
        }

        if (isUpdated) {
            pos.setLastUpdate(fireTime);
            session.flush();
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

        public PreorderItem(Long idOfPreorderComplex, Long idOfPreorderMenuDetail, Long idOfOrg, Long idOfGood, Integer amount,
                Date createdDate, Long idOfGoodsRequestPosition, Date preorderDate) {
            this.idOfPreorderComplex = idOfPreorderComplex;
            this.idOfPreorderMenuDetail = idOfPreorderMenuDetail;
            this.idOfOrg = idOfOrg;
            this.idOfGood = idOfGood;
            this.amount = amount;
            this.createdDate = createdDate;
            this.idOfGoodsRequestPosition = idOfGoodsRequestPosition;
            this.preorderDate = preorderDate;
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
}

