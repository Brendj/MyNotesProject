/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.without.corps;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DetailedDeviationsWithoutCorpsService;
import ru.axetta.ecafe.processor.core.persistence.utils.FriendlyOrganizationsInfoModel;
import ru.axetta.ecafe.processor.core.persistence.utils.OrgUtils;
import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.DeviationPaymentItem;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.DeviationPaymentSubReportItem;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.PlanOrderItem;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Алмаз
 * Date: 27.01.15
 * Time: 19:11
 * To change this template use File | Settings | File Templates.
 */
public class DetailedDeviationsWithoutCorpsBuilder extends BasicReportForAllOrgJob.Builder {

    private final String templateFilename;
    private final String subReportDir;
    private final String templateFileNameInterval;

    public DetailedDeviationsWithoutCorpsBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
        subReportDir = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath();
        templateFileNameInterval = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath()
                + "DetailedDeviationsWithoutCorpsIntervalJasperReport.jasper";
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
        return new DetailedDeviationsWithoutCorpsJasperReport(generateBeginTime, generateDuration, jasperPrint,
                startTime, endTime);
    }

    private JRDataSource buildDataSource(Session session,
            Set<FriendlyOrganizationsInfoModel> friendlyOrganizationsInfoModels, Date startTime, Date endTime) {

        //Результирующий лист по которому строиться отчет
        List<DeviationPaymentItem> deviationPaymentItemList = new ArrayList<DeviationPaymentItem>();

        // План питания льготники
        String orderTypeLgotnick = "4,6,8";

        String conditionDetectedNotEat = "Проход по карте зафиксирован, питание не предоставлено";
        String conditionNotDetectedEat = "Проход по карте не зафиксирован, питание предоставлено";

        if (CalendarUtils.truncateToDayOfMonth(startTime).equals(CalendarUtils.truncateToDayOfMonth(endTime))) {

            Date addOneDayEndTime = CalendarUtils.addOneDay(startTime);
            CalendarUtils.truncateToDayOfMonth(addOneDayEndTime);

            Long rowNum = 0L;

            for (FriendlyOrganizationsInfoModel organizationsInfoModel : friendlyOrganizationsInfoModels) {
                Set<Org> orgSet = organizationsInfoModel.getFriendlyOrganizationsSet();
                ++rowNum;
                if (orgSet.size() > 0) {
                    List<Long> idOfOrgList = new ArrayList<Long>();
                    for (Org org : orgSet) {
                        idOfOrgList.add(org.getIdOfOrg());
                    }

                    DeviationPaymentItem deviationPaymentItem = new DeviationPaymentItem();
                    List<DeviationPaymentSubReportItem> deviationPaymentSubReportItemList = new ArrayList<DeviationPaymentSubReportItem>();

                    collectingReportItems(session, orderTypeLgotnick, startTime, addOneDayEndTime,
                            conditionDetectedNotEat, conditionNotDetectedEat, idOfOrgList,
                            deviationPaymentSubReportItemList);

                    if (!deviationPaymentSubReportItemList.isEmpty()) {
                        if (organizationsInfoModel.getAddress() != null
                                && organizationsInfoModel.getOfficialName() != null) {
                            deviationPaymentItem.setOrgName(organizationsInfoModel.getOfficialName());
                            deviationPaymentItem.setAddress(organizationsInfoModel.getAddress());
                        }
                        deviationPaymentItem.setRowNum(rowNum);
                        deviationPaymentItem.setDeviationPaymentSubReportItemList(deviationPaymentSubReportItemList);
                        deviationPaymentItemList.add(deviationPaymentItem);
                    }

                } else {
                    Org org = (Org) session.load(Org.class, organizationsInfoModel.getIdOfOrg());
                    DeviationPaymentItem deviationPaymentItem = new DeviationPaymentItem();
                    List<DeviationPaymentSubReportItem> deviationPaymentSubReportItemList = new ArrayList<DeviationPaymentSubReportItem>();
                    List<Long> idOfOrgList = new ArrayList<Long>();
                    idOfOrgList.add(org.getIdOfOrg());
                    collectingReportItems(session, orderTypeLgotnick, startTime, addOneDayEndTime,
                            conditionDetectedNotEat, conditionNotDetectedEat, idOfOrgList,
                            deviationPaymentSubReportItemList);

                    if (!deviationPaymentSubReportItemList.isEmpty()) {
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
                ++rowNum;
                if (orgSet.size() > 0) {
                    List<Long> idOfOrgList = new ArrayList<Long>();
                    for (Org org : orgSet) {
                        idOfOrgList.add(org.getIdOfOrg());
                    }
                    DeviationPaymentItem deviationPaymentItem = new DeviationPaymentItem();
                    List<DeviationPaymentSubReportItem> deviationPaymentSubReportItemList = new ArrayList<DeviationPaymentSubReportItem>();

                    collectingReportItemsInterval(session, orderTypeLgotnick, startTime, endTime,
                            conditionDetectedNotEat, conditionNotDetectedEat, deviationPaymentSubReportItemList,
                            idOfOrgList);

                    if (!deviationPaymentSubReportItemList.isEmpty()) {
                        if (organizationsInfoModel.getAddress() != null
                                && organizationsInfoModel.getOfficialName() != null) {
                            deviationPaymentItem.setOrgName(organizationsInfoModel.getOfficialName());
                            deviationPaymentItem.setAddress(organizationsInfoModel.getAddress());
                        }
                        deviationPaymentItem.setRowNum(rowNum);
                        deviationPaymentItem.setDeviationPaymentSubReportItemList(deviationPaymentSubReportItemList);
                        deviationPaymentItemList.add(deviationPaymentItem);
                    }
                } else {
                    Org org = (Org) session.load(Org.class, organizationsInfoModel.getIdOfOrg());
                    List<DeviationPaymentSubReportItem> deviationPaymentSubReportItemList = new ArrayList<DeviationPaymentSubReportItem>();
                    List<Long> idOfOrgList = new ArrayList<Long>();
                    idOfOrgList.add(org.getIdOfOrg());
                    collectingReportItemsInterval(session, orderTypeLgotnick, startTime, endTime,
                            conditionDetectedNotEat, conditionNotDetectedEat, deviationPaymentSubReportItemList,
                            idOfOrgList);
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

    public void collectingReportItems(Session session, String orderType, Date startTime, Date addOneDayEndTime,
            String conditionDetectedNotEat, String conditionNotDetectedEat, List<Long> idOfOrgList,
            List<DeviationPaymentSubReportItem> deviationPaymentSubReportItemList) {

        List<PlanOrderItem> resultSubtraction = new ArrayList<PlanOrderItem>(); //Разность
        List<PlanOrderItem> resultIntersection = new ArrayList<PlanOrderItem>(); //Пересечение

        // Оплаченные заказы
        List<PlanOrderItem> planOrderItemsPaidByOneDay = DetailedDeviationsWithoutCorpsService.
                loadPaidPlanOrderInfo(session, orderType, idOfOrgList, startTime, addOneDayEndTime);

        //План тех кто были в школе - Те кто дожны были получить бесплатное питание | Проход по карте зафиксирован
        List<PlanOrderItem> planOrderItemsToPayDetected = DetailedDeviationsWithoutCorpsService.
                loadPlanOrderItemToPayDetected(session, startTime, addOneDayEndTime, idOfOrgList);

        //План тех кто не был в школе - Те кто дожны были получить бесплатное питание | Проход по карте не зафиксирован
        List<PlanOrderItem> planOrderItemsToPayNotDetected = DetailedDeviationsWithoutCorpsService
                .loadPlanOrderItemToPayNotDetected(session, startTime, addOneDayEndTime, idOfOrgList);

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

    public void collectingReportItemsInterval(Session session, String orderTypeLgotnick, Date startTime, Date endTime,
            String conditionDetectedNotEat, String conditionNotDetectedEat,
            List<DeviationPaymentSubReportItem> deviationPaymentSubReportItemList, List<Long> idOfOrgList) {
        List<PlanOrderItem> resultSubtractionInterval = new ArrayList<PlanOrderItem>(); // Разность за интервал
        List<PlanOrderItem> resultIntersectionInterval = new ArrayList<PlanOrderItem>(); // Пересечение за интервал

        // Те кто дожны были получить бесплатное питание | Проход по карте зафиксирован - за интервал
        List<PlanOrderItem> planOrderItemsToPayDetectedInterval = new ArrayList<PlanOrderItem>();

        // Те кто дожны были получить бесплатное питание | Проход по карте не зафиксирован - за интервал
        List<PlanOrderItem> planOrderItemsToPayNotDetectedInterval = new ArrayList<PlanOrderItem>();

        List<PlanOrderItem> planOrderItemsPaidByInterval = DetailedDeviationsWithoutCorpsService
                .loadPaidPlanOrderInfo(session, orderTypeLgotnick, idOfOrgList, startTime, endTime);

        Date sTt = startTime;
        CalendarUtils.truncateToDayOfMonth(sTt);

        Date eTt = CalendarUtils.addOneDay(startTime);
        CalendarUtils.truncateToDayOfMonth(eTt);

        // План по тем кто отметился в здании за интервал
        List<PlanOrderItem> planOrderItemToPayDetectedIntervalList = DetailedDeviationsWithoutCorpsService
                .loadPlanOrderItemToPayDetected(session, sTt, eTt, idOfOrgList);

        if (planOrderItemToPayDetectedIntervalList != null) {
            planOrderItemsToPayDetectedInterval.addAll(planOrderItemToPayDetectedIntervalList);
        }
        // План по тем кто не в здании за интервал
        List<PlanOrderItem> planOrderItemToPayNotDetectedIntervalList = DetailedDeviationsWithoutCorpsService
                .loadPlanOrderItemToPayNotDetected(session, sTt, eTt, idOfOrgList);

        if (planOrderItemToPayNotDetectedIntervalList != null) {
            planOrderItemsToPayNotDetectedInterval.addAll(planOrderItemToPayNotDetectedIntervalList);
        }

        Date nextDateEndTime = CalendarUtils.addOneDay(endTime);
        CalendarUtils.truncateToDayOfMonth(nextDateEndTime);

        while (eTt.before(CalendarUtils.truncateToDayOfMonth(nextDateEndTime))) {
            sTt = CalendarUtils.addOneDay(sTt);
            eTt = CalendarUtils.addOneDay(eTt);
            planOrderItemToPayDetectedIntervalList = DetailedDeviationsWithoutCorpsService
                    .loadPlanOrderItemToPayDetected(session, sTt, eTt, idOfOrgList);
            if (planOrderItemToPayDetectedIntervalList != null) {
                planOrderItemsToPayDetectedInterval.addAll(planOrderItemToPayDetectedIntervalList);
            }
            // План по тем кто не в здании
            planOrderItemToPayNotDetectedIntervalList = DetailedDeviationsWithoutCorpsService
                    .loadPlanOrderItemToPayNotDetected(session, sTt, eTt, idOfOrgList);
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
}
