/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.without.corps;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.core.persistence.OrderTypeEnumType;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DetailedDeviationsWithoutCorpsService;
import ru.axetta.ecafe.processor.core.persistence.utils.FriendlyOrganizationsInfoModel;
import ru.axetta.ecafe.processor.core.persistence.utils.OrgUtils;
import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.ComplexInfoItem;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.DeviationPaymentNewItem;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.DeviationPaymentNewSubReportItem;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.PlanOrderItem;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 03.03.15
 * Time: 12:12
 */

public class DetailedDeviationsWithoutCorpsNewBuilder extends BasicReportForAllOrgJob.Builder {

    private final String templateFilename;
    private final String subReportDir;
    private final String templateFileNameInterval;

    public DetailedDeviationsWithoutCorpsNewBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
        subReportDir = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath();
        templateFileNameInterval = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath()
                + "DetailedDeviationsWithoutCorpsNewIntervalJasperReport.jasper";
    }

    @Override
    public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        if (!(new File(this.templateFilename)).exists()) {
            throw new Exception(String.format("Не найден файл шаблона '%s'", templateFilename));
        }
        String idOfOrg = StringUtils.trimToEmpty(getReportProperties().getProperty(ReportPropertiesUtils.P_ID_OF_ORG));

        List<Long> idOfOrgList = new ArrayList<Long>();
        idOfOrgList.add(Long.parseLong(idOfOrg));

        Set<FriendlyOrganizationsInfoModel> friendlyOrganizationsInfoModels = OrgUtils
                .getMainBuildingAndFriendlyOrgsList(session, idOfOrgList);

        Date generateBeginTime = new Date();

        /* Параметры для передачи в jasper */
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("beginDate", CalendarUtils.dateToString(startTime));
        parameterMap.put("endDate", CalendarUtils.dateToString(endTime));
        parameterMap.put("IS_IGNORE_PAGINATION", true);
        parameterMap.put("SUBREPORT_DIR", subReportDir);
        JRDataSource dataSource = buildDataSource(session, friendlyOrganizationsInfoModels, idOfOrgList, startTime,
                endTime);
        JasperPrint jasperPrint;
        if (CalendarUtils.truncateToDayOfMonth(startTime).equals(CalendarUtils.truncateToDayOfMonth(endTime))) {
            jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
        } else {
            jasperPrint = JasperFillManager.fillReport(templateFileNameInterval, parameterMap, dataSource);
        }
        Date generateEndTime = new Date();
        final long generateDuration = generateEndTime.getTime() - generateBeginTime.getTime();
        return new DetailedDeviationsWithoutCorpsNewJasperReport(generateBeginTime, generateDuration, jasperPrint,
                startTime, endTime);
    }

    private String getOrderTypes() {
        return OrderTypeEnumType.REDUCED_PRICE_PLAN.ordinal() + ", " + OrderTypeEnumType.CORRECTION_TYPE.ordinal();
    }

    private String getOrderTypeReserve() {
        return OrderTypeEnumType.REDUCED_PRICE_PLAN_RESERVE.ordinal() + ", " + OrderTypeEnumType.DISCOUNT_PLAN_CHANGE.ordinal();
    }

    private String getOrderTypeRecycle() {
        return "" + OrderTypeEnumType.RECYCLING_RETIONS.ordinal();
    }

    private JRDataSource buildDataSource(Session session,
            Set<FriendlyOrganizationsInfoModel> friendlyOrganizationsInfoModels, List<Long> selectedIdOfOrg,
            Date startTime, Date endTime) {

        //Результирующий лист по которому строиться отчет
        List<DeviationPaymentNewItem> deviationPaymentNewItemList = new ArrayList<DeviationPaymentNewItem>();

        // План питания льготники
        String orderTypeLgotnick = getOrderTypes();

        String conditionDetectedNotEat = "Проход по карте зафиксирован, питание не предоставлено";
        String conditionNotDetectedEat = "Проход по карте не зафиксирован, питание предоставлено";
        String conditionReserve = "Обучающиеся из группы резерва, получившие питание. Обучающиеся, получившие питание по функционалу замены";
        String conditionRecycle = "Утилизированное питание";

        HashMap<Long, List<DiscountRule>> rulesForOrgMap = new HashMap<Long, List<DiscountRule>>();
        HashMap<Long, List<ComplexInfoItem>> complexInfoItemListByPlanMap = new HashMap<Long, List<ComplexInfoItem>>();

        // платные категории
        List<Long> onlyPaidCategories = DetailedDeviationsWithoutCorpsService.loadAllPaydAbleCategories(session);

        Org selectedOrg = (Org) session.load(Org.class, selectedIdOfOrg.get(0));

        if (CalendarUtils.truncateToDayOfMonth(startTime).equals(CalendarUtils.truncateToDayOfMonth(endTime))) {

            Date addOneDayEndTime = CalendarUtils.addOneDay(startTime);
            CalendarUtils.truncateToDayOfMonth(addOneDayEndTime);

            Long rowNum = 0L;

            for (FriendlyOrganizationsInfoModel organizationsInfoModel : friendlyOrganizationsInfoModels) {
                Set<Org> orgSet = organizationsInfoModel.getFriendlyOrganizationsSet();
                ++rowNum;

                List<Long> idOfOrgList = new ArrayList<Long>();
                if (orgSet.isEmpty()) {
                    idOfOrgList.add(organizationsInfoModel.getIdOfOrg());
                } else {
                    for (Org org : orgSet) {
                        idOfOrgList.add(org.getIdOfOrg());
                    }
                }

                for (Long idOfOrg : idOfOrgList) {
                    // правила для организации
                    List<DiscountRule> rulesForOrg = DetailedDeviationsWithoutCorpsService
                            .getDiscountRulesByOrg(session, idOfOrg);
                    rulesForOrgMap.put(idOfOrg, rulesForOrg);

                    List<ComplexInfoItem> complexInfoItemListByPlan = DetailedDeviationsWithoutCorpsService
                            .loadComplexNameByPlan(session, idOfOrg, startTime, endTime);
                    complexInfoItemListByPlanMap.put(idOfOrg, complexInfoItemListByPlan);
                }

                DeviationPaymentNewItem deviationPaymentNewItem = new DeviationPaymentNewItem();
                List<DeviationPaymentNewSubReportItem> deviationPaymentSubReportItemList = new ArrayList<DeviationPaymentNewSubReportItem>();

                collectingReportItems(session, orderTypeLgotnick, startTime, addOneDayEndTime, conditionDetectedNotEat,
                        conditionNotDetectedEat, conditionReserve, conditionRecycle, idOfOrgList, deviationPaymentSubReportItemList,
                        rulesForOrgMap, complexInfoItemListByPlanMap, onlyPaidCategories);

                if (!deviationPaymentSubReportItemList.isEmpty()) {


                    deviationPaymentNewItem.setOrgName(selectedOrg.getShortName());
                    deviationPaymentNewItem.setAddress(selectedOrg.getAddress());
                    deviationPaymentNewItem.setRowNum(rowNum);
                    deviationPaymentNewItem.setDeviationPaymentNewSubReportItemList(deviationPaymentSubReportItemList);
                    deviationPaymentNewItemList.add(deviationPaymentNewItem);
                }
            }
        } else {
            Long rowNum = 0L;

            for (FriendlyOrganizationsInfoModel organizationsInfoModel : friendlyOrganizationsInfoModels) {
                Set<Org> orgSet = organizationsInfoModel.getFriendlyOrganizationsSet();
                ++rowNum;
                List<Long> idOfOrgList = new ArrayList<Long>();
                if (orgSet.isEmpty()) {
                    idOfOrgList.add(organizationsInfoModel.getIdOfOrg());
                } else {
                    for (Org org : orgSet) {
                        idOfOrgList.add(org.getIdOfOrg());
                    }
                }

                for (Long idOfOrg : idOfOrgList) {
                    // правила для организации
                    List<DiscountRule> rulesForOrg = DetailedDeviationsWithoutCorpsService
                            .getDiscountRulesByOrg(session, idOfOrg);
                    rulesForOrgMap.put(idOfOrg, rulesForOrg);

                    List<ComplexInfoItem> complexInfoItemListByPlan = DetailedDeviationsWithoutCorpsService
                            .loadComplexNameByPlan(session, idOfOrg, startTime, endTime);
                    complexInfoItemListByPlanMap.put(idOfOrg, complexInfoItemListByPlan);
                }

                DeviationPaymentNewItem deviationPaymentNewItem = new DeviationPaymentNewItem();
                List<DeviationPaymentNewSubReportItem> deviationPaymentSubReportItemList = new ArrayList<DeviationPaymentNewSubReportItem>();

                collectingReportItemsInterval(session, orderTypeLgotnick, startTime, endTime, conditionDetectedNotEat,
                        conditionNotDetectedEat, conditionReserve, conditionRecycle, deviationPaymentSubReportItemList, idOfOrgList,
                        rulesForOrgMap, complexInfoItemListByPlanMap, onlyPaidCategories);

                if (!deviationPaymentSubReportItemList.isEmpty()) {

                    deviationPaymentNewItem.setOrgName(selectedOrg.getShortName());
                    deviationPaymentNewItem.setAddress(selectedOrg.getAddress());

                    deviationPaymentNewItem.setRowNum(rowNum);
                    deviationPaymentNewItem.setDeviationPaymentNewSubReportItemList(deviationPaymentSubReportItemList);
                    deviationPaymentNewItemList.add(deviationPaymentNewItem);
                }
            }
        }

        for (DeviationPaymentNewItem dev : deviationPaymentNewItemList) {
            Collections.sort(dev.getDeviationPaymentNewSubReportItemList(), Collections.reverseOrder());
        }

        return new JRBeanCollectionDataSource(deviationPaymentNewItemList);
    }

    public void collectingReportItems(Session session, String orderType, Date startTime, Date addOneDayEndTime,
            String conditionDetectedNotEat, String conditionNotDetectedEat, String conditionReserve, String conditionRecycle,
            List<Long> idOfOrgList, List<DeviationPaymentNewSubReportItem> deviationPaymentSubReportItemList,
            HashMap<Long, List<DiscountRule>> rulesForOrgMap,
            HashMap<Long, List<ComplexInfoItem>> complexInfoItemListByPlanMap, List<Long> onlyPaidCategories) {

        List<PlanOrderItem> resultSubtraction = new ArrayList<PlanOrderItem>(); //Разность
        List<PlanOrderItem> resultIntersection = new ArrayList<PlanOrderItem>(); //Пересечение

        // Оплаченные заказы
        List<PlanOrderItem> planOrderItemsPaidByOneDay = DetailedDeviationsWithoutCorpsService.
                loadPaidPlanOrderInfo(session, orderType, idOfOrgList, startTime, addOneDayEndTime);

        // План льготного питания, резерв - ordertype = 6 + 11
        List<PlanOrderItem> planOrderItemsReserveByOneDay = DetailedDeviationsWithoutCorpsService.
                loadPaidPlanOrderInfo(session, getOrderTypeReserve(), idOfOrgList, startTime, addOneDayEndTime);

        // Утилизация
        List<PlanOrderItem> planOrderItemsRecycleByOneDay = DetailedDeviationsWithoutCorpsService.
                loadPaidPlanOrderInfo(session, getOrderTypeRecycle(), idOfOrgList, startTime, addOneDayEndTime);

        // План начальные классы без льгот, но которые питаются как льготники (проход не зафиксирован)
        List<PlanOrderItem> planOrderItemsPrimaryClassesWithoutBenefitsNotDetected = DetailedDeviationsWithoutCorpsService
                .loadPlanOrderItemsPrimaryClassesWithoutBenefitsNotDetected(session, startTime, addOneDayEndTime,
                        idOfOrgList);

        List<PlanOrderItem> planOrderItemList = planOrderItemsPaidByOneDay;

        List<Long> idOfClientsList = new ArrayList<Long>();

        for (PlanOrderItem planOrderItem : planOrderItemList) {
            idOfClientsList.add(planOrderItem.getIdOfClient());
        }

        //План тех кто были в школе - Те кто дожны были получить бесплатное питание | Проход по карте зафиксирован
        List<PlanOrderItem> planOrderItemsToPayDetected = DetailedDeviationsWithoutCorpsService.
                loadPlanOrderItemToPayDetected(session, startTime, addOneDayEndTime, idOfOrgList, rulesForOrgMap,
                        complexInfoItemListByPlanMap, onlyPaidCategories);

        //План тех кто не был в школе - Те кто дожны были получить бесплатное питание | Проход по карте не зафиксирован
        List<PlanOrderItem> planOrderItemsToPayNotDetected = DetailedDeviationsWithoutCorpsService
                .loadPlanOrderItemToPayNotDetected(session, startTime, addOneDayEndTime, idOfOrgList, idOfClientsList,
                        rulesForOrgMap, complexInfoItemListByPlanMap, onlyPaidCategories);

        if (planOrderItemsToPayDetected != null) {
            if (!planOrderItemsToPayDetected.isEmpty()) {
                for (PlanOrderItem planOrderItem : planOrderItemsToPayDetected) {
                    if (!planOrderItemsPaidByOneDay.contains(planOrderItem)) {
                        resultSubtraction.add(planOrderItem);
                    }
                }
            }
        }

        if (planOrderItemsToPayNotDetected != null) {
            if (!planOrderItemsToPayNotDetected.isEmpty()) {
                for (PlanOrderItem planOrderItem : planOrderItemsPaidByOneDay) {
                    if (planOrderItemsToPayNotDetected.contains(planOrderItem)) {
                        resultIntersection.add(planOrderItem);
                    }
                }
            }
        }

        Collections.sort(resultIntersection);
        Collections.sort(resultSubtraction);
        Collections.sort(planOrderItemsReserveByOneDay);
        Collections.sort(planOrderItemsPrimaryClassesWithoutBenefitsNotDetected);
        Collections.sort(planOrderItemsRecycleByOneDay);

        if (!resultSubtraction.isEmpty()) {
            fill(resultSubtraction, conditionDetectedNotEat, deviationPaymentSubReportItemList);
        }
        if (!resultIntersection.isEmpty()) {
            fill(resultIntersection, conditionNotDetectedEat, deviationPaymentSubReportItemList);
        }
        if (!planOrderItemsReserveByOneDay.isEmpty()) {
            fill(planOrderItemsReserveByOneDay, conditionReserve, deviationPaymentSubReportItemList);
        }
        if (!planOrderItemsPrimaryClassesWithoutBenefitsNotDetected.isEmpty()) {
            List<PlanOrderItem> planOrderItemsContains = new ArrayList<PlanOrderItem>();

            for (PlanOrderItem planOrderItem: planOrderItemsPrimaryClassesWithoutBenefitsNotDetected) {
                if (!resultIntersection.contains(planOrderItem)) {
                    planOrderItemsContains.add(planOrderItem);
                }
            }

            if (!planOrderItemsContains.isEmpty()) {
                fill(planOrderItemsContains, conditionNotDetectedEat, deviationPaymentSubReportItemList);
            }
        }
        if (!planOrderItemsRecycleByOneDay.isEmpty()) {
            fill(planOrderItemsRecycleByOneDay, conditionRecycle, deviationPaymentSubReportItemList);
        }
    }

    public void collectingReportItemsInterval(Session session, String orderTypeLgotnick, Date startTime, Date endTime,
            String conditionDetectedNotEat, String conditionNotDetectedEat, String conditionReserve, String conditionRecycle,
            List<DeviationPaymentNewSubReportItem> deviationPaymentSubReportItemList, List<Long> idOfOrgList,
            HashMap<Long, List<DiscountRule>> rulesForOrgMap,
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
                loadPaidPlanOrderInfo(session, getOrderTypeReserve(), idOfOrgList, startTime, endTime);

        // Утилизация
        List<PlanOrderItem> planOrderItemsRecycleByInterval = DetailedDeviationsWithoutCorpsService.
                loadPaidPlanOrderInfo(session, getOrderTypeRecycle(), idOfOrgList, startTime, endTime);

        // План начальные классы без льгот, но которые питаются как льготники (проход не зафиксирован)
        List<PlanOrderItem> planOrderItemsPrimaryClassesWithoutBenefitsNotDetectedInterval = DetailedDeviationsWithoutCorpsService
                .loadPlanOrderItemsPrimaryClassesWithoutBenefitsNotDetected(session, startTime, endTime, idOfOrgList);

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

        Collections.sort(resultSubtractionInterval);
        Collections.sort(resultIntersectionInterval);
        Collections.sort(planOrderItemsReserveByInterval);
        Collections.sort(planOrderItemsPrimaryClassesWithoutBenefitsNotDetectedInterval);
        Collections.sort(planOrderItemsRecycleByInterval);

        if (!resultSubtractionInterval.isEmpty()) {
            fill(resultSubtractionInterval, conditionDetectedNotEat, deviationPaymentSubReportItemList);
        }
        if (!resultIntersectionInterval.isEmpty()) {
            fill(resultIntersectionInterval, conditionNotDetectedEat, deviationPaymentSubReportItemList);
        }
        if (!planOrderItemsReserveByInterval.isEmpty()) {
            fill(planOrderItemsReserveByInterval, conditionReserve, deviationPaymentSubReportItemList);
        }
        if (!planOrderItemsPrimaryClassesWithoutBenefitsNotDetectedInterval.isEmpty()) {
            List<PlanOrderItem> planOrderItemsContains = new ArrayList<PlanOrderItem>();

            for (PlanOrderItem planOrderItem: planOrderItemsPrimaryClassesWithoutBenefitsNotDetectedInterval) {
                if (!resultIntersectionInterval.contains(planOrderItem)) {
                    planOrderItemsContains.add(planOrderItem);
                }
            }

            if (!planOrderItemsContains.isEmpty()) {
                fill(planOrderItemsContains, conditionNotDetectedEat, deviationPaymentSubReportItemList);
            }
        }
        if (!planOrderItemsRecycleByInterval.isEmpty()) {
            fill(planOrderItemsRecycleByInterval, conditionRecycle, deviationPaymentSubReportItemList);
        }
    }

    public void fill(List<PlanOrderItem> result, String condition,
            List<DeviationPaymentNewSubReportItem> deviationPaymentNewSubReportItemList) {
        Long rowNum = 0L;
        String groupName = result.get(0).getGroupName();

        for (PlanOrderItem planOrderItem : result) {
            if (groupName.equals(planOrderItem.getGroupName())) {
                ++rowNum;
            } else {
                groupName = planOrderItem.getGroupName();
                rowNum = 1L;
            }
            DeviationPaymentNewSubReportItem deviationPaymentNewSubReportItem = createReportItem(rowNum, condition,
                    planOrderItem);
            deviationPaymentNewSubReportItemList.add(deviationPaymentNewSubReportItem);
        }
    }

    public DeviationPaymentNewSubReportItem createReportItem(Long rowNum, String condition,
            PlanOrderItem planOrderItem) {
        DeviationPaymentNewSubReportItem deviationPaymentNewSubReportItem = new DeviationPaymentNewSubReportItem(rowNum,
                condition, planOrderItem.getGroupName(), planOrderItem.getClientName(), planOrderItem.getOrderDate(),
                planOrderItem.getComplexName());
        return deviationPaymentNewSubReportItem;
    }
}
