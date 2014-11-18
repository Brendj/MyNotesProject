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
import ru.axetta.ecafe.processor.core.persistence.utils.FriendlyOrganizationsInfoModel;
import ru.axetta.ecafe.processor.core.persistence.utils.OrgUtils;
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
import java.text.ParseException;
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

    final private long OVERALL;
    final private String OVERALL_TITLE;
    final private Session session;

    public RequestsAndOrdersReportService(Session session, long OVERALL, String OVERALL_TITLE) {
        this.OVERALL = OVERALL;
        this.OVERALL_TITLE = OVERALL_TITLE;
        this.session = session;
    }

    public List<Item> buildReportItems(Date startTime, Date endTime, List<Long> idOfOrgList,
            List<Long> idOfMenuSourceOrgList, boolean hideMissedColumns, boolean useColorAccent,
            boolean showOnlyDivergence) throws Exception {
        HashMap<Long, BasicReportJob.OrgShortItem> orgMap = getOrgMap(idOfOrgList, idOfMenuSourceOrgList);
        List<Item> itemList = new LinkedList<Item>();
        Date beginDate = CalendarUtils.truncateToDayOfMonth(startTime);
        Date endDate = CalendarUtils.endOfDay(endTime);
        List complexList = getComplexList(orgMap, beginDate, endDate);
        if (complexList.size() <= 0) {
            InputMismatchException e = new InputMismatchException(
                    "В указанный период времени данные по комплексам отсутствуют: попробуйте изменить параметры отчета.");
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
                if ((complexInfo.getUsedSubscriptionFeeding() != null) && (complexInfo.getUsedSubscriptionFeeding()
                        == 1)) {
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
        } else {
            reportDataMap.complement();
        }
        populateDataList(reportDataMap, itemList, useColorAccent, showOnlyDivergence);
        if ((itemList == null) || (itemList.size() == 0)) {
            Exception e = new Exception("В указанный период времени данные по организации отсутствуют: попробуйте изменить параметры отчета.");
            throw e;
        } else {
            return itemList;
        }
    }

    private HashMap<Long, BasicReportJob.OrgShortItem> complementOrgMap(Session session, HashMap<Long, BasicReportJob.OrgShortItem> orgMap) {

        List<Long> idOfOrgList = null;
        for (Long idOfOrg: orgMap.keySet()){
            idOfOrgList.add(idOfOrg);
        }

        Set<Long> idOfOrgSet = null;
        Set<FriendlyOrganizationsInfoModel> organizationsInfoModelSet = OrgUtils.getMainBuildingAndFriendlyOrgsList(session, idOfOrgList);
        for (FriendlyOrganizationsInfoModel org: organizationsInfoModelSet) {
            idOfOrgSet.add(org.getIdOfOrg());
            Set<Org> friends = org.getFriendlyOrganizationsSet();
            if (friends != null) {
                for (Org friend: friends) {
                    idOfOrgSet.add(friend.getIdOfOrg());
                }
            }
        }

        HashMap<Long, BasicReportJob.OrgShortItem> completeOrgMap = null;
        for(Long idOfOrg: idOfOrgSet) {
            Org org = (Org) session.load(Org.class, idOfOrg);
            BasicReportJob.OrgShortItem educationItem = new BasicReportJob.OrgShortItem();
            educationItem.setIdOfOrg(org.getIdOfOrg());
            educationItem.setShortName(org.getShortName());
            educationItem.setOfficialName(org.getOfficialName());
            educationItem.setAddress(org.getAddress());
            completeOrgMap.put(educationItem.getIdOfOrg(), educationItem);
        }

        return completeOrgMap;
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
        orgMap = new HashMap<Long, BasicReportJob.OrgShortItem>(orgList.size());
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

    private void populateDataList(ReportDataMap reportDataMap, List<Item> itemList, Boolean useColorAccent, Boolean showOnlyDivergence) {
        for (String orgName : reportDataMap.keySet()) {
            for (FeedingPlanType feedingPlanType: reportDataMap.get(orgName).keySet()) {
                String feedingPlanTypeString = feedingPlanType.toString();
                for (String complexName: reportDataMap.get(orgName).get(feedingPlanType).keySet()){
                    for (Date date: reportDataMap.get(orgName).get(feedingPlanType).get(complexName).keySet()) {
                        Long requested = reportDataMap.get(orgName).get(feedingPlanType).get(complexName).get(date).get(State.Requested);
                        requested = requested == null ? 0L : requested;
                        Long ordered = reportDataMap.get(orgName).get(feedingPlanType).get(complexName).get(date).get(State.Ordered);
                        ordered = ordered == null ? 0L : ordered;
                        String orgNum = Org.extractOrgNumberFromName(orgName);
                        Boolean differState = (requested - ordered) != 0L;
                        if (!showOnlyDivergence || differState) {
                            if (useColorAccent) {
                                itemList.add(new Item(orgNum, orgName, feedingPlanTypeString, complexName, "Заказано", date, requested, differState));
                                itemList.add(new Item(orgNum, orgName, feedingPlanTypeString, complexName, "Оплачено", date, ordered, differState));
                            } else {
                                itemList.add(new Item(orgNum, orgName, feedingPlanTypeString, complexName, "Заказано", date, requested, false));
                                itemList.add(new Item(orgNum, orgName, feedingPlanTypeString, complexName, "Оплачено", date, ordered, false));
                            }
                        }
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
            Date date = position.getGoodRequest().getDoneDate();
            State state = State.Requested;
            Long count = totalCount + dailySampleCount;

            FeedingPlan feedingPlan = new FeedingPlan();
            Complex complex = new Complex();
            DateElement dateElement = new DateElement();
            Element element = new Element();

            element.put(state, count);
            dateElement.put(date, element);
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

        for (Object obj : orderDetailsList) {
            OrderDetail detail = (OrderDetail) obj;
            BasicReportJob.OrgShortItem org = orgMap.get(detail.getOrg().getIdOfOrg());

            Long totalCount = detail.getQty();;

            String orgName = org.getOfficialName() != null ? org.getOfficialName() : org.getShortName();
            FeedingPlanType feedingPlanType =
                    (complexOrgDictionary.containsKey(detail.getOrg().getIdOfOrg()))
                            ? (complexOrgDictionary.get(detail.getOrg().getIdOfOrg()).goodInfos.containsKey(detail.getGood().getGlobalId())
                                ? complexOrgDictionary.get(detail.getOrg().getIdOfOrg()).goodInfos.get(detail.getGood().getGlobalId()).feedingPlanType
                                : FeedingPlanType.PAY_PLAN)
                            : FeedingPlanType.PAY_PLAN;
            String complexName = detail.getGood().getFullName() != null ? detail.getGood().getFullName() : detail.getGood().getNameOfGood();
            Date date = detail.getOrder().getCreateTime();
            State state = State.Ordered;
            Long count = totalCount;

            FeedingPlan feedingPlan = new FeedingPlan();
            Complex complex = new Complex();
            DateElement dateElement = new DateElement();
            Element element = new Element();

            element.put(state, count);
            dateElement.put(date, element);
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
        final private static SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("EE dd.MM", new Locale("ru"));
        private String orgNum;
        private String orgName;
        private String feedingPlanTypeString;
        private String complexName;
        private String stateString;
        private Date date;
        private String dateString;
        private Long count;
        private Boolean differState;

        protected Item(String orgNum, String orgName, String feedingPlanTypeString, String complexName,
                String stateString, String dateString, Long count, Boolean differState) {
            this.orgNum = orgNum;
            this.orgName = orgName;
            this.feedingPlanTypeString = feedingPlanTypeString;
            this.complexName = complexName;
            this.stateString = stateString;
            try {
                this.date = simpleDateFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            this.dateString = dateString;
            this.count = count;
            this.differState = differState;
        }

        protected Item(String orgNum, String orgName, String feedingPlanTypeString, String complexName,
                String stateString, Date date, Long count, Boolean differState) {
            this.orgNum = orgNum;
            this.orgName = orgName;
            this.feedingPlanTypeString = feedingPlanTypeString;
            this.complexName = complexName;
            this.stateString = stateString;
            this.date = CalendarUtils.truncateToDayOfMonth(date);
            this.dateString = simpleDateFormat.format(date);
            this.count = count;
            this.differState = differState;
        }

        @Override
        public int compareTo(Object o) {
            return Integer.valueOf(hashCode()).compareTo(o.hashCode());
        }

        @Override
        public boolean equals(Object o) {
            // todo needs refactoring - maybe incomplete
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Item item = (Item) o;

            return dateString.equals(item.dateString) && complexName.equals(item.complexName) && orgName
                    .equals(item.orgName);

        }

        @Override
        public int hashCode() {
            int result = orgName.hashCode();
            result = 31 * result + complexName.hashCode();
            result = 31 * result + dateString.hashCode();
            return result;
        }

        public String getOrgNum() {
            return orgNum;
        }

        public void setOrgNum(String orgNum) {
            this.orgNum = orgNum;
        }

        public String getOrgName() {
            return orgName;
        }

        public void setOrgName(String orgName) {
            this.orgName = orgName;
        }

        public String getFeedingPlanTypeString() {
            return feedingPlanTypeString;
        }

        public void setFeedingPlanTypeString(String feedingPlanTypeString) {
            this.feedingPlanTypeString = feedingPlanTypeString;
        }

        public String getComplexName() {
            return complexName;
        }

        public void setComplexName(String complexName) {
            this.complexName = complexName;
        }

        public String getStateString() {
            return stateString;
        }

        public void setStateString(String stateString) {
            this.stateString = stateString;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getDateString() {
            return dateString;
        }

        public void setDateString(String dateString) {
            this.dateString = dateString;
        }


        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            count = count;
        }

        public Boolean getDifferState() {
            return differState;
        }

        public void setDifferState(Boolean differState) {
            this.differState = differState;
        }
    }
}
