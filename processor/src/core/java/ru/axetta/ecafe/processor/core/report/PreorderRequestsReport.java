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

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
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

            String idOfOrgString = StringUtils.trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
            Long idOfOrg = Long.parseLong(idOfOrgString);

            Org org = (Org) session.load(Org.class, idOfOrg);

            if (null != org) {
                parameterMap.put("address", org.getAddress());
                parameterMap.put("shortName", org.getOfficialName());
            } else {
                parameterMap.put("address", "не указан");
                parameterMap.put("shortName", "не указано");
            }
            parameterMap.put("idOfOrg", idOfOrg);

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
                    idOfOrg, new ArrayList<Long>(idOfMenuSourceOrgList), hideMissedColumns, hideGeneratePeriod, hideLastValue,
                    guidFilter);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
            Date genEndTime = new Date();
            long generateDuration = genEndTime.getTime() - generateTime.getTime();
            return new PreorderRequestsReport(generateTime, generateDuration, jasperPrint, startTime, endTime, idOfOrg);
        }

        public static List<GoodRequestsNewReportService.Item> loadAllItems(Session session, Date startTime, Date endTime, int hideDailySampleValue,
                Date generateEndTime, Long _idOfOrg, List<Long> idOfMenuSourceOrgList, boolean hideMissedColumns,
                boolean hideGeneratePeriod, int hideLastValue, List<String> guidFilter, boolean notify) {
            HashMap<Long, BasicReportJob.OrgShortItem> orgMap = getDefinedOrgs(session, _idOfOrg, idOfMenuSourceOrgList);
            List<GoodRequestsNewReportService.Item> itemList = new LinkedList<GoodRequestsNewReportService.Item>();

            Date beginDate = CalendarUtils.truncateToDayOfMonth(startTime);
            Date endDate = CalendarUtils.endOfDay(endTime);
            TreeSet<Date> dates = new TreeSet<Date>();

            String sqlQuery =
                    "SELECT distinct ci.idoforg, "
                            + "   CASE WHEN (pc.amount = 0) THEN md.idofgood ELSE ci.idofgood END AS idofgood, "
                            + "   CASE WHEN (pc.amount = 0) THEN false ELSE true END AS iscomplex, "
                            + "   CASE WHEN (pc.amount = 0) THEN gmd.goodscode ELSE gc.goodscode END AS goodscode "
                            + "FROM cf_preorder_complex pc "
                            + "INNER JOIN cf_clients c ON c.idofclient = pc.idofclient "
                            + "INNER JOIN cf_complexinfo ci ON c.idoforg = ci.idoforg AND ci.menudate = pc.preorderdate "
                            + "   AND ci.idofcomplex = pc.armcomplexid "
                            + "LEFT JOIN cf_preorder_menudetail pmd ON pc.idofpreordercomplex = pmd.idofpreordercomplex "
                            + "LEFT JOIN cf_menu m ON c.idoforg = m.idoforg AND pmd.preorderdate = m.menudate "
                            + "LEFT JOIN cf_menudetails md ON m.idofmenu = md.idofmenu AND pmd.armidofmenu = md.localidofmenu "
                            + "LEFT JOIN cf_goods gc ON gc.idofgood = ci.idofgood "
                            + "LEFT JOIN cf_goods gmd ON gmd.idofgood = md.idofgood "
                            + "WHERE ci.idOfOrg IN (:orgList) AND ci.menuDate BETWEEN :startDate AND :endDate ";
                            //+ "   AND (pc.deletedState = 0 OR pc.deletedState IS NULL) AND (pmd.deletedState = 0 OR pmd.deletedState IS NULL)";
            //and pc.deletedState = 0 and pmd.deletedState = 0";

            Query query = session.createSQLQuery(sqlQuery);
            query.setParameterList("orgList", orgMap.keySet());
            query.setParameter("startDate", beginDate.getTime());
            query.setParameter("endDate", endDate.getTime());

            List complexList = query.list();

            Map<Long, GoodRequestsNewReportService.ComplexInfoItem> complexOrgDictionary = new HashMap<Long, GoodRequestsNewReportService.ComplexInfoItem>();
            Map<Long, GoodRequestsNewReportService.GoodInfo> allGoodsInfo = new HashMap<Long, GoodRequestsNewReportService.GoodInfo>();
            for (Object obj : complexList) {
                Object values[] = (Object[])obj;

                //globalId
                if (null == values[1])
                    continue;

                GoodRequestsNewReportService.FeedingPlanType planType;
                final Long idOfOrg = ((BigInteger) values[0]).longValue();
                final Long globalId = ((BigInteger) values[1]).longValue();
                Boolean isComplex = (Boolean) values[2];
                final String goodsCode = (null != values[3]) ? values[3].toString() : "";

                if (isComplex) {
                    planType = GoodRequestsNewReportService.FeedingPlanType.COMPLEX;
                } else {
                    planType = GoodRequestsNewReportService.FeedingPlanType.DISH;
                }

                GoodRequestsNewReportService.ComplexInfoItem infoItem = complexOrgDictionary.get(idOfOrg);
                GoodRequestsNewReportService.GoodInfo info = new GoodRequestsNewReportService.GoodInfo(globalId, "", planType, goodsCode, null);
                if (infoItem == null) {
                    infoItem = new GoodRequestsNewReportService.ComplexInfoItem(idOfOrg);
                }
                infoItem.goodInfos.put(globalId, info);
                complexOrgDictionary.put(idOfOrg, infoItem);
                allGoodsInfo.put(globalId, info);
            }

            List<GoodRequestPosition> goodRequestPositionList = getGoodRequestPositions(session, guidFilter, generateEndTime,
                    hideGeneratePeriod, orgMap, beginDate, endDate);
            //adding old positions, which already notified
            if (!goodRequestPositionList.isEmpty())
                goodRequestPositionList.addAll(getGoodRequestPositions(session, new ArrayList<String>(), generateEndTime,
                    hideGeneratePeriod, orgMap, beginDate, endDate));

            Map<Long, GoodRequestsNewReportService.GoodInfo> requestGoodsInfo = new HashMap<Long, GoodRequestsNewReportService.GoodInfo>();
            for (Object obj : goodRequestPositionList) {
                processPosition(session, hideDailySampleValue, hideMissedColumns, hideLastValue, orgMap, itemList, beginDate,
                        endDate, dates, complexOrgDictionary, goodRequestPositionList, requestGoodsInfo, obj, notify);
            }

            if (itemList.isEmpty()) {
                for (BasicReportJob.OrgShortItem item : orgMap.values()) {
                    itemList.add(new GoodRequestsNewReportService.Item(item, "", CalendarUtils.truncateToDayOfMonth(startTime), hideDailySampleValue,
                            hideLastValue, null, 0L, "", null, null));
                }
                itemList.add(new GoodRequestsNewReportService.Item(OVERALL, OVERALL_TITLE, "", CalendarUtils.truncateToDayOfMonth(startTime),
                        hideDailySampleValue, hideLastValue, null, 0L, 0L, "", "", null, null));
            }
            return itemList;
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, int hideDailySampleValue,
                Date generateEndTime, Long idOfOrg, List<Long> idOfMenuSourceOrgList, boolean hideMissedColumns,
                boolean hideGeneratePeriod, int hideLastValue, List<String> guidFilter) {

            return new JRBeanCollectionDataSource(loadAllItems(session, startTime, endTime, hideDailySampleValue,
                    generateEndTime, idOfOrg, idOfMenuSourceOrgList, hideMissedColumns, hideGeneratePeriod,
                    hideLastValue, guidFilter, true));
        }

        private static List<GoodRequestPosition> getGoodRequestPositions(Session session, List<String> guidFilter, Date generateEndTime,
                boolean hideGeneratePeriod, HashMap<Long, BasicReportJob.OrgShortItem> orgMap,
                Date beginDate, Date endDate) {
            Criteria criteria = session.createCriteria(GoodRequestPosition.class);
            criteria.createAlias("goodRequest", "gr");
            criteria.add(Restrictions.between("gr.doneDate", beginDate, endDate));
            criteria.add(Restrictions.in("gr.orgOwner", orgMap.keySet()));
            criteria.add(Restrictions.isNotNull("good"));
            criteria.add(Restrictions.eq("deletedState", false));
            criteria.add(Restrictions.eq("gr.deletedState", false));
            criteria.add(Restrictions.eq("gr.comment", PREORDER_COMMENT));
            Junction countCondition = Restrictions.disjunction();
            countCondition.add(Restrictions.ne("totalCount", 0L))
                    .add(Restrictions.ne("dailySampleCount", 0L))
                    .add(Restrictions.ne("tempClientsCount", 0L));
            criteria.add(countCondition);

            if (!guidFilter.isEmpty()) {
                criteria.add(Restrictions.or(Restrictions.eq("notified", false), Restrictions.isNull("notified")));
            } else {
                criteria.add(Restrictions.eq("notified", true));
            }

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

        private static Long getSafeValue(Long number) {
            return number != null ? number : 0L;
        }

        private static void processPosition(Session session, int hideDailySampleValue, boolean hideMissedColumns, int hideLastValue,
                HashMap<Long, BasicReportJob.OrgShortItem> orgMap, List<GoodRequestsNewReportService.Item> itemList, Date beginDate, Date endDate,
                TreeSet<Date> dates, Map<Long, GoodRequestsNewReportService.ComplexInfoItem> complexOrgDictionary, List goodRequestPositionList,
                Map<Long, GoodRequestsNewReportService.GoodInfo> requestGoodsInfo, Object obj, boolean notify) {

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
            GoodRequestsNewReportService.FeedingPlanType planType = null;
            if (complexOrgDictionary.containsKey(position.getOrgOwner())) {
                if (complexOrgDictionary.get(position.getOrgOwner()).goodInfos.containsKey(good.getGlobalId())) {
                    planType = complexOrgDictionary.get(position.getOrgOwner()).goodInfos
                            .get(good.getGlobalId()).feedingPlanType;
                } else {
                    return;
                }
            } else {
                return;
            }
            String name = good.getFullName();
            if (StringUtils.isEmpty(name)) {
                name = good.getNameOfGood();
            }
            String goodsCode = good.getGoodsCode();
            if (!requestGoodsInfo.containsKey(good.getGlobalId())) {
                requestGoodsInfo.put(good.getGlobalId(), new GoodRequestsNewReportService.GoodInfo(good.getGlobalId(), name, planType, goodsCode, null));
            }
            // чтобы хотя бы раз выполнилмся, для уведомлений
            if (!hideMissedColumns && goodRequestPositionList.indexOf(obj) == 0) {
                while (beginDate.getTime() <= endDate.getTime()) {
                    itemList.add(new GoodRequestsNewReportService.Item(org, name, beginDate, 0L, 0L, 0L, 0L,
                            0L, 0L, hideDailySampleValue, hideLastValue, planType, 0L, 0L, goodsCode,
                            null, null));
                    dates.add(beginDate);
                    beginDate = CalendarUtils.addOneDay(beginDate);
                }
            }

            Long needToMark = 0L;
            if (notify && !position.getNotified())
                needToMark = 1L;

            addItemsFromList(itemList, org, doneDate, name, totalCount, dailySampleCount, tempClientsCount, newTotalCount, newDailySample, newTempClients,
                    hideDailySampleValue, hideLastValue, planType, notificationMark, needToMark, goodsCode);
            dates.add(doneDate);

            if (notify && !position.getNotified()) {
                position.setNotified(true);
                session.persist(position);
            }
        }

        private static HashMap<Long, BasicReportJob.OrgShortItem> getDefinedOrgs(Session session, Long idOfOrg,
                List<Long> idOfMenuSourceOrgList) {
            Criteria orgCriteria = session.createCriteria(Org.class);
            if (null != idOfOrg) {
                orgCriteria.add(Restrictions.eq("idOfOrg", idOfOrg));
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
                long _idOfOrg = Long.parseLong(row[0].toString());
                BasicReportJob.OrgShortItem educationItem;
                educationItem = new BasicReportJob.OrgShortItem(_idOfOrg, row[1].toString(), row[2].toString());
                if (row[3] != null) {
                    Long sourceMenuOrg = Long.parseLong(row[3].toString());
                    educationItem.setSourceMenuOrg(sourceMenuOrg);
                    if (!idOfMenuSourceOrgList.contains(sourceMenuOrg)) idOfMenuSourceOrgList.add(sourceMenuOrg);
                }
                orgMap.put(_idOfOrg, educationItem);
            }
            return orgMap;
        }

        private static void addItemsFromList(List<GoodRequestsNewReportService.Item> itemList, BasicReportJob.OrgShortItem org,
                Date doneDate, String name, int hideDailySampleValue, int hideLastValue, GoodRequestsNewReportService.FeedingPlanType goodType,
                Long notificationMark, Long needToMark, String goodsCode) {
            itemList.add(
                    new GoodRequestsNewReportService.Item(org, name, doneDate, hideDailySampleValue, hideLastValue, goodType,
                            notificationMark, goodsCode, null, null));
            itemList.add(new GoodRequestsNewReportService.Item(OVERALL, OVERALL_TITLE, name, doneDate, hideDailySampleValue, hideLastValue,
                    goodType, notificationMark, needToMark, 0L, "", goodsCode, null, null));
        }

        private static void addItemsFromList(List<GoodRequestsNewReportService.Item> itemList, BasicReportJob.OrgShortItem org, Date doneDate, String name,
                Long totalCount, Long dailySampleCount, Long tempClientsCount, Long newTotalCount, Long newDailySample,
                Long newTempClients, int hideDailySampleValue, int hideLastValue, GoodRequestsNewReportService.FeedingPlanType goodType, Long notificationMark,
                Long needToMark, String goodsCode) {
            itemList.add(new GoodRequestsNewReportService.Item(org, name, doneDate, totalCount, dailySampleCount, tempClientsCount, newTotalCount,
                    newDailySample, newTempClients, hideDailySampleValue, hideLastValue, goodType, notificationMark, needToMark, goodsCode, null, null));
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
}
