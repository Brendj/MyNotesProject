package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.ClientsEntereventsService;
import ru.axetta.ecafe.processor.core.persistence.utils.DetailedDeviationsWithoutCorpsService;
import ru.axetta.ecafe.processor.core.persistence.utils.FriendlyOrganizationsInfoModel;
import ru.axetta.ecafe.processor.core.persistence.utils.OrgUtils;
import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 02.10.14
 * Time: 18:06
 */

public class DetailedDeviationsPaymentOrReducedPriceMealsBuilder extends BasicReportForAllOrgJob.Builder {

    private final String templateFilename;
    private final String subReportDir;
    private final String templateFileNameInterval;

    public DetailedDeviationsPaymentOrReducedPriceMealsBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
        subReportDir = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath();
        templateFileNameInterval = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath()
                + "DetailedDeviationsPaymentOrReducedPriceMealsIntervalJasperReport.jasper";
    }

    public DetailedDeviationsPaymentOrReducedPriceMealsBuilder() {
        templateFilename = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath()
                + DetailedDeviationsPaymentOrReducedPriceMealsJasperReport.class.getSimpleName() + ".jasper";
        subReportDir = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath();
        templateFileNameInterval = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath()
                + "DetailedDeviationsPaymentOrReducedPriceMealsIntervalJasperReport.jasper";
    }

    @Override
    public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        if (!(new File(this.templateFilename)).exists()) {
            throw new Exception(String.format("Не найден файл шаблона '%s'", templateFilename));
        }
        String idOfOrgs = StringUtils.trimToEmpty(getReportProperties().getProperty(ReportPropertiesUtils.P_ID_OF_ORG));

        List<Long> idOfOrgList = new ArrayList<Long>();
        for (String idOfOrg : Arrays.asList(StringUtils.split(idOfOrgs, ','))) {
            idOfOrgList.add(Long.parseLong(idOfOrg));
        }

        Set<FriendlyOrganizationsInfoModel> friendlyOrganizationsInfoModels = OrgUtils
                .getMainBuildingAndFriendlyOrgsList(session, idOfOrgList);

        Date generateBeginTime = new Date();

        /* Параметры для передачи в jasper */
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("beginDate", CalendarUtils.dateToString(startTime));
        parameterMap.put("endDate", CalendarUtils.dateToString(endTime));
        parameterMap.put("IS_IGNORE_PAGINATION", true);
        parameterMap.put("SUBREPORT_DIR", subReportDir);
        JRDataSource dataSource = buildDataSource(session, friendlyOrganizationsInfoModels, startTime, endTime);
        JasperPrint jasperPrint;
        if (CalendarUtils.truncateToDayOfMonth(startTime).equals(CalendarUtils.truncateToDayOfMonth(endTime))) {
            jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
        } else {
            jasperPrint = JasperFillManager.fillReport(templateFileNameInterval, parameterMap, dataSource);
        }
        Date generateEndTime = new Date();
        final long generateDuration = generateEndTime.getTime() - generateBeginTime.getTime();
        return new DetailedDeviationsPaymentOrReducedPriceMealsJasperReport(generateBeginTime, generateDuration,
                jasperPrint, startTime, endTime);
    }

    @SuppressWarnings("unchecked")
    private JRDataSource buildDataSource(Session session,
            Set<FriendlyOrganizationsInfoModel> friendlyOrganizationsInfoModels, Date startTime, Date endTime)
            throws Exception {

        //Результирующиц лист по которому строиться отчет
        List<DeviationPaymentItem> deviationPaymentItemList = new ArrayList<DeviationPaymentItem>();

        // Те кто получил бесплатное питание за один день
        List<PlanOrderItem> planOrderItemsPaidByOneDay;

        // Те кто получил бесплатное питание за период
        List<PlanOrderItem> planOrderItemsPaidByInterval;

        // План питания льготники
        String orderTypeLgotnick = "4,8"; //6 - резерв

        String conditionDetectedNotEat = "Проход по карте зафиксирован, питание не предоставлено";
        String conditionNotDetectedEat = "Проход по карте не зафиксирован, питание предоставлено";

        HashMap<Long, List<DiscountRule>> rulesForOrgMap = new HashMap<Long, List<DiscountRule>>();
        HashMap<Long, List<ComplexInfoItem>> complexInfoItemListByPlanMap = new HashMap<Long, List<ComplexInfoItem>>();

        // платные категории
        List<Long> onlyPaidCategories = ClientsEntereventsService.loadAllPaydAbleCategories(session);

        if (CalendarUtils.truncateToDayOfMonth(startTime).equals(CalendarUtils.truncateToDayOfMonth(endTime))) {

            Date addOneDayEndTime = CalendarUtils.addOneDay(startTime);
            CalendarUtils.truncateToDayOfMonth(addOneDayEndTime);
            Long rowNum = 0L;

            for (FriendlyOrganizationsInfoModel organizationsInfoModel : friendlyOrganizationsInfoModels) {
                Set<Org> orgSet = organizationsInfoModel.getFriendlyOrganizationsSet();

                if (orgSet.size() > 0) {
                    List<Long> idOfOrgList = new ArrayList<Long>();
                    for (Org org : orgSet) {
                        idOfOrgList.add(org.getIdOfOrg());
                    }

                    for (Long idOfOrg : idOfOrgList) {
                        // правила для организации
                        List<DiscountRule> rulesForOrg = ClientsEntereventsService
                                .getDiscountRulesByOrg(session, idOfOrg);
                        rulesForOrgMap.put(idOfOrg, rulesForOrg);
                        //имена комплексов по плану
                        List<ComplexInfoItem> complexInfoItemListByPlan = ClientsEntereventsService
                                .loadComplexNameByPlan(session, idOfOrg, startTime, endTime);
                        complexInfoItemListByPlanMap.put(idOfOrg, complexInfoItemListByPlan);
                    }

                    for (Long idOfOrg : idOfOrgList) {
                        ++rowNum;
                        DeviationPaymentItem deviationPaymentItem = new DeviationPaymentItem();
                        List<DeviationPaymentSubReportItem> deviationPaymentSubReportItemList = new ArrayList<DeviationPaymentSubReportItem>();

                        collectingReportItems(session, orderTypeLgotnick, idOfOrg, startTime, addOneDayEndTime,
                                conditionDetectedNotEat, conditionNotDetectedEat, deviationPaymentSubReportItemList,
                                idOfOrgList, rulesForOrgMap, complexInfoItemListByPlanMap, onlyPaidCategories);

                        if (!deviationPaymentSubReportItemList.isEmpty()) {
                            if (organizationsInfoModel.getAddress() != null
                                    && organizationsInfoModel.getOfficialName() != null) {
                                deviationPaymentItem.setMainBuilding(organizationsInfoModel.getOfficialName());
                            } else {
                                for (Org orgSetItem : orgSet) {
                                    if (orgSetItem.isMainBuilding()) {
                                        deviationPaymentItem.setMainBuilding(orgSetItem.getOfficialName());
                                    }
                                }
                            }
                            Org org = (Org) session.load(Org.class, idOfOrg);
                            deviationPaymentItem.setAddress(org.getAddress());
                            deviationPaymentItem.setOrgName(org.getShortName());
                            deviationPaymentItem.setRowNum(rowNum);
                            deviationPaymentItem
                                    .setDeviationPaymentSubReportItemList(deviationPaymentSubReportItemList);
                            deviationPaymentItemList.add(deviationPaymentItem);
                        }
                    }
                } else {
                    Org org = (Org) session.load(Org.class, organizationsInfoModel.getIdOfOrg());

                    // правила для организации
                    List<DiscountRule> rulesForOrg = ClientsEntereventsService
                            .getDiscountRulesByOrg(session, org.getIdOfOrg());
                    rulesForOrgMap.put(org.getIdOfOrg(), rulesForOrg);

                    List<ComplexInfoItem> complexInfoItemListByPlan = ClientsEntereventsService
                            .loadComplexNameByPlan(session, org.getIdOfOrg(), startTime, endTime);
                    complexInfoItemListByPlanMap.put(org.getIdOfOrg(), complexInfoItemListByPlan);

                    DeviationPaymentItem deviationPaymentItem = new DeviationPaymentItem();
                    List<DeviationPaymentSubReportItem> deviationPaymentSubReportItemList = new ArrayList<DeviationPaymentSubReportItem>();
                    List<Long> idOfOrgList = new ArrayList<Long>();
                    idOfOrgList.add(org.getIdOfOrg());
                    collectingReportItems(session, orderTypeLgotnick, org.getIdOfOrg(), startTime, addOneDayEndTime,
                            conditionDetectedNotEat, conditionNotDetectedEat, deviationPaymentSubReportItemList,
                            idOfOrgList, rulesForOrgMap, complexInfoItemListByPlanMap, onlyPaidCategories);
                    if (!deviationPaymentSubReportItemList.isEmpty()) {
                        deviationPaymentItem.setMainBuilding(org.getShortName());
                        deviationPaymentItem.setAddress(org.getAddress());
                        deviationPaymentItem.setOrgName(org.getOfficialName());
                        deviationPaymentItem.setRowNum(rowNum);
                        deviationPaymentItem.setDeviationPaymentSubReportItemList(deviationPaymentSubReportItemList);
                        deviationPaymentItemList.add(deviationPaymentItem);
                    }
                }
            }
        } else {
            Long rowNum = 0L;

            for (FriendlyOrganizationsInfoModel organizationsInfoModel : friendlyOrganizationsInfoModels) {
                Set<Org> orgSet = organizationsInfoModel.getFriendlyOrganizationsSet();
                if (orgSet.size() > 0) {
                    List<Long> idOfOrgList = new ArrayList<Long>();
                    for (Org org : orgSet) {
                        idOfOrgList.add(org.getIdOfOrg());
                    }

                    for (Long idOfOrg : idOfOrgList) {
                        // правила для организации
                        List<DiscountRule> rulesForOrg = ClientsEntereventsService
                                .getDiscountRulesByOrg(session, idOfOrg);
                        rulesForOrgMap.put(idOfOrg, rulesForOrg);

                        List<ComplexInfoItem> complexInfoItemListByPlan = ClientsEntereventsService
                                .loadComplexNameByPlan(session, idOfOrg, startTime, endTime);
                        complexInfoItemListByPlanMap.put(idOfOrg, complexInfoItemListByPlan);
                    }

                    for (Long idOfOrg : idOfOrgList) {
                        ++rowNum;
                        DeviationPaymentItem deviationPaymentItem = new DeviationPaymentItem();
                        List<DeviationPaymentSubReportItem> deviationPaymentSubReportItemList = new ArrayList<DeviationPaymentSubReportItem>();

                        collectingReportItemsInterval(session, orderTypeLgotnick, idOfOrg, startTime, endTime,
                                conditionDetectedNotEat, conditionNotDetectedEat, deviationPaymentSubReportItemList,
                                idOfOrgList, rulesForOrgMap, complexInfoItemListByPlanMap, onlyPaidCategories);

                        if (!deviationPaymentSubReportItemList.isEmpty()) {
                            if (organizationsInfoModel.getAddress() != null
                                    && organizationsInfoModel.getOfficialName() != null) {
                                deviationPaymentItem.setMainBuilding(organizationsInfoModel.getOfficialName());
                            } else {
                                for (Org orgSetItem : orgSet) {
                                    if (orgSetItem.isMainBuilding()) {
                                        deviationPaymentItem.setMainBuilding(orgSetItem.getOfficialName());
                                    }
                                }
                            }
                            Org org = (Org) session.load(Org.class, idOfOrg);
                            deviationPaymentItem.setAddress(org.getAddress());
                            deviationPaymentItem.setOrgName(org.getOfficialName());
                            deviationPaymentItem.setRowNum(rowNum);
                            deviationPaymentItem
                                    .setDeviationPaymentSubReportItemList(deviationPaymentSubReportItemList);
                            deviationPaymentItemList.add(deviationPaymentItem);
                        }
                    }
                } else {
                    Org org = (Org) session.load(Org.class, organizationsInfoModel.getIdOfOrg());

                    // правила для организации
                    List<DiscountRule> rulesForOrg = ClientsEntereventsService
                            .getDiscountRulesByOrg(session, org.getIdOfOrg());
                    rulesForOrgMap.put(org.getIdOfOrg(), rulesForOrg);

                    List<ComplexInfoItem> complexInfoItemListByPlan = ClientsEntereventsService
                            .loadComplexNameByPlan(session, org.getIdOfOrg(), startTime, endTime);
                    complexInfoItemListByPlanMap.put(org.getIdOfOrg(), complexInfoItemListByPlan);

                    List<DeviationPaymentSubReportItem> deviationPaymentSubReportItemList = new ArrayList<DeviationPaymentSubReportItem>();
                    List<Long> idOfOrgList = new ArrayList<Long>();
                    idOfOrgList.add(org.getIdOfOrg());
                    collectingReportItemsInterval(session, orderTypeLgotnick, org.getIdOfOrg(), startTime, endTime,
                            conditionDetectedNotEat, conditionNotDetectedEat, deviationPaymentSubReportItemList,
                            idOfOrgList, rulesForOrgMap, complexInfoItemListByPlanMap, onlyPaidCategories);
                    DeviationPaymentItem deviationPaymentItem = new DeviationPaymentItem();
                    if (!deviationPaymentSubReportItemList.isEmpty()) {
                        deviationPaymentItem.setAddress(org.getAddress());
                        deviationPaymentItem.setOrgName(org.getOfficialName());
                        deviationPaymentItem.setDeviationPaymentSubReportItemList(deviationPaymentSubReportItemList);
                        deviationPaymentItemList.add(deviationPaymentItem);
                    }
                }
            }
        }

        for (DeviationPaymentItem dev : deviationPaymentItemList) {
            Collections.sort(dev.getDeviationPaymentSubReportItemList());
        }

        return new JRBeanCollectionDataSource(deviationPaymentItemList);
    }

    public void fill(List<PlanOrderItem> result, String condition,
            List<DeviationPaymentSubReportItem> deviationPaymentSubReportItemList) {
        for (PlanOrderItem planOrderItem : result) {
            DeviationPaymentSubReportItem deviationPaymentSubReportItem = createReportItem(condition, planOrderItem);
            deviationPaymentSubReportItemList.add(deviationPaymentSubReportItem);
        }
    }

    public DeviationPaymentSubReportItem createReportItem(String condition, PlanOrderItem planOrderItem) {
        DeviationPaymentSubReportItem deviationPaymentSubReportItem = new DeviationPaymentSubReportItem(condition,
                planOrderItem.getGroupName(), planOrderItem.getClientName(), planOrderItem.getOrderDate(),
                planOrderItem.getComplexName());
        return deviationPaymentSubReportItem;
    }

    public void collectingReportItems(Session session, String orderTypeLgotnick, Long idOfOrg, Date startTime,
            Date addOneDayEndTime, String conditionDetectedNotEat, String conditionNotDetectedEat,
            List<DeviationPaymentSubReportItem> deviationPaymentSubReportItemList, List<Long> idOfOrgList,
            HashMap<Long, List<DiscountRule>> rulesForOrgMap,
            HashMap<Long, List<ComplexInfoItem>> complexInfoItemListByPlanMap, List<Long> onlyPaidCategories) {

        List<PlanOrderItem> resultSubtraction = new ArrayList<PlanOrderItem>(); // Разность
        List<PlanOrderItem> resultIntersection = new ArrayList<PlanOrderItem>(); // Пересечение

        // Оплаченные заказы по всем организациям
        List<PlanOrderItem> planOrderItemsPaidByOneDayAllOrgs = ClientsEntereventsService.
                loadPaidPlanOrderInfo(session, orderTypeLgotnick, idOfOrgList, startTime, addOneDayEndTime);

        List<PlanOrderItem> planOrderItemList = planOrderItemsPaidByOneDayAllOrgs;

        List<Long> idOfClientsList = new ArrayList<Long>();

        for (PlanOrderItem planOrderItem : planOrderItemList) {
            idOfClientsList.add(planOrderItem.getIdOfClient());
        }

        // Оплаченные Заказы
        List<PlanOrderItem> planOrderItemsPaidByOneDay = ClientsEntereventsService
                .loadPaidPlanOrderInfo(session, orderTypeLgotnick, idOfOrg, startTime, addOneDayEndTime);

        // Те кто дожны были получить бесплатное питание | Проход по карте зафиксирован
        List<PlanOrderItem> planOrderItemsToPayDetected = ClientsEntereventsService
                .loadPlanOrderItemToPayDetected(session, startTime, addOneDayEndTime, idOfOrg, rulesForOrgMap,
                        complexInfoItemListByPlanMap, onlyPaidCategories);

        // Те кто дожны были получить бесплатное питание | Проход по карте не зафиксирован
        List<PlanOrderItem> planOrderItemsToPayNotDetected = ClientsEntereventsService
                .loadPlanOrderItemToPayNotDetected(session, startTime, addOneDayEndTime, idOfOrg, idOfOrgList,
                        idOfClientsList, rulesForOrgMap, complexInfoItemListByPlanMap, onlyPaidCategories);

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

        if (!resultSubtraction.isEmpty()) {
            fill(resultSubtraction, conditionDetectedNotEat, deviationPaymentSubReportItemList);
        }

        if (!resultIntersection.isEmpty()) {
            fill(resultIntersection, conditionNotDetectedEat, deviationPaymentSubReportItemList);
        }
    }

    public void collectingReportItemsInterval(Session session, String orderTypeLgotnick, Long idOfOrg, Date startTime,
            Date endTime, String conditionDetectedNotEat, String conditionNotDetectedEat,
            List<DeviationPaymentSubReportItem> deviationPaymentSubReportItemList, List<Long> idOfOrgList,
            HashMap<Long, List<DiscountRule>> rulesForOrgMap,
            HashMap<Long, List<ComplexInfoItem>> complexInfoItemListByPlanMap, List<Long> onlyPaidCategories) {
        List<PlanOrderItem> resultSubtractionInterval = new ArrayList<PlanOrderItem>(); // Разность за интервал
        List<PlanOrderItem> resultIntersectionInterval = new ArrayList<PlanOrderItem>(); // Пересечение за интервал

        // Те кто дожны были получить бесплатное питание | Проход по карте зафиксирован - за интервал
        List<PlanOrderItem> planOrderItemsToPayDetectedInterval = new ArrayList<PlanOrderItem>();

        // Те кто дожны были получить бесплатное питание | Проход по карте не зафиксирован - за интервал
        List<PlanOrderItem> planOrderItemsToPayNotDetectedInterval = new ArrayList<PlanOrderItem>();

        List<PlanOrderItem> planOrderItemsPaidByInterval = ClientsEntereventsService
                .loadPaidPlanOrderInfo(session, orderTypeLgotnick, idOfOrg, startTime, endTime);

        // Оплаченные заказы по всем организациям
        List<PlanOrderItem> planOrderItemsPaidByIntervalAllOrgs = DetailedDeviationsWithoutCorpsService
                .loadPaidPlanOrderInfo(session, orderTypeLgotnick, idOfOrgList, startTime, endTime);

        List<PlanOrderItem> planOrderItemList = planOrderItemsPaidByIntervalAllOrgs;

        List<Long> idOfClientsList = new ArrayList<Long>();

        for (PlanOrderItem planOrderItem : planOrderItemList) {
            idOfClientsList.add(planOrderItem.getIdOfClient());
        }

        Date sTt = startTime;
        CalendarUtils.truncateToDayOfMonth(sTt);

        Date eTt = CalendarUtils.addOneDay(startTime);
        CalendarUtils.truncateToDayOfMonth(eTt);

        // План по тем кто отметился в здании за интервал
        List<PlanOrderItem> planOrderItemToPayDetectedIntervalList = ClientsEntereventsService
                .loadPlanOrderItemToPayDetected(session, sTt, eTt, idOfOrg, rulesForOrgMap,
                        complexInfoItemListByPlanMap, onlyPaidCategories);

        if (planOrderItemToPayDetectedIntervalList != null) {
            planOrderItemsToPayDetectedInterval.addAll(planOrderItemToPayDetectedIntervalList);
        }
        // План по тем кто не в здании за интервал
        List<PlanOrderItem> planOrderItemToPayNotDetectedIntervalList = ClientsEntereventsService
                .loadPlanOrderItemToPayNotDetected(session, sTt, eTt, idOfOrg, idOfOrgList, idOfClientsList,
                        rulesForOrgMap, complexInfoItemListByPlanMap, onlyPaidCategories);

        if (planOrderItemToPayNotDetectedIntervalList != null) {
            planOrderItemsToPayNotDetectedInterval.addAll(planOrderItemToPayNotDetectedIntervalList);
        }

        Date nextDateEndTime = CalendarUtils.addOneDay(endTime);
        CalendarUtils.truncateToDayOfMonth(nextDateEndTime);

        while (eTt.before(CalendarUtils.truncateToDayOfMonth(nextDateEndTime))) {
            sTt = CalendarUtils.addOneDay(sTt);
            eTt = CalendarUtils.addOneDay(eTt);
            planOrderItemToPayDetectedIntervalList = ClientsEntereventsService
                    .loadPlanOrderItemToPayDetected(session, sTt, eTt, idOfOrg, rulesForOrgMap,
                            complexInfoItemListByPlanMap, onlyPaidCategories);
            if (planOrderItemToPayDetectedIntervalList != null) {
                planOrderItemsToPayDetectedInterval.addAll(planOrderItemToPayDetectedIntervalList);
            }
            // План по тем кто не в здании
            planOrderItemToPayNotDetectedIntervalList = ClientsEntereventsService
                    .loadPlanOrderItemToPayNotDetected(session, sTt, eTt, idOfOrg, idOfOrgList, idOfClientsList,
                            rulesForOrgMap, complexInfoItemListByPlanMap, onlyPaidCategories);
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

        if (!resultSubtractionInterval.isEmpty()) {
            fill(resultSubtractionInterval, conditionDetectedNotEat, deviationPaymentSubReportItemList);
        }
        if (!resultIntersectionInterval.isEmpty()) {
            fill(resultIntersectionInterval, conditionNotDetectedEat, deviationPaymentSubReportItemList);
        }
    }
}
