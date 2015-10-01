/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.sfk.adjustmentpayment;

import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DetailedDeviationsWithoutCorpsService;
import ru.axetta.ecafe.processor.core.persistence.utils.FriendlyOrganizationsInfoModel;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.ComplexInfoItem;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.PlanOrderItem;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 28.09.15
 * Time: 15:21
 */

public class AdjustmentPaymentReportService {

    private static final Logger logger = LoggerFactory.getLogger(AdjustmentPaymentReportBuilder.class);

    public AdjustmentPaymentReportService() {
    }

    // Получение саедений об организации
    public AdjustmentPaymentReportModel getNumAndAddressByOrg(Session session, Long idOfOrg) {
        Query query = session.createSQLQuery(
                "SELECT substring(shortname FROM 'Y*([0-9-]+)') orgnum, address FROM cf_orgs WHERE idoforg = :idOfOrg");
        query.setParameter("idOfOrg", idOfOrg);

        List resultList = query.list();

        String orgnum;
        String address;

        AdjustmentPaymentReportModel adjustmentPaymentReportModel = new AdjustmentPaymentReportModel();

        for (Object res : resultList) {
            Object[] result = (Object[]) res;

            orgnum = (String) result[0];
            address = (String) result[1];

            adjustmentPaymentReportModel.setNum(1L);
            adjustmentPaymentReportModel.setOrgnum(orgnum);
            adjustmentPaymentReportModel.setAddress(address);
        }

        return adjustmentPaymentReportModel;
    }

    //Проход по карте зафиксирован, питание не предоставлено (льготники, чел.) - passage
    //Проход по карте не зафиксирован, питание предоставлено (льготники, чел) - food
    //Льготное питание предоставлено резервникам (чел.) - reserve
    public AdjustmentPaymentReportModel getPassageFoodReserveCount(Session session,
            AdjustmentPaymentReportModel adjustmentPaymentReportModel, String orderTypeLgotnick, Date startTime,
            Date endTime, List<Long> idOfOrgList, HashMap<Long, List<DiscountRule>> rulesForOrgMap,
            HashMap<Long, List<ComplexInfoItem>> complexInfoItemListByPlanMap, List<Long> onlyPaidCategories) {

        List<PlanOrderItem> resultSubtractionInterval = new ArrayList<PlanOrderItem>(); // Разность за интервал
        List<PlanOrderItem> resultIntersectionInterval = new ArrayList<PlanOrderItem>(); // Пересечение за интервал

        // Те кто дожны были получить бесплатное питание | Проход по карте зафиксирован - за интервал
        List<PlanOrderItem> planOrderItemsToPayDetectedInterval = new ArrayList<PlanOrderItem>();

        // Те кто дожны были получить бесплатное питание | Проход по карте не зафиксирован - за интервал
        List<PlanOrderItem> planOrderItemsToPayNotDetectedInterval = new ArrayList<PlanOrderItem>();

        // Оплаченные заказы за интервал
        List<PlanOrderItem> planOrderItemsPaidByInterval = DetailedDeviationsWithoutCorpsService
                .loadPaidPlanOrderInfo(session, orderTypeLgotnick, idOfOrgList, startTime, endTime);

        // План льготного питания, резерв - ordertype = 6
        List<PlanOrderItem> planOrderItemsReserveByInterval = DetailedDeviationsWithoutCorpsService.
                loadPaidPlanOrderInfo(session, "6", idOfOrgList, startTime, endTime);

        List<PlanOrderItem> planOrderItemList = planOrderItemsPaidByInterval;

        List<Long> idOfClientsList = new ArrayList<Long>();

        for (PlanOrderItem planOrderItem : planOrderItemList) {
            idOfClientsList.add(planOrderItem.getIdOfClient());
        }

        Date sTt = startTime;
        CalendarUtils.truncateToDayOfMonth(sTt);

        Date eTt = CalendarUtils.addOneDay(startTime);
        CalendarUtils.truncateToDayOfMonth(eTt);

        // План по тем кто отметился в здании за интервал
        List<PlanOrderItem> planOrderItemToPayDetectedIntervalList = DetailedDeviationsWithoutCorpsService
                .loadPlanOrderItemToPayDetected(session, sTt, eTt, idOfOrgList, rulesForOrgMap,
                        complexInfoItemListByPlanMap, onlyPaidCategories);

        if (planOrderItemToPayDetectedIntervalList != null) {
            planOrderItemsToPayDetectedInterval.addAll(planOrderItemToPayDetectedIntervalList);
        }
        // План по тем кто не в здании за интервал
        List<PlanOrderItem> planOrderItemToPayNotDetectedIntervalList = DetailedDeviationsWithoutCorpsService
                .loadPlanOrderItemToPayNotDetected(session, sTt, eTt, idOfOrgList, idOfClientsList, rulesForOrgMap,
                        complexInfoItemListByPlanMap, onlyPaidCategories);

        if (planOrderItemToPayNotDetectedIntervalList != null) {
            planOrderItemsToPayNotDetectedInterval.addAll(planOrderItemToPayNotDetectedIntervalList);
        }

        Date nextDateEndTime = CalendarUtils.addOneDay(endTime);
        CalendarUtils.truncateToDayOfMonth(nextDateEndTime);

        while (eTt.before(CalendarUtils.truncateToDayOfMonth(nextDateEndTime))) {
            sTt = CalendarUtils.addOneDay(sTt);
            eTt = CalendarUtils.addOneDay(eTt);
            planOrderItemToPayDetectedIntervalList = DetailedDeviationsWithoutCorpsService
                    .loadPlanOrderItemToPayDetected(session, sTt, eTt, idOfOrgList, rulesForOrgMap,
                            complexInfoItemListByPlanMap, onlyPaidCategories);
            if (planOrderItemToPayDetectedIntervalList != null) {
                planOrderItemsToPayDetectedInterval.addAll(planOrderItemToPayDetectedIntervalList);
            }
            // План по тем кто не в здании
            planOrderItemToPayNotDetectedIntervalList = DetailedDeviationsWithoutCorpsService
                    .loadPlanOrderItemToPayNotDetected(session, sTt, eTt, idOfOrgList, idOfClientsList, rulesForOrgMap,
                            complexInfoItemListByPlanMap, onlyPaidCategories);
            if (planOrderItemToPayNotDetectedIntervalList != null) {
                planOrderItemsToPayNotDetectedInterval.addAll(planOrderItemToPayNotDetectedIntervalList);
            }
        }
        if (!planOrderItemsToPayDetectedInterval.isEmpty()) {
            for (PlanOrderItem planOrderItem : planOrderItemsToPayDetectedInterval) {
                if (!planOrderItemsPaidByInterval.contains(planOrderItem)) {
                    resultSubtractionInterval.add(planOrderItem);
                }
            }
        }
        if (!planOrderItemsToPayNotDetectedInterval.isEmpty()) {
            for (PlanOrderItem planOrderItem : planOrderItemsPaidByInterval) {
                if (planOrderItemsToPayNotDetectedInterval.contains(planOrderItem)) {
                    resultIntersectionInterval.add(planOrderItem);
                }
            }
        }

        adjustmentPaymentReportModel.setFood(Long.valueOf(resultIntersectionInterval.size()));
        adjustmentPaymentReportModel.setPassage(Long.valueOf(resultSubtractionInterval.size()));
        adjustmentPaymentReportModel.setReserve(Long.valueOf(planOrderItemsReserveByInterval.size()));

        return adjustmentPaymentReportModel;
    }

    public AdjustmentPaymentReportModel getMainData(Session session, Date startDate, Date endDate,
            FriendlyOrganizationsInfoModel friendlyOrganizationsInfoModel, Long idOfOrg) {

        //Результирующие данные для отчета
        AdjustmentPaymentReportModel adjustmentPaymentReportModel = getNumAndAddressByOrg(session, idOfOrg);

        Set<Org> orgSet = friendlyOrganizationsInfoModel.getFriendlyOrganizationsSet();

        List<Long> idOfOrgList = new ArrayList<Long>();
        if (orgSet.isEmpty()) {
            idOfOrgList.add(friendlyOrganizationsInfoModel.getIdOfOrg());
        } else {
            for (Org org : orgSet) {
                idOfOrgList.add(org.getIdOfOrg());
            }
        }

        // План питания льготники
        String orderTypeLgotnick = "4,8"; // 6 - резерв

        HashMap<Long, List<DiscountRule>> rulesForOrgMap = new HashMap<Long, List<DiscountRule>>();
        HashMap<Long, List<ComplexInfoItem>> complexInfoItemListByPlanMap = new HashMap<Long, List<ComplexInfoItem>>();

        for (Long idOfOrg1 : idOfOrgList) {
            // правила для организации
            List<DiscountRule> rulesForOrg = DetailedDeviationsWithoutCorpsService
                    .getDiscountRulesByOrg(session, idOfOrg1);
            rulesForOrgMap.put(idOfOrg1, rulesForOrg);

            List<ComplexInfoItem> complexInfoItemListByPlan = DetailedDeviationsWithoutCorpsService
                    .loadComplexNameByPlan(session, idOfOrg1, startDate, endDate);
            complexInfoItemListByPlanMap.put(idOfOrg1, complexInfoItemListByPlan);
        }

        // платные категории
        List<Long> onlyPaidCategories = DetailedDeviationsWithoutCorpsService.loadAllPaydAbleCategories(session);

        getPassageFoodReserveCount(session, adjustmentPaymentReportModel, orderTypeLgotnick, startDate, endDate,
                idOfOrgList, rulesForOrgMap, complexInfoItemListByPlanMap, onlyPaidCategories);

        return adjustmentPaymentReportModel;
    }
}
