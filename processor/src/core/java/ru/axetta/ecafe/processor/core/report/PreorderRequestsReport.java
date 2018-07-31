/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static ru.axetta.ecafe.processor.core.service.PreorderRequestsReportService.PREORDER_COMMENT;

public class PreorderRequestsReport extends BasicReportForContragentJob {
    /*
     * Параметры отчета для добавления в правила и шаблоны
     *
     * При создании любого отчета необходимо добавить параметры:
     * REPORT_NAME - название отчета на русском
     * TEMPLATE_FILE_NAMES - названия всех jasper-файлов, созданных для отчета
     * IS_TEMPLATE_REPORT - добавлять ли отчет в шаблоны отчетов
     * PARAM_HINTS - параметры отчета (смотри ReportRuleConstants.PARAM_HINTS)
     * заполняется, если отчет добавлен в шаблоны (класс AutoReportGenerator)
     *
     * Затем КАЖДЫЙ класс отчета добавляется в массив ReportRuleConstants.ALL_REPORT_CLASSES
     */
    public static final String REPORT_NAME = "Сводный отчет по заявкам(предзаказы)";
    public static final String[] TEMPLATE_FILE_NAMES = {"PreordersRequestsReport_notify.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};

    final private static String OVERALL = "";
    final private static String OVERALL_TITLE = "ИТОГО";

    final public static String P_GUID_FILTER = "guidFilter";

    final private static Logger logger = LoggerFactory.getLogger(GoodRequestsNewReport.class);

    public static class Builder extends BasicReportForContragentJob.Builder {

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("startDate", CalendarUtils.dateShortToStringFullYear(startTime));
            parameterMap.put("endDate", CalendarUtils.dateShortToStringFullYear(endTime));
            parameterMap.put("reportName", REPORT_NAME);

            boolean hideMissedColumns = Boolean.parseBoolean(reportProperties.getProperty(GoodRequestsNewReport.P_HIDE_MISSED_COLUMNS, "false"));
            boolean hideGeneratePeriod = Boolean.parseBoolean(reportProperties.getProperty(GoodRequestsNewReport.P_HIDE_GENERATE_PERIOD, "false"));
            String hideDailySampleProperty = reportProperties.getProperty(GoodRequestsNewReport.P_HIDE_DAILY_SAMPLE_COUNT, "false");
            final int hideDailySampleValue = Boolean.parseBoolean(hideDailySampleProperty)?0:1;

            String hideLastValueProperty = reportProperties.getProperty(GoodRequestsNewReport.P_HIDE_LAST_VALUE, "false");
            final int hideLastValue = Boolean.parseBoolean(hideLastValueProperty)?0:1;

            String defaultGenerateTime = Long.toString(System.currentTimeMillis());
            long generateBeginDate = Long.parseLong(reportProperties.getProperty(GoodRequestsNewReport.P_GENERATE_BEGIN_DATE, defaultGenerateTime));
            Date generateBeginTime = new Date(generateBeginDate);
            // на час
            long generateEndDate = Long.parseLong(reportProperties.getProperty(GoodRequestsNewReport.P_GENERATE_END_DATE, Long.toString(System.currentTimeMillis()+60*60*1000)));
            Date generateEndTime = new Date(generateEndDate);

            long lastCreateOrUpdateDate = Long.parseLong(reportProperties.getProperty(GoodRequestsNewReport.P_LAST_CREATE_OR_UPDATE_DATE, defaultGenerateTime));
            Date lastCreateOrUpdateDateTime = new Date(lastCreateOrUpdateDate);

            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy EE HH:mm:ss", new Locale("ru"));
            parameterMap.put(GoodRequestsNewReport.P_GENERATE_END_DATE, format.format(lastCreateOrUpdateDateTime));

            String idOfOrgs = StringUtils.trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
            List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
            List<Long> idOfOrgList = new ArrayList<Long>(stringOrgList.size());
            for (String idOfOrg : stringOrgList) {
                idOfOrgList.add(Long.parseLong(idOfOrg));
            }

            String idOfMenuSourceOrgs = StringUtils.trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_MENU_SOURCE_ORG));
            List<String> idOfMenuSourceOrgStrList = Arrays.asList(StringUtils.split(idOfMenuSourceOrgs, ','));
            Set<Long> idOfMenuSourceOrgList = new HashSet();
            for (String idOfMenuSourceOrg : idOfMenuSourceOrgStrList) {
                idOfMenuSourceOrgList.add(Long.parseLong(idOfMenuSourceOrg));
            }

            String guidFilterString = reportProperties.getProperty(PreorderRequestsReport.P_GUID_FILTER, "");
            String[] guidFilterArray = StringUtils.split(guidFilterString, ",");
            List<String> guidFilter = new ArrayList<String>(Arrays.asList(guidFilterArray));

            JRDataSource dataSource = createDataSource(session, startTime, endTime, hideDailySampleValue, generateEndTime,
                    idOfOrgList, new ArrayList<Long>(idOfMenuSourceOrgList), hideMissedColumns, hideGeneratePeriod, hideLastValue,
                    guidFilter);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
            Date genEndTime = new Date();
            long generateDuration = genEndTime.getTime() - generateTime.getTime();
            return new PreordersReport(generateTime, generateDuration, jasperPrint, startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, int hideDailySampleValue,
                Date generateEndTime, List<Long> idOfOrgList, List<Long> idOfMenuSourceOrgList, boolean hideMissedColumns,
                boolean hideGeneratePeriod, int hideLastValue, List<String> guidFilter) {

            HashMap<Long, BasicReportJob.OrgShortItem> orgMap = getDefinedOrgs(session, idOfOrgList, idOfMenuSourceOrgList);
            List<Item> itemList = new LinkedList<Item>();

            Date beginDate = CalendarUtils.truncateToDayOfMonth(startTime);
            Date endDate = CalendarUtils.endOfDay(endTime);
            TreeSet<Date> dates = new TreeSet<Date>();

            String sqlQuery =
                    "SELECT distinct ci.idoforg, "
                  + "   CASE WHEN (pc.amount = 0) THEN md.idofgood ELSE ci.idofgood END AS idofgood, "
                  + "   CASE WHEN (pc.amount = 0) THEN false ELSE true END AS iscomplex "
                  + "FROM cf_preorder_complex pc "
                  + "INNER JOIN cf_clients c ON c.idofclient = pc.idofclient "
                  + "INNER JOIN cf_complexinfo ci ON c.idoforg = ci.idoforg AND ci.menudate = pc.preorderdate "
                  + "   AND ci.idofcomplex = pc.armcomplexid "
                  + "LEFT JOIN cf_preorder_menudetail pmd ON pc.idofpreordercomplex = pmd.idofpreordercomplex "
                  + "LEFT JOIN cf_menu m ON c.idoforg = m.idoforg AND pmd.preorderdate = m.menudate "
                  + "LEFT JOIN cf_menudetails md ON m.idofmenu = md.idofmenu AND pmd.armidofmenu = md.localidofmenu "
                  + "WHERE ci.idOfOrg IN (:orgList) AND ci.menuDate BETWEEN :startDate AND :endDate and pc.deletedState = 0 and pmd.deletedState = 0";

            Query query = session.createSQLQuery(sqlQuery);
            query.setParameterList("orgList", orgMap.keySet());
            query.setParameter("startDate", beginDate.getTime());
            query.setParameter("endDate", endDate.getTime());

            List complexList = query.list();

            Map<Long, ComplexInfoItem> complexOrgDictionary = new HashMap<Long, ComplexInfoItem>();
            Map<Long, GoodInfo> allGoodsInfo = new HashMap<Long, GoodInfo>();
            for (Object obj : complexList) {
                Object values[] = (Object[])obj;

                //globalId
                if (null == values[1])
                    continue;

                GoodType goodType;
                final Long idOfOrg = ((BigInteger) values[0]).longValue();
                final Long globalId = ((BigInteger) values[1]).longValue();
                Boolean isComplex = (Boolean) values[2];

                if (isComplex) {
                    goodType = GoodType.COMPLEX;
                } else {
                    goodType = GoodType.DISH;
                }

                ComplexInfoItem infoItem = complexOrgDictionary.get(idOfOrg);
                GoodInfo info = new GoodInfo(globalId, "", goodType);
                if (infoItem == null) {
                    infoItem = new ComplexInfoItem(idOfOrg);
                }
                infoItem.goodInfos.put(globalId, info);
                complexOrgDictionary.put(idOfOrg, infoItem);
                allGoodsInfo.put(globalId, info);
            }

            List<GoodRequestPosition> goodRequestPositionList = getGoodRequestPositions(session, guidFilter, generateEndTime,
                    hideGeneratePeriod, orgMap, beginDate, endDate);

            Map<Long, GoodInfo> requestGoodsInfo = new HashMap<Long, GoodInfo>();
            for (Object obj : goodRequestPositionList) {
                processPosition(session, hideDailySampleValue, hideMissedColumns, hideLastValue, orgMap, itemList, beginDate,
                        endDate, dates, complexOrgDictionary, goodRequestPositionList, requestGoodsInfo, obj);
            }

            if (!idOfMenuSourceOrgList.isEmpty()) {
                for (BasicReportJob.OrgShortItem item : orgMap.values()) {
                    if (item.getSourceMenuOrg() != null) {
                        MultiValueMap fullNameProviderMap = new MultiValueMap();
                        Criteria goodCriteria = session.createCriteria(Good.class);
                        goodCriteria.add(Restrictions.in("orgOwner", idOfMenuSourceOrgList));
                        goodCriteria.setProjection(Projections.projectionList().add(Projections.property("fullName"))
                                .add(Projections.property("nameOfGood")).add(Projections.property("orgOwner"))
                                .add(Projections.property("globalId")));
                        List goodNames = goodCriteria.list();
                        for (Object obj : goodNames) {
                            Object[] row = (Object[]) obj;
                            final Long idOfOrg = Long.valueOf(row[2].toString());
                            final Long idOfGood = Long.valueOf(row[3].toString());
                            String nameOfGood = row[0].toString();
                            if (StringUtils.isEmpty(nameOfGood)) {
                                nameOfGood = row[1].toString();
                            }
                            if (!requestGoodsInfo.containsKey(idOfGood)) {
                                GoodType goodType = null;
                                if (allGoodsInfo.containsKey(idOfGood)) {
                                    goodType = allGoodsInfo.get(idOfGood).goodType;
                                } else {
                                    continue;
                                }
                                fullNameProviderMap.put(idOfOrg, new GoodInfo(idOfGood, nameOfGood, goodType));
                            }
                        }
                        if (fullNameProviderMap.getCollection(item.getSourceMenuOrg()) != null) {
                            for (Object object : fullNameProviderMap.getCollection(item.getSourceMenuOrg())) {
                                GoodInfo goodInfo = (GoodInfo) object;
                                if (hideMissedColumns) {
                                    for (Date date : dates) {
                                        addItemsFromList(itemList, item, date, goodInfo.name, hideDailySampleValue,
                                                hideLastValue, goodInfo.goodType, 0L);
                                    }
                                } else {
                                    beginDate = CalendarUtils.truncateToDayOfMonth(startTime);
                                    endDate = CalendarUtils.endOfDay(endTime);
                                    while (beginDate.getTime() <= endDate.getTime()) {
                                        addItemsFromList(itemList, item, beginDate, goodInfo.name, hideDailySampleValue,
                                                hideLastValue, goodInfo.goodType, 0L);
                                        beginDate = CalendarUtils.addOneDay(beginDate);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (itemList.isEmpty()) {
                for (BasicReportJob.OrgShortItem item : orgMap.values()) {
                    itemList.add(new Item(item, "", CalendarUtils.truncateToDayOfMonth(startTime), hideDailySampleValue,
                            hideLastValue, null, 0L));
                }
                itemList.add(new Item(OVERALL, OVERALL_TITLE, "", CalendarUtils.truncateToDayOfMonth(startTime),
                        hideDailySampleValue, hideLastValue, null, 0L));
            }

            return new JRBeanCollectionDataSource(itemList);
        }

        private List<GoodRequestPosition> getGoodRequestPositions(Session session, List<String> guidFilter, Date generateEndTime,
                boolean hideGeneratePeriod, HashMap<Long, BasicReportJob.OrgShortItem> orgMap,
                Date beginDate, Date endDate) {
            Criteria criteria = session.createCriteria(GoodRequestPosition.class);
            criteria.createAlias("goodRequest", "gr");
            criteria.add(Restrictions.between("gr.doneDate", beginDate, endDate));
            criteria.add(Restrictions.in("gr.orgOwner", orgMap.keySet()));
            criteria.add(Restrictions.isNotNull("good"));
            criteria.add(Restrictions.eq("gr.comment", PREORDER_COMMENT));

            criteria.add(Restrictions.or(Restrictions.eq("notified", false), Restrictions.isNull("notified")));

            if (hideGeneratePeriod) {
                Disjunction dateDisjunction = Restrictions.disjunction();
                dateDisjunction.add(Restrictions.le("createdDate", generateEndTime));
                dateDisjunction.add(Restrictions.le("lastUpdate", generateEndTime));
                criteria.add(dateDisjunction);
            }
            if (!guidFilter.isEmpty()) {
                criteria.add(Restrictions.in("guid", guidFilter));
            }
            List<GoodRequestPosition> list = criteria.list();
            return list == null ? new ArrayList<GoodRequestPosition>() : list;
        }

        private Long getSafeValue(Long number) {
            return number != null ? number : 0L;
        }

        private void processPosition(Session session, int hideDailySampleValue, boolean hideMissedColumns, int hideLastValue,
                HashMap<Long, BasicReportJob.OrgShortItem> orgMap, List<Item> itemList, Date beginDate, Date endDate,
                TreeSet<Date> dates, Map<Long, ComplexInfoItem> complexOrgDictionary, List goodRequestPositionList,
                Map<Long, GoodInfo> requestGoodsInfo, Object obj) {

            Date doneDate;
            GoodRequestPosition position = (GoodRequestPosition) obj;
            BasicReportJob.OrgShortItem org = orgMap.get(position.getOrgOwner());

            Long totalCount = position.getTotalCount() / 1000;
            Long dailySampleCount = getSafeValue(position.getDailySampleCount()) / 1000L;
            Long tempClientsCount = getSafeValue(position.getTempClientsCount()) / 1000L;

            Long newTotalCount = totalCount - getSafeValue(position.getLastTotalCount()) / 1000L;
            Long newDailySample = dailySampleCount - getSafeValue(position.getLastDailySampleCount()) / 1000L;
            Long newTempClients = tempClientsCount - getSafeValue(position.getLastTempClientsCount()) / 1000L;

            Long notificationMark = 0L;
            if (position.getDeletedState()) {
                totalCount = 0L;
                notificationMark = 1L;
            }

            doneDate = CalendarUtils.truncateToDayOfMonth(position.getGoodRequest().getDoneDate());

            final Good good = position.getGood();
            GoodType goodType = null;
            if (complexOrgDictionary.containsKey(position.getOrgOwner())) {
                if (complexOrgDictionary.get(position.getOrgOwner()).goodInfos.containsKey(good.getGlobalId())) {
                    goodType = complexOrgDictionary.get(position.getOrgOwner()).goodInfos
                            .get(good.getGlobalId()).goodType;
                }
            }
            String name = good.getFullName();
            if (StringUtils.isEmpty(name)) {
                name = good.getNameOfGood();
            }
            if (!requestGoodsInfo.containsKey(good.getGlobalId())) {
                requestGoodsInfo.put(good.getGlobalId(), new GoodInfo(good.getGlobalId(), name, goodType));
            }
            // чтобы хотя бы раз выполнилмся, для уведомлений
            if (!hideMissedColumns && goodRequestPositionList.indexOf(obj) == 0) {
                while (beginDate.getTime() <= endDate.getTime()) {
                    itemList.add(new Item(org, name, beginDate, 0L, 0L, 0L, 0L,
                            0L, 0L, hideDailySampleValue, hideLastValue, goodType, 0L));
                    dates.add(beginDate);
                    beginDate = CalendarUtils.addOneDay(beginDate);
                }
            }

            addItemsFromList(itemList, org, doneDate, name, totalCount, dailySampleCount, tempClientsCount, newTotalCount, newDailySample, newTempClients,
                    hideDailySampleValue, hideLastValue, goodType, notificationMark);
            dates.add(doneDate);

            position.setNotified(true);
            session.persist(position);
        }

        private HashMap<Long, BasicReportJob.OrgShortItem> getDefinedOrgs(Session session, List<Long> idOfOrgList,
                List<Long> idOfMenuSourceOrgList) {
            Criteria orgCriteria = session.createCriteria(Org.class);
            if (!CollectionUtils.isEmpty(idOfOrgList)) {
                orgCriteria.add(Restrictions.in("idOfOrg", idOfOrgList));
            }
            orgCriteria.createAlias("sourceMenuOrgs", "sm", JoinType.LEFT_OUTER_JOIN);
            if (!CollectionUtils.isEmpty(idOfMenuSourceOrgList)) {
                orgCriteria.add(Restrictions.in("sm.idOfOrg", idOfMenuSourceOrgList));
            }
            orgCriteria.setProjection(
                    Projections.projectionList().add(Projections.property("idOfOrg")).add(Projections.property("shortName"))
                            .add(Projections.property("officialName")).add(Projections.property("sm.idOfOrg")));
            List orgList = orgCriteria.list();
            HashMap<Long, BasicReportJob.OrgShortItem> orgMap = new HashMap<Long, BasicReportJob.OrgShortItem>(
                    orgList.size());
            for (Object obj : orgList) {
                Object[] row = (Object[]) obj;
                long idOfOrg = Long.parseLong(row[0].toString());
                BasicReportJob.OrgShortItem educationItem;
                educationItem = new BasicReportJob.OrgShortItem(idOfOrg, row[1].toString(), row[2].toString());
                if (row[3] != null) {
                    Long sourceMenuOrg = Long.parseLong(row[3].toString());
                    educationItem.setSourceMenuOrg(sourceMenuOrg);
                    if (!idOfMenuSourceOrgList.contains(sourceMenuOrg)) idOfMenuSourceOrgList.add(sourceMenuOrg);
                }
                orgMap.put(idOfOrg, educationItem);
            }
            return orgMap;
        }

        private void addItemsFromList(List<Item> itemList, BasicReportJob.OrgShortItem org, Date doneDate, String name,
                int hideDailySampleValue, int hideLastValue, GoodType goodType, Long notificationMark) {
            itemList.add(
                    new Item(org, name, doneDate, hideDailySampleValue, hideLastValue, goodType, notificationMark));
            itemList.add(new Item(OVERALL, OVERALL_TITLE, name, doneDate, hideDailySampleValue, hideLastValue,
                    goodType, notificationMark));
        }

        private void addItemsFromList(List<Item> itemList, BasicReportJob.OrgShortItem org, Date doneDate, String name,
                Long totalCount, Long dailySampleCount, Long tempClientsCount, Long newTotalCount, Long newDailySample,
                Long newTempClients, int hideDailySampleValue, int hideLastValue, GoodType goodType, Long notificationMark) {
            itemList.add(new Item(org, name, doneDate, totalCount, dailySampleCount, tempClientsCount, newTotalCount,
                    newDailySample, newTempClients, hideDailySampleValue, hideLastValue, goodType, notificationMark));
        }
    }

    public PreorderRequestsReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfContragent) {
        super(generateTime, generateDuration, print, startTime, endTime, idOfContragent);
    }

    public PreorderRequestsReport() {
    }

    @Override
    public BasicReportForContragentJob createInstance() {
        return new PreorderRequestsReport();
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    private static class ComplexInfoItem {

        final long idOfOrg;
        final Map<Long, GoodInfo> goodInfos = new HashMap<Long, GoodInfo>();

        private ComplexInfoItem(long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

    }

    private enum GoodType {
        /*0*/ COMPLEX("Вариативное платное питание. Комплексы"),
        /*1*/ DISH("Вариативное платное питание. Отдельные блюда");

        private String name;

        GoodType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private static class GoodInfo {

        final long idOfGood;
        final String name;
        final GoodType goodType;

        private GoodInfo(long idOfGood, String name, GoodType goodType) {
            this.idOfGood = idOfGood;
            this.name = name;
            this.goodType = goodType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            GoodInfo goodInfo = (GoodInfo) o;

            if (idOfGood != goodInfo.idOfGood) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return (int) (idOfGood ^ (idOfGood >>> 32));
        }
    }

    public static class Item implements Comparable {

        final private static String STR_YEAR_DATE_FORMAT = "EE dd.MM";
        final private static DateFormat YEAR_DATE_FORMAT = new SimpleDateFormat(STR_YEAR_DATE_FORMAT, new Locale("ru"));
        private String orgNum;
        private String officialName;
        private String goodName;
        private Date doneDate;
        private String doneDateStr;
        private int hideDailySample = 0;
        private int hideLastValue = 0;
        private GoodType goodType;
        private String goodTypeStr;
        private Integer goodTypeNum;
        private Long totalCount;
        private Long dailySample;
        private Long tempClients;
        private Long newTotalCount;
        private Long newDailySample;
        private Long newTempClients;
        private Long notificationMark;

        protected Item(Item item, Date doneDate) {
            this(item.getOrgNum(), item.getOfficialName(), item.getGoodName(), doneDate, 0L, 0L,
                    0L, 0L, 0L, 0L, item.getHideDailySample(),
                    item.getHideLastValue(), item.getGoodType(), item.getNotificationMark());
        }

        public Item(String orgNum, String officialName, String goodName, Date doneDate, Long totalCount,
                Long dailySample, Long tempClients, Long newTotalCount, Long newDailySample, Long newTempClients,
                int hideDailySampleValue, int hideLastValue, GoodType goodType, Long notificationMark) {
            this.orgNum = orgNum;
            this.officialName = officialName;
            this.goodName = goodName;
            this.doneDate = doneDate;
            doneDateStr = YEAR_DATE_FORMAT.format(doneDate);
            this.totalCount = totalCount;
            this.dailySample = dailySample;
            this.tempClients = tempClients;
            this.newTotalCount = newTotalCount;
            this.newDailySample = newDailySample;
            this.newTempClients = newTempClients;
            this.hideDailySample = hideDailySampleValue;
            this.hideLastValue = hideLastValue;
            this.goodType = goodType;
            if (goodType == null) {
                goodTypeStr = "";
                goodTypeNum = -1;
            } else {
                goodTypeStr = goodType.getName();
                goodTypeNum = goodType.ordinal();
            }
            this.notificationMark = notificationMark;

        }

        public Item(String orgNum, String officialName, String goodName, Date doneDate, int hideDailySampleValue,
                int hideLastValue, GoodType goodType, Long notificationMark) {
            this(orgNum, officialName, goodName, doneDate, 0L, 0L, 0L, 0L,
                    0L, 0L, hideDailySampleValue, hideLastValue, goodType, notificationMark);
        }

        public Item(BasicReportJob.OrgShortItem item, String goodName, Date doneDate, Long totalCount, Long dailySample, Long tempClients,
                Long newTotalCount, Long newDailySample, Long newTempClients, int hideDailySampleValue, int hideLastValue,
                GoodType goodType, Long notificationMark) {
            this(Org.extractOrgNumberFromName(item.getOfficialName()), item.getShortName(), goodName, doneDate, totalCount,
                    dailySample, tempClients, newTotalCount, newDailySample, newTempClients, hideDailySampleValue, hideLastValue,
                    goodType, notificationMark);

        }

        public Item(BasicReportJob.OrgShortItem item, String goodName, Date doneDate, int hideDailySampleValue,
                int hideLastValue, GoodType goodType, Long notificationMark) {
            this(item, goodName, doneDate, 0L, 0L, 0L, 0L, 0L,
                    0L, hideDailySampleValue, hideLastValue, goodType, notificationMark);
        }

        @Override
        public int compareTo(Object o) {
            return Integer.valueOf(hashCode()).compareTo(o.hashCode());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Item item = (Item) o;

            return doneDate.equals(item.doneDate) && goodName.equals(item.goodName) && officialName
                    .equals(item.officialName);

        }

        @Override
        public int hashCode() {
            int result = officialName.hashCode();
            result = 31 * result + goodName.hashCode();
            result = 31 * result + doneDate.hashCode();
            return result;
        }

        public String getOrgNum() {
            return orgNum;
        }

        public void setOrgNum(String orgNum) {
            this.orgNum = orgNum;
        }

        public String getOfficialName() {
            return officialName;
        }

        public void setOfficialName(String officialName) {
            this.officialName = officialName;
        }

        public String getGoodName() {
            return goodName;
        }

        public void setGoodName(String goodName) {
            this.goodName = goodName;
        }

        public Date getDoneDate() {
            return doneDate;
        }

        public void setDoneDate(Date doneDate) {
            this.doneDate = doneDate;
        }

        public String getDoneDateStr() {
            return doneDateStr;
        }

        public void setDoneDateStr(String doneDateStr) {
            this.doneDateStr = doneDateStr;
        }

        public Long getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(Long totalCount) {
            this.totalCount = totalCount;
        }

        public Long getDailySample() {
            return dailySample;
        }

        public void setDailySample(Long dailySample) {
            this.dailySample = dailySample;
        }

        public Long getNewTotalCount() {
            return newTotalCount;
        }

        public void setNewTotalCount(Long newTotalCount) {
            this.newTotalCount = newTotalCount;
        }

        public Long getNewDailySample() {
            return newDailySample;
        }

        public void setNewDailySample(Long newDailySample) {
            this.newDailySample = newDailySample;
        }

        public int getHideDailySample() {
            return hideDailySample;
        }

        public void setHideDailySample(int hideDailySample) {
            this.hideDailySample = hideDailySample;
        }

        public int getHideLastValue() {
            return hideLastValue;
        }

        public void setHideLastValue(int hideLastValue) {
            this.hideLastValue = hideLastValue;
        }

        public GoodType getGoodType() {
            return goodType;
        }

        public void setGoodType(GoodType goodType) {
            this.goodType = goodType;
        }

        public String getGoodTypeStr() {
            return goodTypeStr;
        }

        public void setGoodTypeStr(String goodTypeStr) {
            this.goodTypeStr = goodTypeStr;
        }

        public Integer getGoodTypeNum() {
            return goodTypeNum;
        }

        public void setGoodTypeNum(Integer goodTypeNum) {
            this.goodTypeNum = goodTypeNum;
        }

        public Long getNotificationMark() {
            return notificationMark;
        }

        public void setNotificationMark(Long notificationMark) {
            this.notificationMark = notificationMark;
        }

        public Long getTempClients() {
            return tempClients;
        }

        public void setTempClients(Long tempClients) {
            this.tempClients = tempClients;
        }

        public Long getNewTempClients() {
            return newTempClients;
        }

        public void setNewTempClients(Long newTempClients) {
            this.newTempClients = newTempClients;
        }
    }
}
