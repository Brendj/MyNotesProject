/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.ComplexInfo;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
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
 * User: damir
 * Date: 19.03.14
 */
public class GoodRequestsNewReportService {

    final private static Logger logger = LoggerFactory.getLogger(GoodRequestsNewReportService.class);
    private static HashMap<FeedingPlanType, String> priorety = new HashMap<FeedingPlanType, String>();

    static {
        priorety.put(FeedingPlanType.REDUCED_PRICE_PLAN, "Льготное питание");
        priorety.put(FeedingPlanType.PAY_PLAN, "Платное питание");
        priorety.put(FeedingPlanType.SUBSCRIPTION_FEEDING, "Абонементное питание");
    }

    final private String OVERALL;
    final private String OVERALL_TITLE;
    final private boolean hideTotalRow;
    final private Session session;

    public GoodRequestsNewReportService(Session session, String OVERALL, String OVERALL_TITLE, boolean hideTotalRow) {
        this.OVERALL = OVERALL;
        this.OVERALL_TITLE = OVERALL_TITLE;
        this.session = session;
        this.hideTotalRow = hideTotalRow;
    }

    public List<Item> buildReportItems(Date startTime, Date endTime, String nameFilter, int orgFilter,
            int hideDailySampleValue, Date generateBeginTime, Date generateEndTime, List<Long> idOfOrgList,
            List<Long> idOfMenuSourceOrgList, boolean hideMissedColumns, boolean hideGeneratePeriod, int hideLastValue,
            boolean notification) {

        HashMap<Long, BasicReportJob.OrgShortItem> orgMap = getDefinedOrgs(idOfOrgList, idOfMenuSourceOrgList);

        List<Item> itemList = new LinkedList<Item>();

        Date beginDate = CalendarUtils.truncateToDayOfMonth(startTime);
        Date endDate = CalendarUtils.endOfDay(endTime);
        TreeSet<Date> dates = new TreeSet<Date>();

        Criteria criteriaComplex = session.createCriteria(ComplexInfo.class);
        criteriaComplex.createAlias("org", "o");
        criteriaComplex.add(Restrictions.isNotNull("good"));
        criteriaComplex.add(Restrictions.in("o.idOfOrg", orgMap.keySet()));
        criteriaComplex.add(Restrictions.between("menuDate", beginDate, endDate));
        List complexList = criteriaComplex.list();
        Map<Long, ComplexInfoItem> complexOrgDictionary = new HashMap<Long, ComplexInfoItem>();
        Map<Long, GoodInfo> allGoodsInfo = new HashMap<Long, GoodInfo>();
        for (Object obj : complexList) {
            ComplexInfo complexInfo = (ComplexInfo) obj;
            FeedingPlanType feedingPlanType = null;
            if (complexInfo != null) {
                if ((complexInfo.getUsedSubscriptionFeeding() != null) && (complexInfo.getUsedSubscriptionFeeding()
                        == 1)) {
                    //feedingPlanType = "Абонементное питание";
                    feedingPlanType = FeedingPlanType.SUBSCRIPTION_FEEDING;
                } else {
                    if (complexInfo.getModeFree() == 1) {
                        //feedingPlanType = "Льготное питание";
                        feedingPlanType = FeedingPlanType.REDUCED_PRICE_PLAN;
                    } else {
                        //feedingPlanType = "Платное питание";
                        feedingPlanType = FeedingPlanType.PAY_PLAN;
                    }
                }
            }
            final Long globalId = complexInfo.getGood().getGlobalId();
            final Long idOfOrg = complexInfo.getOrg().getIdOfOrg();
            ComplexInfoItem infoItem = complexOrgDictionary.get(idOfOrg);
            GoodInfo info = new GoodInfo(globalId, "", feedingPlanType);
            if (infoItem == null) {
                infoItem = new ComplexInfoItem(idOfOrg);
            }
            infoItem.goodInfos.put(globalId, info);
            complexOrgDictionary.put(idOfOrg, infoItem);
            allGoodsInfo.put(globalId, info);
        }

        List<GoodRequestPosition> goodRequestPositionList = getGoodRequestPositions(nameFilter, generateEndTime,
                hideGeneratePeriod, false, orgMap, beginDate, endDate);
        if (notification) {
            List<GoodRequestPosition> goodRequestPositionListN = getGoodRequestPositions(nameFilter, generateEndTime,
                    hideGeneratePeriod, true, orgMap, beginDate, endDate);
            if (goodRequestPositionListN.size() > 0) {
                goodRequestPositionList.addAll(goodRequestPositionListN);
            }
        }
        Map<Long, GoodInfo> requestGoodsInfo = new HashMap<Long, GoodInfo>();
        for (Object obj : goodRequestPositionList) {
            processPosition(hideDailySampleValue, generateBeginTime, generateEndTime, hideMissedColumns,
                    hideGeneratePeriod, hideLastValue, orgMap, itemList, beginDate, endDate, dates,
                    complexOrgDictionary, goodRequestPositionList, requestGoodsInfo, obj, notification);
        }

        if (orgFilter == 0 && !idOfMenuSourceOrgList.isEmpty()) {
            for (BasicReportJob.OrgShortItem item : orgMap.values()) {
                if (item.getSourceMenuOrg() != null) {
                    MultiValueMap fullNameProviderMap = new MultiValueMap();
                    Criteria goodCriteria = session.createCriteria(Good.class);
                    goodCriteria.add(Restrictions.in("orgOwner", idOfMenuSourceOrgList));
                    if (StringUtils.isNotEmpty(nameFilter)) {
                        goodCriteria.add(Restrictions.or(Restrictions.ilike("fullName", nameFilter, MatchMode.ANYWHERE),
                                Restrictions.ilike("nameOfGood", nameFilter, MatchMode.ANYWHERE)));
                    }
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
                            FeedingPlanType feedingPlanType = null;
                            if (allGoodsInfo.containsKey(idOfGood)) {
                                feedingPlanType = allGoodsInfo.get(idOfGood).feedingPlanType;
                            } else {
                                continue;
                            }
                            fullNameProviderMap.put(idOfOrg, new GoodInfo(idOfGood, nameOfGood, feedingPlanType));
                        }
                    }
                    if (fullNameProviderMap.getCollection(item.getSourceMenuOrg()) != null) {
                        for (Object object : fullNameProviderMap.getCollection(item.getSourceMenuOrg())) {
                            GoodInfo goodInfo = (GoodInfo) object;
                            if (hideMissedColumns) {
                                for (Date date : dates) {
                                    addItemsFromList(itemList, item, date, goodInfo.name, hideDailySampleValue,
                                            hideLastValue, goodInfo.feedingPlanType, 0L);
                                }
                            } else {
                                beginDate = CalendarUtils.truncateToDayOfMonth(startTime);
                                endDate = CalendarUtils.endOfDay(endTime);
                                while (beginDate.getTime() <= endDate.getTime()) {
                                    addItemsFromList(itemList, item, beginDate, goodInfo.name, hideDailySampleValue,
                                            hideLastValue, goodInfo.feedingPlanType, 0L);
                                    beginDate = CalendarUtils.addOneDay(beginDate);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (itemList.isEmpty() && !hideTotalRow) {
            for (BasicReportJob.OrgShortItem item : orgMap.values()) {
                itemList.add(new Item(item, "", CalendarUtils.truncateToDayOfMonth(startTime), hideDailySampleValue,
                        hideLastValue, null, 0L));
            }
            itemList.add(new Item(OVERALL, OVERALL_TITLE, "", CalendarUtils.truncateToDayOfMonth(startTime),
                    hideDailySampleValue, hideLastValue, null, 0L));
        }
        return itemList;
    }

    private HashMap<Long, BasicReportJob.OrgShortItem> getDefinedOrgs(List<Long> idOfOrgList,
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
                idOfMenuSourceOrgList.add(sourceMenuOrg);
            }
            orgMap.put(idOfOrg, educationItem);
        }
        return orgMap;
    }

    private void processPosition(int hideDailySampleValue, Date generateBeginTime, Date generateEndTime,
            boolean hideMissedColumns, boolean hideGeneratePeriod, int hideLastValue,
            HashMap<Long, BasicReportJob.OrgShortItem> orgMap, List<Item> itemList, Date beginDate, Date endDate,
            TreeSet<Date> dates, Map<Long, ComplexInfoItem> complexOrgDictionary, List goodRequestPositionList,
            Map<Long, GoodInfo> requestGoodsInfo, Object obj, boolean notification) {
        Date doneDate;
        GoodRequestPosition position = (GoodRequestPosition) obj;
        BasicReportJob.OrgShortItem org = orgMap.get(position.getOrgOwner());

        Long totalCount = position.getTotalCount() / 1000;
        Long dailySampleCount = getSafeValue(position.getDailySampleCount()) / 1000L;
        Long tempClientsCount = getSafeValue(position.getTempClientsCount()) / 1000L;

        Long newTotalCount = 0L;
        Long newDailySample = 0L;

        if (hideGeneratePeriod) {
            Date createDate = position.getCreatedDate();
            if (CalendarUtils.betweenDate(createDate, generateBeginTime, generateEndTime)) {
                newTotalCount = totalCount;
                newDailySample = dailySampleCount;
            }

            Date lastDate = position.getLastUpdate();
            if (lastDate != null) {
                if (CalendarUtils.betweenDate(lastDate, generateBeginTime, generateEndTime)) {
                    newTotalCount = totalCount - getSafeValue(position.getLastTotalCount()) / 1000L;
                    newDailySample = dailySampleCount - getSafeValue(position.getLastDailySampleCount()) / 1000L;
                }
            }
        }

        Long notificationMark = 0L;
        if (position.getDeletedState()) {
            totalCount = 0L;
            notificationMark = 1L;
        }

        doneDate = CalendarUtils.truncateToDayOfMonth(position.getGoodRequest().getDoneDate());

        final Good good = position.getGood();
        FeedingPlanType feedingPlanType = null;
        if (complexOrgDictionary.containsKey(position.getOrgOwner())) {
            if (complexOrgDictionary.get(position.getOrgOwner()).goodInfos.containsKey(good.getGlobalId())) {
                feedingPlanType = complexOrgDictionary.get(position.getOrgOwner()).goodInfos
                        .get(good.getGlobalId()).feedingPlanType;
            }
        }
        String name = good.getFullName();
        if (StringUtils.isEmpty(name)) {
            name = good.getNameOfGood();
        }
        if (!requestGoodsInfo.containsKey(good.getGlobalId())) {
            requestGoodsInfo.put(good.getGlobalId(), new GoodInfo(good.getGlobalId(), name, feedingPlanType));
        }
        // чтобы хотя бы раз выполнилмся, для уведомлений
        if (!hideMissedColumns && hideTotalRow && goodRequestPositionList.indexOf(obj) == 0) {
            while (beginDate.getTime() <= endDate.getTime()) {
                itemList.add(new Item(org, name, beginDate, 0L, 0L, 0L, 0L, 0L, hideDailySampleValue, hideLastValue,
                        feedingPlanType, 0L));
                dates.add(beginDate);
                beginDate = CalendarUtils.addOneDay(beginDate);
            }
        }

        addItemsFromList(itemList, org, doneDate, name, totalCount, dailySampleCount, tempClientsCount, newTotalCount, newDailySample,
                hideDailySampleValue, hideLastValue, feedingPlanType, notificationMark);
        dates.add(doneDate);
        if (notification) {
            position.setNotified(true);
            session.persist(position);
        }
    }

    private Long getSafeValue(Long number) {
        return number != null ? number : 0L;
    }

    private List<GoodRequestPosition> getGoodRequestPositions(String nameFilter, Date generateEndTime,
            boolean hideGeneratePeriod, boolean notification, HashMap<Long, BasicReportJob.OrgShortItem> orgMap,
            Date beginDate, Date endDate) {
        Criteria criteria = session.createCriteria(GoodRequestPosition.class);
        criteria.createAlias("goodRequest", "gr");
        criteria.add(Restrictions.between("gr.doneDate", beginDate, endDate));
        criteria.add(Restrictions.in("gr.orgOwner", orgMap.keySet()));
        criteria.add(Restrictions.isNotNull("good"));

        if (notification) {
            criteria.add(Restrictions.eq("deletedState", true));
            criteria.add(Restrictions.eq("gr.deletedState", true));
            criteria.add(Restrictions.or(Restrictions.eq("notified", false), Restrictions.isNull("notified")));
        } else {
            criteria.add(Restrictions.eq("deletedState", false));
            criteria.add(Restrictions.eq("gr.deletedState", false));
        }

        if (hideGeneratePeriod) {
            Disjunction dateDisjunction = Restrictions.disjunction();
            dateDisjunction.add(Restrictions.le("createdDate", generateEndTime));
            dateDisjunction.add(Restrictions.le("lastUpdate", generateEndTime));
            criteria.add(dateDisjunction);
        }
        if (StringUtils.isNotEmpty(nameFilter)) {
            criteria.createAlias("good", "g");
            criteria.add(Restrictions.or(Restrictions.ilike("g.fullName", nameFilter, MatchMode.ANYWHERE),
                    Restrictions.ilike("g.nameOfGood", nameFilter, MatchMode.ANYWHERE)));
        }
        List<GoodRequestPosition> list = criteria.list();
        return list == null ? new ArrayList<GoodRequestPosition>() : list;
    }

    private void addItemsFromList(List<Item> itemList, BasicReportJob.OrgShortItem org, Date doneDate, String name,
            int hideDailySampleValue, int hideLastValue, FeedingPlanType feedingPlanType, Long notificationMark) {
        itemList.add(
                new Item(org, name, doneDate, hideDailySampleValue, hideLastValue, feedingPlanType, notificationMark));
        if (!hideTotalRow) {
            itemList.add(new Item(OVERALL, OVERALL_TITLE, name, doneDate, hideDailySampleValue, hideLastValue,
                    feedingPlanType, notificationMark));
        }
    }

    private void addItemsFromList(List<Item> itemList, BasicReportJob.OrgShortItem org, Date doneDate, String name,
            Long totalCount, Long dailySampleCount, Long tempClientsCount, Long newTotalCount, Long newDailySample, int hideDailySampleValue,
            int hideLastValue, FeedingPlanType feedingPlanType, Long notificationMark) {
        itemList.add(new Item(org, name, doneDate, totalCount, dailySampleCount, tempClientsCount, newTotalCount, newDailySample,
                hideDailySampleValue, hideLastValue, feedingPlanType, notificationMark));
        if (!hideTotalRow) {
            itemList.add(new Item(OVERALL, OVERALL_TITLE, name, doneDate, totalCount, dailySampleCount, tempClientsCount, newTotalCount,
                    newDailySample, hideDailySampleValue, hideLastValue, feedingPlanType, 0L));
        }
    }

    private static enum FeedingPlanType {
        /*0*/ REDUCED_PRICE_PLAN,
        /*1*/ PAY_PLAN,
        /*2*/ SUBSCRIPTION_FEEDING
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
        private Long tempClients;
        private Long newTotalCount;
        private Long newDailySample;
        private Long notificationMark;

        protected Item(Item item, Date doneDate) {
            this(item.getOrgNum(), item.getOfficialName(), item.getGoodName(), doneDate, 0L, 0L, 0L, 0L, 0L,
                    item.getHideDailySample(), item.getHideLastValue(), item.getFeedingPlanType(),
                    item.getNotificationMark());
        }

        public Item(String orgNum, String officialName, String goodName, Date doneDate, Long totalCount,
                Long dailySample, Long tempClients, Long newTotalCount, Long newDailySample, int hideDailySampleValue, int hideLastValue,
                FeedingPlanType feedingPlanType, Long notificationMark) {
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
            this.hideDailySample = hideDailySampleValue;
            this.hideLastValue = hideLastValue;
            this.feedingPlanType = feedingPlanType;
            if (feedingPlanType == null) {
                feedingPlanTypeStr = "";
                feedingPlanTypeNum = -1;
            } else {
                feedingPlanTypeStr = priorety.get(feedingPlanType);
                feedingPlanTypeNum = feedingPlanType.ordinal();
            }
            this.notificationMark = notificationMark;

        }

        public Item(String orgNum, String officialName, String goodName, Date doneDate, int hideDailySampleValue,
                int hideLastValue, FeedingPlanType feedingPlanType, Long notificationMark) {
            this(orgNum, officialName, goodName, doneDate, 0L, 0L, 0L, 0L, 0L, hideDailySampleValue,
                    hideLastValue, feedingPlanType, notificationMark);
        }

        public Item(BasicReportJob.OrgShortItem item, String goodName, Date doneDate, Long totalCount, Long dailySample, Long tempClients,
                Long newTotalCount, Long newDailySample, int hideDailySampleValue, int hideLastValue,
                FeedingPlanType feedingPlanType, Long notificationMark) {
            this(Org.extractOrgNumberFromName(item.getOfficialName()), item.getShortName(), goodName, doneDate,
                    totalCount, dailySample, tempClients, newTotalCount, newDailySample, hideDailySampleValue, hideLastValue,
                    feedingPlanType, notificationMark);

        }

        public Item(BasicReportJob.OrgShortItem item, String goodName, Date doneDate, int hideDailySampleValue,
                int hideLastValue, FeedingPlanType feedingPlanType, Long notificationMark) {
            this(item, goodName, doneDate, 0L, 0L, 0L, 0L, 0L, hideDailySampleValue, hideLastValue, feedingPlanType,
                    notificationMark);
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
    }
}
