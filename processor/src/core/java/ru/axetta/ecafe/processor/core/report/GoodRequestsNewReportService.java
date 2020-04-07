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
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static ru.axetta.ecafe.processor.core.service.PreorderRequestsReportService.PREORDER_COMMENT;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 19.03.14
 */
public class GoodRequestsNewReportService {

    final private static Logger logger = LoggerFactory.getLogger(GoodRequestsNewReportService.class);

    final private String OVERALL;
    final private String OVERALL_TITLE;
    final private boolean hideTotalRow;
    final private Session session;

    final private String MENU_DETAIL_TYPE_DINNER = "dinner";        // обед
    final private String MENU_DETAIL_TYPE_BREAKFAST = "breakfast";  // завтрак
    final private String MENU_DETAIL_TYPE_AFTERNOON = "afternoon";  // полдник
    final private String MENU_DETAIL_TYPE_SUPPER = "supper";        // ужин

    public GoodRequestsNewReportService(Session session, String OVERALL, String OVERALL_TITLE, boolean hideTotalRow) {
        this.OVERALL = OVERALL;
        this.OVERALL_TITLE = OVERALL_TITLE;
        this.session = session;
        this.hideTotalRow = hideTotalRow;
    }

    public List<Item> buildReportItems(Date startTime, Date endTime, String nameFilter, int orgFilter,
            int hideDailySampleValue, Date generateBeginTime, Date generateEndTime, List<Long> idOfOrgList,
            List<Long> idOfMenuSourceOrgList, boolean hideMissedColumns, boolean hideGeneratePeriod, int hideLastValue,
            boolean notification, boolean hidePreorders, boolean preordersOnly, boolean needFullGoodNames) {
        HashMap<Long, BasicReportJob.OrgShortItem> orgMap = getDefinedOrgs(idOfOrgList, idOfMenuSourceOrgList);

        List<Item> itemList = new LinkedList<Item>();

        Date beginDate = CalendarUtils.truncateToDayOfMonth(startTime);
        Date endDate = CalendarUtils.endOfDay(endTime);
        TreeSet<Date> dates = new TreeSet<Date>();
        Map<Long, ComplexInfoItem> complexOrgDictionary = new HashMap<Long, ComplexInfoItem>();
        Map<Long, GoodInfo> allGoodsInfo = new HashMap<Long, GoodInfo>();

        if (!preordersOnly) {
            Criteria criteriaComplex = session.createCriteria(ComplexInfo.class);

            criteriaComplex.createAlias("org", "o")
                    .add(Restrictions.isNotNull("good"))
                    .add(Restrictions.in("o.idOfOrg", orgMap.keySet()))
                    .add(Restrictions.between("menuDate", beginDate, endDate));

            List<ComplexInfo> complexList = criteriaComplex.list();
            for (ComplexInfo complexInfo : complexList) {
                FeedingPlanType feedingPlanType = null;
                if (complexInfo != null) {
                    if ((complexInfo.getUsedSubscriptionFeeding() != null)
                            && (complexInfo.getUsedSubscriptionFeeding() == 1)) {
                        feedingPlanType = FeedingPlanType.SUBSCRIPTION_FEEDING;
                    } else {
                        feedingPlanType = complexInfo.getModeFree() == 1 ?
                                FeedingPlanType.REDUCED_PRICE_PLAN : FeedingPlanType.PAY_PLAN;
                    }
                }
                final Long globalId = complexInfo.getGood().getGlobalId();
                final Long idOfOrg = complexInfo.getOrg().getIdOfOrg();
                final String goodsCode = complexInfo.getGood().getGoodsCode();
                final Long price = complexInfo.getCurrentPrice();

                ComplexInfoItem infoItem = complexOrgDictionary.get(idOfOrg);
                GoodInfo info = new GoodInfo(globalId, "", feedingPlanType, goodsCode, price);
                if (infoItem == null) {
                    infoItem = new ComplexInfoItem(idOfOrg);
                }
                infoItem.goodInfos.put(globalId, info);
                complexOrgDictionary.put(idOfOrg, infoItem);
                allGoodsInfo.put(globalId, info);
            }
        }

        if (!hidePreorders) {
            String sqlQuery =
                    "SELECT distinct ci.idoforg, "
                            + "   CASE WHEN (pc.amount = 0) THEN md.idofgood ELSE ci.idofgood END AS idofgood, "
                            + "   CASE WHEN (pc.amount = 0) THEN false ELSE true END AS iscomplex, "
                            + "   CASE WHEN (pc.amount = 0) THEN gmd.goodscode ELSE gc.goodscode END AS goodscode, "
                            + "   CASE WHEN (pc.amount = 0 AND pc.complexname ILIKE '%завтрак%') THEN 'breakfast' "
                            + "        WHEN (pc.amount = 0 AND pc.complexname ILIKE '%обед%') THEN 'dinner' "
                            + "        WHEN (pc.amount = 0 AND pc.complexname ILIKE '%полдник%') THEN 'afternoon' "
                            + "        WHEN (pc.amount = 0 AND pc.complexname ILIKE '%ужин%') THEN 'supper' ELSE '' END AS type,"
                            + "   CASE WHEN (pc.amount = 0) THEN pmd.idofgoodsrequestposition ELSE pc.idofgoodsrequestposition "
                            + "         END AS idofgoodsrequestposition ,"
                            + "   CASE WHEN (pc.amount = 0) THEN pmd.menudetailprice ELSE pc.complexprice "
                            + "         END AS price "
                            + "FROM cf_preorder_complex pc "
                            + "INNER JOIN cf_clients c ON c.idofclient = pc.idofclient "
                            + "INNER JOIN cf_complexinfo ci ON c.idoforg = ci.idoforg AND ci.menudate = pc.preorderdate "
                            + "   AND ci.idofcomplex = pc.armcomplexid "
                            + "LEFT JOIN cf_preorder_menudetail pmd ON pc.idofpreordercomplex = pmd.idofpreordercomplex "
                            + "LEFT JOIN cf_menu m ON c.idoforg = m.idoforg AND pmd.preorderdate = m.menudate "
                            + "LEFT JOIN cf_menudetails md ON m.idofmenu = md.idofmenu AND pmd.armidofmenu = md.localidofmenu "
                            + "LEFT JOIN cf_goods gc ON gc.idofgood = ci.idofgood "
                            + "LEFT JOIN cf_goods gmd ON gmd.idofgood = md.idofgood "
                            + "WHERE ci.idOfOrg IN (:orgList) AND ci.menuDate BETWEEN :startDate AND :endDate "
                            + "   AND (pc.deletedState = 0 OR pc.deletedState IS NULL) AND (pmd.deletedState = 0 OR pmd.deletedState IS NULL)";
            //and pc.deletedState = 0 and pmd.deletedState = 0";

            Query query = session.createSQLQuery(sqlQuery);
            query.setParameterList("orgList", orgMap.keySet());
            query.setParameter("startDate", beginDate.getTime());
            query.setParameter("endDate", endDate.getTime());

            List complexList = query.list();

            for (Object obj : complexList) {
                Object values[] = (Object[])obj;

                //globalId
                if (null == values[1])
                    continue;

                FeedingPlanType planType;
                final Long idOfOrg = ((BigInteger) values[0]).longValue();
                final Long globalId = ((BigInteger) values[1]).longValue();
                Boolean isComplex = (Boolean) values[2];
                final String goodsCode = (null != values[3]) ? values[3].toString() : "";
                final String menuDetailType = (null != values[4]) ? values[4].toString() : "";
                final Long idOfGoodsRequestPosition = (null !=  values[5]) ? ((BigInteger) values[5]).longValue() : null;
                final Long price = (null != values[6]) ? ((BigInteger) values[6]).longValue() : null;

                if (isComplex) {
                    planType = FeedingPlanType.COMPLEX;
                } else {
                    if (MENU_DETAIL_TYPE_BREAKFAST.equals(menuDetailType)) {
                        planType = FeedingPlanType.BREAKFAST;
                    } else if (MENU_DETAIL_TYPE_DINNER.equals(menuDetailType)) {
                        planType = FeedingPlanType.DINNER;
                    } else if (MENU_DETAIL_TYPE_AFTERNOON.equals(menuDetailType)) {
                        planType = FeedingPlanType.AFTERNOON;
                    } else if (MENU_DETAIL_TYPE_SUPPER.equals(menuDetailType)) {
                        planType = FeedingPlanType.SUPPER;
                    } else {
                        logger.info(String.format("GoodRequestsNewReportService: unexpected menu detail type was finded in org with id = %d", idOfOrg));
                        continue;
                    }
                }

                ComplexInfoItem infoItem = complexOrgDictionary.get(idOfOrg);
                GoodInfo info = new GoodInfo(globalId, "", planType, goodsCode, price);
                if (infoItem == null) {
                    infoItem = new ComplexInfoItem(idOfOrg);
                }
                if (!infoItem.goodInfos.containsKey(globalId)) {
                    infoItem.goodInfos.put(globalId, info);
                }
                infoItem.preorderInfo.put(idOfGoodsRequestPosition, info);
                complexOrgDictionary.put(idOfOrg, infoItem);
                allGoodsInfo.put(globalId, info);
            }
        }

        List<GoodRequestPosition> goodRequestPositionList = getGoodRequestPositions(nameFilter, generateEndTime,
                hideGeneratePeriod, false, orgMap, beginDate, endDate, preordersOnly, hidePreorders);
        if (notification) {
            List<GoodRequestPosition> goodRequestPositionListN = getGoodRequestPositions(nameFilter, generateEndTime,
                    hideGeneratePeriod, true, orgMap, beginDate, endDate, preordersOnly, hidePreorders);
            if (goodRequestPositionListN.size() > 0) {
                goodRequestPositionList.addAll(goodRequestPositionListN);
            }
        }
        Map<Long, GoodInfo> requestGoodsInfo = new HashMap<Long, GoodInfo>();
        for (GoodRequestPosition goodRequestPosition : goodRequestPositionList) {
            processPosition(hideDailySampleValue, generateBeginTime, generateEndTime, hideMissedColumns,
                    hideGeneratePeriod, hideLastValue, orgMap, itemList, beginDate, endDate, dates,
                    complexOrgDictionary, goodRequestPositionList, requestGoodsInfo, goodRequestPosition, notification,
                    needFullGoodNames);
        }

        if (orgFilter == 0 && !idOfMenuSourceOrgList.isEmpty()) {
            MultiValueMap fullNameProviderMap = new MultiValueMap();
            Criteria goodCriteria = session.createCriteria(Good.class);
            goodCriteria.add(Restrictions.in("orgOwner", idOfMenuSourceOrgList));
            if (StringUtils.isNotEmpty(nameFilter)) {
                goodCriteria.add(Restrictions.or(
                        Restrictions.ilike("fullName", nameFilter, MatchMode.ANYWHERE),
                        Restrictions.ilike("nameOfGood", nameFilter, MatchMode.ANYWHERE)));
            }
            goodCriteria.setProjection(
                    Projections.projectionList()
                            .add(Projections.property("fullName"))
                            .add(Projections.property("nameOfGood"))
                            .add(Projections.property("orgOwner"))
                            .add(Projections.property("globalId"))
                            .add(Projections.property("goodsCode"))
            );
            goodCriteria.setResultTransformer(Transformers.aliasToBean(Good.class));
            List<Good> goodNames = goodCriteria.list();
            for (Good good : goodNames) {
                final Long idOfOrg = good.getOrgOwner();
                final Long idOfGood = good.getGlobalId();
                final String goodsCode = good.getGoodsCode();
                String nameOfGood = StringUtils.isEmpty(good.getNameOfGood()) ?
                        good.getNameOfGood() : good.getFullName();
                if (!requestGoodsInfo.containsKey(idOfGood)) {
                    FeedingPlanType feedingPlanType = null;
                    Long price = null;
                    if (allGoodsInfo.containsKey(idOfGood)) {
                        feedingPlanType = allGoodsInfo.get(idOfGood).feedingPlanType;
                        price = allGoodsInfo.get(idOfGood).price;
                    } else {
                        continue;
                    }
                    fullNameProviderMap.put(idOfOrg, new GoodInfo(idOfGood, nameOfGood, feedingPlanType, goodsCode, price));
                }
            }
            for (BasicReportJob.OrgShortItem item : orgMap.values()) {
                if (item.getSourceMenuOrg() != null
                        && fullNameProviderMap.getCollection(item.getSourceMenuOrg()) != null) {
                    for (Object object : fullNameProviderMap.getCollection(item.getSourceMenuOrg())) {
                        GoodInfo goodInfo = (GoodInfo) object;
                        if (hideMissedColumns) {
                            for (Date date : dates) {
                                addItemsFromList(itemList, item, date, goodInfo.name, hideDailySampleValue,
                                        hideLastValue, goodInfo.feedingPlanType, 0L, goodInfo.goodsCode, goodInfo.price,
                                        goodInfo.feedingPlanType.getTotalString());
                            }
                        } else {
                            beginDate = CalendarUtils.truncateToDayOfMonth(startTime);
                            endDate = CalendarUtils.endOfDay(endTime);
                            while (beginDate.getTime() <= endDate.getTime()) {
                                addItemsFromList(itemList, item, beginDate, goodInfo.name, hideDailySampleValue,
                                        hideLastValue, goodInfo.feedingPlanType, 0L, goodInfo.goodsCode, goodInfo.price,
                                        goodInfo.feedingPlanType.getTotalString());
                                beginDate = CalendarUtils.addOneDay(beginDate);

                            }
                        }
                    }
                }
            }
        }

        if (itemList.isEmpty() && !hideTotalRow) {
            for (BasicReportJob.OrgShortItem item : orgMap.values()) {
                itemList.add(new Item(item, "", CalendarUtils.truncateToDayOfMonth(startTime), hideDailySampleValue,
                        hideLastValue, null, 0L, "", 0L, ""));
            }
            itemList.add(new Item(OVERALL, OVERALL_TITLE, "", CalendarUtils.truncateToDayOfMonth(startTime),
                    hideDailySampleValue, hideLastValue, null, 0L, null, "",
                    "", 0L, ""));
        }

        if(orgFilter == 0){
            buildReportItemsWithoutData(itemList, orgMap, startTime, hideDailySampleValue, hideLastValue);
        }
        return itemList;
    }

    private void buildReportItemsWithoutData(List<Item> itemList, HashMap<Long, BasicReportJob.OrgShortItem> orgMap,
            Date startTime, int hideDailySampleValue, int hideLastValue) {
        for(BasicReportJob.OrgShortItem orgItem : orgMap.values()){
            boolean findCurrentOrg = false;
            for(Item dataItem : itemList){
                Long idOfOrgFromItem = dataItem.getIdOfOrg();
                if(idOfOrgFromItem != null && idOfOrgFromItem.equals(orgItem.getIdOfOrg())){
                    findCurrentOrg = true;
                    break;
                }
            }
            if(!findCurrentOrg){
                Item newItem = new Item(orgItem, "", CalendarUtils.truncateToDayOfMonth(startTime), hideDailySampleValue,
                        hideLastValue, null, 0L, "", 0L, "");
                itemList.add(newItem);
            }
        }
    }

    public List<Item> buildReportItems(Date startTime, Date endTime, String nameFilter, int orgFilter,
            int hideDailySampleValue, Date generateBeginTime, Date generateEndTime, List<Long> idOfOrgList,
            List<Long> idOfMenuSourceOrgList, boolean hideMissedColumns, boolean hideGeneratePeriod, int hideLastValue,
            boolean notification) {

        return buildReportItems(startTime, endTime, nameFilter, orgFilter, hideDailySampleValue, generateBeginTime,
                generateEndTime, idOfOrgList, idOfMenuSourceOrgList, hideMissedColumns, hideGeneratePeriod,
                hideLastValue, notification, false, false, true);
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
                Projections.projectionList()
                        .add(Projections.property("idOfOrg"))
                        .add(Projections.property("shortName"))
                        .add(Projections.property("officialName"))
                        .add(Projections.property("sm.idOfOrg"))
                        .add(Projections.property("address")));
        List orgList = orgCriteria.list();

        HashMap<Long, BasicReportJob.OrgShortItem> orgMap = new HashMap<Long, BasicReportJob.OrgShortItem>(orgList.size());
        for (Object obj : orgList) {
            Object[] row = (Object[]) obj;
            long idOfOrg = (Long) row[0];
            BasicReportJob.OrgShortItem educationItem;
            educationItem = new BasicReportJob.OrgShortItem(idOfOrg, row[1].toString(), row[2].toString());
            if (row[3] != null) {
                Long sourceMenuOrg = (Long) row[3];
                educationItem.setSourceMenuOrg(sourceMenuOrg);
                idOfMenuSourceOrgList.add(sourceMenuOrg);
            }
            if (null != row[4]) {
                educationItem.setAddress(row[4].toString());
            }
            orgMap.put(idOfOrg, educationItem);
        }
        return orgMap;
    }

    private void processPosition(int hideDailySampleValue, Date generateBeginTime, Date generateEndTime,
            boolean hideMissedColumns, boolean hideGeneratePeriod, int hideLastValue,
            HashMap<Long, BasicReportJob.OrgShortItem> orgMap, List<Item> itemList, Date beginDate, Date endDate,
            TreeSet<Date> dates, Map<Long, ComplexInfoItem> complexOrgDictionary, List goodRequestPositionList,
            Map<Long, GoodInfo> requestGoodsInfo, GoodRequestPosition position, boolean notification, boolean needFullGoodNames) {
        Date doneDate;
        BasicReportJob.OrgShortItem org = orgMap.get(position.getOrgOwner());

        Long totalCount = position.getTotalCount() / 1000;
        Long dailySampleCount = getSafeValue(position.getDailySampleCount()) / 1000L;
        Long tempClientsCount = getSafeValue(position.getTempClientsCount()) / 1000L;

        Long newTotalCount = 0L;
        Long newDailySample = 0L;
        Long newTempClients = 0L;

        if (hideGeneratePeriod) {
            Date createDate = position.getCreatedDate();
            if (CalendarUtils.betweenDate(createDate, generateBeginTime, generateEndTime)) {
                newTotalCount = totalCount;
                newDailySample = dailySampleCount;
                newTempClients = tempClientsCount;
            }

            Date lastDate = position.getLastUpdate();
            if (lastDate != null) {
                if (CalendarUtils.betweenDate(lastDate, generateBeginTime, generateEndTime)) {
                    newTotalCount = totalCount - getSafeValue(position.getLastTotalCount()) / 1000L;
                    newDailySample = dailySampleCount - getSafeValue(position.getLastDailySampleCount()) / 1000L;
                    newTempClients = tempClientsCount - getSafeValue(position.getLastTempClientsCount()) / 1000L;
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
        Long price = null;
        if (complexOrgDictionary.containsKey(position.getOrgOwner())) {
            ComplexInfoItem complexInfoItem = complexOrgDictionary.get(position.getOrgOwner());
            if (complexInfoItem.preorderInfo.containsKey(position.getGlobalId())) {
                GoodInfo info = complexInfoItem.preorderInfo.get(position.getGlobalId());
                feedingPlanType = info.feedingPlanType;
                price = info.price;
            } else if (complexInfoItem.goodInfos.containsKey(good.getGlobalId())) {
                GoodInfo info = complexInfoItem.goodInfos.get(good.getGlobalId());
                feedingPlanType = info.feedingPlanType;
                price = info.price;
            } else {
                feedingPlanType = FeedingPlanType.PAY_PLAN;
                price = 0L;
            }
        } else {
            feedingPlanType = FeedingPlanType.PAY_PLAN;
            price = 0L;
        }
        String name = "";
        if (needFullGoodNames) {
            name = good.getFullName();
        }

        if (!needFullGoodNames || StringUtils.isEmpty(name)) {
            name = good.getNameOfGood();
        }
        String goodsCode = good.getGoodsCode();
        if (!requestGoodsInfo.containsKey(good.getGlobalId())) {
            requestGoodsInfo.put(good.getGlobalId(), new GoodInfo(good.getGlobalId(), name, feedingPlanType, goodsCode, price));
        }
        // чтобы хотя бы раз выполнилмся, для уведомлений
        if (!hideMissedColumns && hideTotalRow && goodRequestPositionList.indexOf(position) == 0) {
            while (beginDate.getTime() <= endDate.getTime()) {
                itemList.add(new Item(org, name, beginDate, 0L, 0L, 0L, 0L, 0L, 0L, hideDailySampleValue, hideLastValue,
                        feedingPlanType, 0L, "", price, null));
                dates.add(beginDate);
                beginDate = CalendarUtils.addOneDay(beginDate);
            }
        }

        addItemsFromList(itemList, org, doneDate, name, totalCount, dailySampleCount, tempClientsCount, newTotalCount, newDailySample, newTempClients,
                hideDailySampleValue, hideLastValue, feedingPlanType, notificationMark, goodsCode, price, feedingPlanType.getTotalString());
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
            Date beginDate, Date endDate, boolean preordersOnly, boolean hidePreorders) {
        Criteria criteria = session.createCriteria(GoodRequestPosition.class);
        criteria.createAlias("goodRequest", "gr");
        criteria.add(Restrictions.between("gr.doneDate", beginDate, endDate));
        criteria.add(Restrictions.in("gr.orgOwner", orgMap.keySet()));
        criteria.add(Restrictions.isNotNull("good"));

        //Не показываем заявки, которые сохранены, но были отклонены по причине неверной даты (у них GlobalVersion = 0)
        criteria.add(Restrictions.not(Restrictions.eq("globalVersion", 0L)));
        criteria.add(Restrictions.not(Restrictions.eq("gr.globalVersion", 0L)));

        if (preordersOnly) {
            criteria.add(Restrictions.eq("gr.comment", PREORDER_COMMENT));
        }
        if (hidePreorders) {
            criteria.add(Restrictions.ne("gr.comment", PREORDER_COMMENT));
        }

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
            int hideDailySampleValue, int hideLastValue, FeedingPlanType feedingPlanType, Long notificationMark,
            String goodsCode, Long price, String totalPlanStr) {
        itemList.add(
                new Item(org, name, doneDate, hideDailySampleValue, hideLastValue, feedingPlanType, notificationMark, goodsCode, price, totalPlanStr));
        if (!hideTotalRow) {
            itemList.add(new Item(OVERALL, OVERALL_TITLE, name, doneDate, hideDailySampleValue, hideLastValue,
                    feedingPlanType, notificationMark, null, "", goodsCode, price, totalPlanStr));
        }
    }

    private void addItemsFromList(List<Item> itemList, BasicReportJob.OrgShortItem org, Date doneDate, String name,
            Long totalCount, Long dailySampleCount, Long tempClientsCount, Long newTotalCount, Long newDailySample, Long newTempClients, int hideDailySampleValue,
            int hideLastValue, FeedingPlanType feedingPlanType, Long notificationMark, String goodsCode, Long price, String totalPlanStr) {
        itemList.add(new Item(org, name, doneDate, totalCount, dailySampleCount, tempClientsCount, newTotalCount, newDailySample, newTempClients,
                hideDailySampleValue, hideLastValue, feedingPlanType, notificationMark, goodsCode, price, totalPlanStr));
        if (!hideTotalRow) {
            itemList.add(new Item(OVERALL, OVERALL_TITLE, name, doneDate, totalCount, dailySampleCount, tempClientsCount, newTotalCount,
                    newDailySample, newTempClients, hideDailySampleValue, hideLastValue, feedingPlanType, 0L,
                    null, "", goodsCode, price, totalPlanStr));
        }
    }

    public enum FeedingPlanType {
        /*0*/ REDUCED_PRICE_PLAN("Льготное питание", "льготному питанию"),
        /*1*/ PAY_PLAN("Платное питание", "платному питанию"),
        /*2*/ SUBSCRIPTION_FEEDING("Абонементное питание", "абонементному питанию"),
        /*3*/ COMPLEX("Вариативное платное питание. Комплексы", "вариативному платному питанию"),
        /*4*/ DISH("Вариативное платное питание. Отдельные блюда", "вариативному платному питанию"),
        /*5*/ BREAKFAST("Завтрак (перечень блюд на выбор)", "вариативному платному питанию"),
        /*6*/ DINNER("Обед (перечень блюд на выбор)", "вариативному платному питанию"),
        /*7*/ AFTERNOON("Полдник (перечень блюд на выбор)", "вариативному платному питанию"),
        /*8*/ SUPPER("Ужин (перечень блюд на выбор)", "вариативному платному питанию");

        private String name;
        private String totalString;

        FeedingPlanType(String name, String totalString) {
            this.name = name;
            this.totalString = totalString;
        }

        public String getName() {
            return name;
        }

        public String getTotalString() {
            return totalString;
        }
    }

    public static class ComplexInfoItem {

        final long idOfOrg;
        final Map<Long, GoodInfo> goodInfos = new HashMap<Long, GoodInfo>();
        final Map<Long, GoodInfo> preorderInfo = new HashMap<Long, GoodInfo>();

        public ComplexInfoItem(long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

    }

    public static class GoodInfo {

        final long idOfGood;
        final String name;
        final FeedingPlanType feedingPlanType;
        final String goodsCode;
        final Long price;

        public GoodInfo(long idOfGood, String name, FeedingPlanType feedingPlanType, String goodsCode, Long price) {
            this.idOfGood = idOfGood;
            this.name = name;
            this.feedingPlanType = feedingPlanType;
            this.goodsCode = goodsCode;
            this.price = price;
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
        private Long newTempClients;
        private Long notificationMark;
        private Long needToMark;
        private Long idOfOrg;
        private String address;
        private String goodsCode;
        private String totalPlanStr;
        private Long price;

        protected Item(Item item, Date doneDate) {
            this(item.getOrgNum(), item.getOfficialName(), item.getGoodName(), doneDate, 0L, 0L, 0L, 0L, 0L, 0L,
                    item.getHideDailySample(), item.getHideLastValue(), item.getFeedingPlanType(),
                    item.getNotificationMark(), item.getIdOfOrg(), item.getAddress(), item.getGoodsCode(), item.getPrice(),
                    item.getTotalPlanStr());
        }

        public Item(String orgNum, String officialName, String goodName, Date doneDate, Long totalCount, Long dailySample,
                Long tempClients, Long newTotalCount, Long newDailySample, Long newTempClients, int hideDailySampleValue,
                int hideLastValue, FeedingPlanType feedingPlanType, Long notificationMark, Long idOfOrg, String address,
                String goodsCode, Long price, String totalPlanStr) {
            this(orgNum, officialName, goodName, doneDate, totalCount, dailySample, tempClients, newTotalCount,
                    newDailySample, newTempClients, hideDailySampleValue, hideLastValue, feedingPlanType, notificationMark,
                    0L, idOfOrg, address, goodsCode, price, totalPlanStr);
        }

        public Item(String orgNum, String officialName, String goodName, Date doneDate, Long totalCount, Long dailySample,
                Long tempClients, Long newTotalCount, Long newDailySample, Long newTempClients, int hideDailySampleValue,
                int hideLastValue, FeedingPlanType feedingPlanType, Long notificationMark, Long needToMark, Long idOfOrg,
                String address, String goodsCode, Long price, String totalPlanStr) {
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
            this.feedingPlanType = feedingPlanType;
            if (feedingPlanType == null) {
                feedingPlanTypeStr = "";
                feedingPlanTypeNum = -1;
            } else {
                feedingPlanTypeStr = feedingPlanType.getName();
                feedingPlanTypeNum = feedingPlanType.ordinal();
            }
            this.notificationMark = notificationMark;
            this.needToMark = needToMark;
            this.idOfOrg = idOfOrg;
            this.address = address;
            this.goodsCode = goodsCode;
            this.totalPlanStr = totalPlanStr;
            this.price = price;
        }

        public Item(String orgNum, String officialName, String goodName, Date doneDate, int hideDailySampleValue,
                int hideLastValue, FeedingPlanType feedingPlanType, Long notificationMark, Long needToMark, Long idOfOrg,
                String address, String goodsCode, Long price, String totalPlanStr) {
            this(orgNum, officialName, goodName, doneDate, 0L, 0L, 0L, 0L, 0L, 0L, hideDailySampleValue,
                    hideLastValue, feedingPlanType, notificationMark, needToMark, idOfOrg, address, goodsCode, price, totalPlanStr);
        }

        public Item(String orgNum, String officialName, String goodName, Date doneDate, int hideDailySampleValue,
                int hideLastValue, FeedingPlanType feedingPlanType, Long notificationMark, Long idOfOrg, String address,
                String goodsCode, Long price, String totalPlanStr) {
            this(orgNum, officialName, goodName, doneDate, 0L, 0L, 0L, 0L, 0L, 0L, hideDailySampleValue,
                    hideLastValue, feedingPlanType, notificationMark, idOfOrg, address, goodsCode, price, totalPlanStr);
        }

        public Item(BasicReportJob.OrgShortItem item, String goodName, Date doneDate, Long totalCount, Long dailySample, Long tempClients,
                Long newTotalCount, Long newDailySample, Long newTempClients, int hideDailySampleValue, int hideLastValue,
                FeedingPlanType feedingPlanType, Long notificationMark, Long needToMark, String goodsCode, Long price, String totalPlanStr) {
            this(Org.extractOrgNumberFromName(item.getOfficialName()), item.getShortName(), goodName, doneDate,
                    totalCount, dailySample, tempClients, newTotalCount, newDailySample, newTempClients, hideDailySampleValue, hideLastValue,
                    feedingPlanType, notificationMark, needToMark, item.getIdOfOrg(), item.getAddress(), goodsCode, price, totalPlanStr);

        }

        public Item(BasicReportJob.OrgShortItem item, String goodName, Date doneDate, Long totalCount, Long dailySample, Long tempClients,
                Long newTotalCount, Long newDailySample, Long newTempClients, int hideDailySampleValue, int hideLastValue,
                FeedingPlanType feedingPlanType, Long notificationMark, String goodsCode, Long price, String totalPlanStr) {
            this(item, goodName, doneDate, totalCount, dailySample, tempClients, newTotalCount, newDailySample, newTempClients,
                    hideDailySampleValue, hideLastValue, feedingPlanType, notificationMark, 0L, goodsCode, price, totalPlanStr);

        }

        public Item(BasicReportJob.OrgShortItem item, String goodName, Date doneDate, int hideDailySampleValue,
                int hideLastValue, FeedingPlanType feedingPlanType, Long notificationMark, String goodsCode, Long price,
                String totalPlanStr) {
            this(item, goodName, doneDate, 0L, 0L, 0L, 0L, 0L, 0L,
                    hideDailySampleValue, hideLastValue, feedingPlanType, notificationMark, goodsCode, price, totalPlanStr);
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

        public Long getNewTempClients() {
            return newTempClients;
        }

        public void setNewTempClients(Long newTempClients) {
            this.newTempClients = newTempClients;
        }

        public Long getNeedToMark() {
            return needToMark;
        }

        public void setNeedToMark(Long needToMark) {
            this.needToMark = needToMark;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(Long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getGoodsCode() {
            return goodsCode;
        }

        public void setGoodsCode(String goodsCode) {
            this.goodsCode = goodsCode;
        }

        public String getTotalPlanStr() {
            return totalPlanStr;
        }

        public void setTotalPlanStr(String totalPlanStr) {
            this.totalPlanStr = totalPlanStr;
        }

        public Long getPrice() {
            return price;
        }

        public void setPrice(Long price) {
            this.price = price;
        }
    }
}
