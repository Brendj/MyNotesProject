/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.ComplexInfo;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.persistence.OrderTypeEnumType;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.model.requestsandorders.*;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ziganshin
 * Date: 28.10.14
 * Time: 18:00
 * This class almost similar to GoodRequestsNewReportService.
 */
public class RequestsAndOrdersReportService {

    final private static Logger logger = LoggerFactory.getLogger(GoodRequestsNewReportService.class);
    private static HashMap<FeedingPlanType, String> priority = new HashMap<FeedingPlanType, String>();

    static {
        priority.put(FeedingPlanType.REDUCED_PRICE_PLAN, "Льготное питание");
        priority.put(FeedingPlanType.PAY_PLAN, "Платное питание");
        priority.put(FeedingPlanType.SUBSCRIPTION_FEEDING, "Абонементное питание");
    }

    final private long OVERALL;
    final private String OVERALL_TITLE;
    final private Session session;

    public RequestsAndOrdersReportService(Session session, long OVERALL, String OVERALL_TITLE) {
        this.OVERALL = OVERALL;
        this.OVERALL_TITLE = OVERALL_TITLE;
        this.session = session;
    }

    public List<Item> buildReportItems(Date startTime, Date endTime, List<Long> idOfOrgList,
            List<Long> idOfMenuSourceOrgList, boolean hideMissedColumns, boolean useColorAccent, boolean showOnlyDivergence) {

        boolean isNew = false;
        boolean isUpdate = false;

        HashMap<Long, BasicReportJob.OrgShortItem> orgMap = getOrgMap(idOfOrgList, idOfMenuSourceOrgList);

        List<Item> itemList = new LinkedList<Item>();

        Date beginDate = CalendarUtils.truncateToDayOfMonth(startTime);
        Date endDate = CalendarUtils.endOfDay(endTime);
        TreeSet<Date> dates = new TreeSet<Date>();

        List complexList = getComplexList(orgMap, beginDate, endDate);

        if (complexList.size() <= 0) {
            InputMismatchException e = new InputMismatchException("В выбранный период для выбранных организаций комплексы не представлены: выберите другой период или измените набор ОО.");
            throw e;
        }

        ReportDataMap reportDataMap;
        reportDataMap = new ReportDataMap();

        Map<Long, ComplexInfoItem> orgsComplexDictionary = new HashMap<Long, ComplexInfoItem>();
        Map<Long, GoodInfo> complexGoodsDictionary = new HashMap<Long, GoodInfo>();
        for (Object complexObj : complexList) {
            ComplexInfo complexInfo = (ComplexInfo) complexObj;
            FeedingPlanType complexFeedingPlanType = null;
            if (complexInfo != null) {
                if ((complexInfo.getUsedSubscriptionFeeding() != null) && (complexInfo.getUsedSubscriptionFeeding() == 1)) {
                    complexFeedingPlanType = FeedingPlanType.SUBSCRIPTION_FEEDING; //complexFeedingPlanType = "Абонементное питание";
                } else {
                    if (complexInfo.getModeFree() == 1) {
                        complexFeedingPlanType = FeedingPlanType.REDUCED_PRICE_PLAN; //complexFeedingPlanType = "Льготное питание";
                    } else {
                        complexFeedingPlanType = FeedingPlanType.PAY_PLAN; //complexFeedingPlanType = "Платное питание";
                    }
                }
            }
            final Long complexGoodGlobalId = complexInfo.getGood().getGlobalId();
            final Long complexOrgId = complexInfo.getOrg().getIdOfOrg();
            ComplexInfoItem complexInfoItem = orgsComplexDictionary.get(complexOrgId);
            if (complexInfoItem == null) {
                complexInfoItem = new ComplexInfoItem(complexOrgId);
            }
            GoodInfo complexGoodInfo = new GoodInfo(complexGoodGlobalId, "", complexFeedingPlanType);
            complexInfoItem.goodInfos.put(complexGoodGlobalId, complexGoodInfo);
            orgsComplexDictionary.put(complexOrgId, complexInfoItem);
            complexGoodsDictionary.put(complexGoodGlobalId, complexGoodInfo);
        }

        getRequestGoodsInfo(orgMap, reportDataMap, beginDate, endDate, orgsComplexDictionary);

        getPaidOrdersInfo(orgMap, reportDataMap, beginDate, endDate, orgsComplexDictionary);

        if (!hideMissedColumns) {
            reportDataMap.complement(beginDate, endDate);
        }

        populateDataList(reportDataMap, itemList);

        if (itemList.size() <= 0) {
            BasicReportJob.OrgShortItem orgShortItem = (BasicReportJob.OrgShortItem) orgMap.values().toArray()[0];
            String goodName = "";
            Date date = CalendarUtils.truncateToDayOfMonth(startTime);
            FeedingPlanType feedingPlanType = FeedingPlanType.PAY_PLAN;
            itemList.add(new Item(orgShortItem, "", CalendarUtils.truncateToDayOfMonth(startTime), 0, 0, feedingPlanType));
        }

        return itemList;
    }

    private void populateDataList(ReportDataMap reportDataMap, List<Item> itemList) {
        for (String orgName : reportDataMap.keySet()) {
            for (FeedingPlanType feedingPlanType: reportDataMap.get(orgName).keySet()) {
                for (String complexName: reportDataMap.get(orgName).get(feedingPlanType).keySet()){
                    for (String dateString: reportDataMap.get(orgName).get(feedingPlanType).get(complexName).keySet()) {
                        Long requested = reportDataMap.get(orgName).get(feedingPlanType).get(complexName).get(dateString).get(State.Requested);
                        Long ordered = reportDataMap.get(orgName).get(feedingPlanType).get(complexName).get(dateString).get(State.Ordered);
                        String orgNum = Org.extractOrgNumberFromName(orgName);
                        Date date = null;
                        try {
                            date = new SimpleDateFormat("EE dd.MM", new Locale("ru")).parse(dateString);
                        } catch (Exception e) {
                            e.printStackTrace();
                            //throw new Exception("Ошибка парсера строки даты: " + dateString);
                        }
                        itemList.add(
                                new Item(
                                        orgNum,
                                        orgName,
                                        complexName,
                                        date,
                                        requested,
                                        0L,
                                        0L,
                                        0L,
                                        0,
                                        0,
                                        feedingPlanType,
                                        "Заказано"));
                        itemList.add(
                                new Item(
                                        orgNum,
                                        orgName,
                                        complexName,
                                        date,
                                        ordered,
                                        0L,
                                        0L,
                                        0L,
                                        0,
                                        0,
                                        feedingPlanType,
                                        "Оплачено"));
                    }
                }
            }
        }
    }

    private void getRequestGoodsInfo(HashMap<Long, BasicReportJob.OrgShortItem> orgMap, ReportDataMap reportDataMap,
            Date beginDate, Date endDate, Map<Long, ComplexInfoItem> complexOrgDictionary) {

        Criteria goodRequestPositionCriteria = session.createCriteria(GoodRequestPosition.class);
        goodRequestPositionCriteria.createAlias("goodRequest", "gr");
        goodRequestPositionCriteria.add(Restrictions.between("gr.doneDate", beginDate, endDate));
        goodRequestPositionCriteria.add(Restrictions.in("gr.orgOwner", orgMap.keySet()));
        goodRequestPositionCriteria.add(Restrictions.isNotNull("good"));
        goodRequestPositionCriteria.add(Restrictions.eq("deletedState", false));
        goodRequestPositionCriteria.add(Restrictions.eq("gr.deletedState", false));

        List goodRequestPositionList = goodRequestPositionCriteria.list();

        SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("EE dd.MM", new Locale("ru"));
        for (Object obj : goodRequestPositionList) {
            GoodRequestPosition position = (GoodRequestPosition) obj;
            BasicReportJob.OrgShortItem org = orgMap.get(position.getOrgOwner());

            Long totalCount = position.getTotalCount() / 1000;
            Long dailySampleCount = (position.getDailySampleCount() != null) ? position.getDailySampleCount()/1000 : null;

            String orgName = org.getOfficialName() != null ? org.getOfficialName() : org.getShortName();
            FeedingPlanType feedingPlanType =
                    complexOrgDictionary.containsKey(position.getOrgOwner())
                            ? (complexOrgDictionary.get(position.getOrgOwner()).goodInfos.containsKey(position.getGood().getGlobalId())
                                ? complexOrgDictionary.get(position.getOrgOwner()).goodInfos.get(position.getGood().getGlobalId()).feedingPlanType
                                    : FeedingPlanType.PAY_PLAN)
                            : FeedingPlanType.PAY_PLAN;
            String complexName = position.getGood().getFullName() != null ? position.getGood().getFullName() : position.getGood().getNameOfGood();
            String dateString = simpleDateFormat.format(position.getGoodRequest().getDoneDate());
            State state = State.Requested;
            Long count = totalCount + dailySampleCount;

            FeedingPlan feedingPlan = new FeedingPlan();
            Complex complex = new Complex();
            DateElement dateElement = new DateElement();
            Element element = new Element();

            element.put(state, count);
            dateElement.put(dateString, element);
            complex.put(complexName, dateElement);
            feedingPlan.put(feedingPlanType, complex);

            reportDataMap.put(orgName, feedingPlan);
        }
    }

    private void getPaidOrdersInfo(HashMap<Long, BasicReportJob.OrgShortItem> orgMap, ReportDataMap reportDataMap,
            Date beginDate, Date endDate, Map<Long, ComplexInfoItem> complexOrgDictionary) {

        HashMap<Long, Org> orgMapFull;
        orgMapFull = new HashMap<Long, Org>(orgMap.size());
        for(Long orgKey : orgMap.keySet()) {
            Org org = null;
            try {
                org = DAOUtils.findOrg(session, orgMap.get(orgKey).getIdOfOrg());
            } catch (Exception e) {
                System.out.println("Ошибка при вызове DAOUtils.findOrg(" + orgMap.get(orgKey).getIdOfOrg() + ").");
            }
            orgMapFull.put(orgKey, org);
        }

        Criteria orderDetailsCriteria = session.createCriteria(OrderDetail.class, "od");
        orderDetailsCriteria.createAlias("order", "o");
        orderDetailsCriteria.add(Restrictions.between("o.createTime", beginDate, endDate));
        orderDetailsCriteria.add(Restrictions.eq("o.state", 0));
        orderDetailsCriteria.add(
                Restrictions.disjunction().add(Restrictions.eq("o.orderType", OrderTypeEnumType.DEFAULT))
                        .add(Restrictions.eq("o.orderType", OrderTypeEnumType.PAY_PLAN))
                        .add(Restrictions.eq("o.orderType", OrderTypeEnumType.REDUCED_PRICE_PLAN))
                        .add(Restrictions.eq("o.orderType", OrderTypeEnumType.DAILY_SAMPLE))
                        .add(Restrictions.eq("o.orderType", OrderTypeEnumType.REDUCED_PRICE_PLAN_RESERVE))
                        .add(Restrictions.eq("o.orderType", OrderTypeEnumType.SUBSCRIPTION_FEEDING))
                        .add(Restrictions.eq("o.orderType", OrderTypeEnumType.CORRECTION_TYPE)));
        orderDetailsCriteria.add(Restrictions.between("menuType", 50, 99));
        orderDetailsCriteria.add(Restrictions.in("org", orgMapFull.values()));
        orderDetailsCriteria.add(Restrictions.isNotNull("good"));

        List orderDetailsList = orderDetailsCriteria.list();

        SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("EE dd.MM", new Locale("ru"));

        for (Object obj : orderDetailsList) {
            OrderDetail detail = (OrderDetail) obj;
            BasicReportJob.OrgShortItem org = orgMap.get(detail.getOrg().getIdOfOrg());

            Long totalCount = 0L;
            Long dailySampleCount = 0L;
            if (detail.getOrder().getOrderType() != OrderTypeEnumType.DAILY_SAMPLE) {
                totalCount = detail.getQty();
            } else {
                dailySampleCount = detail.getQty();
            }

            String orgName = org.getOfficialName() != null ? org.getOfficialName() : org.getShortName();
            FeedingPlanType feedingPlanType =
                    (complexOrgDictionary.containsKey(detail.getOrg().getIdOfOrg()))
                            ? (complexOrgDictionary.get(detail.getOrg().getIdOfOrg()).goodInfos.containsKey(detail.getGood().getGlobalId())
                                ? complexOrgDictionary.get(detail.getOrg().getIdOfOrg()).goodInfos.get(detail.getGood().getGlobalId()).feedingPlanType
                                : FeedingPlanType.PAY_PLAN)
                            : FeedingPlanType.PAY_PLAN;
            String complexName = detail.getGood().getFullName() != null ? detail.getGood().getFullName() : detail.getGood().getNameOfGood();
            String dateString = simpleDateFormat.format(detail.getOrder().getCreateTime());
            State state = State.Ordered;
            Long count = totalCount + dailySampleCount;

            FeedingPlan feedingPlan = new FeedingPlan();
            Complex complex = new Complex();
            DateElement dateElement = new DateElement();
            Element element = new Element();

            element.put(state, count);
            dateElement.put(dateString, element);
            complex.put(complexName, dateElement);
            feedingPlan.put(feedingPlanType, complex);

            reportDataMap.put(orgName, feedingPlan);
        }
    }

    private List getComplexList(HashMap<Long, BasicReportJob.OrgShortItem> orgMap, Date beginDate, Date endDate) {
        Criteria criteriaComplex = session.createCriteria(ComplexInfo.class);
        criteriaComplex.createAlias("org", "o");
        criteriaComplex.add(Restrictions.isNotNull("good"));
        criteriaComplex.add(Restrictions.in("o.idOfOrg", orgMap.keySet()));
        criteriaComplex.add(Restrictions.between("menuDate", beginDate, endDate));
        return criteriaComplex.list();
    }

    private HashMap<Long, BasicReportJob.OrgShortItem> getOrgMap(List<Long> idOfOrgList,
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
        HashMap<Long, BasicReportJob.OrgShortItem> orgMap;
        orgMap = new HashMap<>(orgList.size());
        for (Object obj : orgList) {
            Object[] row = (Object[]) obj;
            long idOfOrg = Long.parseLong(row[0].toString());
            BasicReportJob.OrgShortItem educationItem;
            educationItem = new BasicReportJob.OrgShortItem(idOfOrg, row[1].toString(), row[2].toString());
            if (row[3] != null) {
                Long sourceMenuOrg = Long.parseLong(row[3].toString());
                educationItem.setSourceMenuOrg(sourceMenuOrg);
            }
            orgMap.put(idOfOrg, educationItem);
        }
        return orgMap;
    }

    private static class ComplexInfoItem {

        final long idOfOrg;
        final Map<Long, GoodInfo> goodInfos = new HashMap<Long, GoodInfo>();

        private ComplexInfoItem(long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

    }

    private static class GoodInfo {

        final long idOfGood;
        final String name;
        final FeedingPlanType feedingPlanType;

        private GoodInfo(long idOfGood, String name, FeedingPlanType feedingPlanType) {
            this.idOfGood = idOfGood;
            this.name = name;
            this.feedingPlanType = feedingPlanType;
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
        private FeedingPlanType feedingPlanType;  // План питания льготный, платный, абонимент
        private String feedingPlanTypeStr;
        private Integer feedingPlanTypeNum;
        private Long totalCount;
        private Long dailySample;
        private Long newTotalCount;
        private Long newDailySample;
        private String state;

        protected Item(Item item, Date doneDate) {
            this(Long.parseLong(item.getOrgNum()), item.getOfficialName(), item.getGoodName(), doneDate, 0L, 0L, 0L, 0L,
                    item.getHideDailySample(), item.getHideLastValue(), item.getFeedingPlanType());
        }

        public Item(String orgNum, String officialName, String goodName, Date doneDate, Long totalCount, Long dailySample,
                Long newTotalCount, Long newDailySample, int hideDailySampleValue, int hideLastValue,
                FeedingPlanType feedingPlanType, String state) {
            this.orgNum = orgNum;
            this.officialName = officialName;
            this.goodName = goodName;
            this.doneDate = doneDate;
            this.doneDateStr = YEAR_DATE_FORMAT.format(doneDate);
            this.totalCount = totalCount;
            this.dailySample = dailySample;
            this.newTotalCount = newTotalCount;
            this.newDailySample = newDailySample;
            this.hideDailySample = hideDailySampleValue;
            this.hideLastValue = hideLastValue;
            this.feedingPlanType = feedingPlanType;
            if (feedingPlanType == null) {
                feedingPlanTypeStr = "";
                feedingPlanTypeNum = -1;
            } else {
                feedingPlanTypeStr = priority.get(feedingPlanType);
                feedingPlanTypeNum = feedingPlanType.ordinal();
            }
            this.state = state;
        }

        public Item(Long orgNum, String officialName, String goodName, Date doneDate, Long totalCount, Long dailySample,
                Long newTotalCount, Long newDailySample, int hideDailySampleValue, int hideLastValue,
                FeedingPlanType feedingPlanType, String state) {
            this.orgNum = orgNum.toString();
            this.officialName = officialName;
            this.goodName = goodName;
            this.doneDate = doneDate;
            this.doneDateStr = YEAR_DATE_FORMAT.format(doneDate);
            this.totalCount = totalCount;
            this.dailySample = dailySample;
            this.newTotalCount = newTotalCount;
            this.newDailySample = newDailySample;
            this.hideDailySample = hideDailySampleValue;
            this.hideLastValue = hideLastValue;
            this.feedingPlanType = feedingPlanType;
            if (feedingPlanType == null) {
                feedingPlanTypeStr = "";
                feedingPlanTypeNum = -1;
            } else {
                feedingPlanTypeStr = priority.get(feedingPlanType);
                feedingPlanTypeNum = feedingPlanType.ordinal();
            }
            this.state = state;
        }

        public Item(Long orgNum, String officialName, String goodName, Date doneDate, Long totalCount, Long dailySample,
                Long newTotalCount, Long newDailySample, int hideDailySampleValue, int hideLastValue,
                FeedingPlanType feedingPlanType) {
            this.orgNum = orgNum.toString();
            this.officialName = officialName;
            this.goodName = goodName;
            this.doneDate = doneDate;
            this.doneDateStr = YEAR_DATE_FORMAT.format(doneDate);
            this.totalCount = totalCount;
            this.dailySample = dailySample;
            this.newTotalCount = newTotalCount;
            this.newDailySample = newDailySample;
            this.hideDailySample = hideDailySampleValue;
            this.hideLastValue = hideLastValue;
            this.feedingPlanType = feedingPlanType;
            if (feedingPlanType == null) {
                feedingPlanTypeStr = "";
                feedingPlanTypeNum = -1;
            } else {
                feedingPlanTypeStr = priority.get(feedingPlanType);
                feedingPlanTypeNum = feedingPlanType.ordinal();
            }
            this.state = "Не определено";
        }

        public Item(Long orgNum, String officialName, String goodName, Date doneDate, int hideDailySampleValue,
                int hideLastValue, FeedingPlanType feedingPlanType) {
            this(orgNum, officialName, goodName, doneDate, 0L, 0L, 0L, 0L, hideDailySampleValue, hideLastValue,
                    feedingPlanType);
        }

        public Item(BasicReportJob.OrgShortItem item, String goodName, Date doneDate, Long totalCount, Long dailySample,
                Long newTotalCount, Long newDailySample, int hideDailySampleValue, int hideLastValue,
                FeedingPlanType feedingPlanType, String state) {
            this(Long.parseLong(Org.extractOrgNumberFromName(item.getOfficialName())), item.getShortName(), goodName,
                    doneDate, totalCount, dailySample, newTotalCount, newDailySample, hideDailySampleValue,
                    hideLastValue, feedingPlanType, state);

        }

        public Item(BasicReportJob.OrgShortItem item, String goodName, Date doneDate, Long totalCount, Long dailySample,
                Long newTotalCount, Long newDailySample, int hideDailySampleValue, int hideLastValue,
                FeedingPlanType feedingPlanType) {
            this(Long.parseLong(Org.extractOrgNumberFromName(item.getOfficialName())), item.getShortName(), goodName,
                    doneDate, totalCount, dailySample, newTotalCount, newDailySample, hideDailySampleValue,
                    hideLastValue, feedingPlanType);

        }

        public Item(BasicReportJob.OrgShortItem org, String goodName, Date date, int hideDailySampleValue,
                int hideLastValue, FeedingPlanType feedingPlanType) {
            this(org, goodName, date, 0L, 0L, 0L, 0L, hideDailySampleValue, hideLastValue, feedingPlanType);
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
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
            return this.orgNum;
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

        public FeedingPlanType getFeedingPlanType() {
            return feedingPlanType;
        }

        public void setFeedingPlanType(FeedingPlanType feedingPlanType) {
            this.feedingPlanType = feedingPlanType;
        }

        public String getFeedingPlanTypeStr() {
            return feedingPlanTypeStr;
        }

        public void setFeedingPlanTypeStr(String feedingPlanTypeStr) {
            this.feedingPlanTypeStr = feedingPlanTypeStr;
        }

        public Integer getFeedingPlanTypeNum() {
            return feedingPlanTypeNum;
        }

        public void setFeedingPlanTypeNum(Integer feedingPlanTypeNum) {
            this.feedingPlanTypeNum = feedingPlanTypeNum;
        }
    }
}
