package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.ClientsEntereventsService;
import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

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
        if (StringUtils.isEmpty(this.templateFilename)) {
            throw new Exception("Не найден файл шаблона.");
        }
        String idOfOrgs = StringUtils.trimToEmpty(getReportProperties().getProperty(ReportPropertiesUtils.P_ID_OF_ORG));

        List<Long> idOfOrgList = new ArrayList<Long>();
        for (String idOfOrg : Arrays.asList(StringUtils.split(idOfOrgs, ','))) {
            idOfOrgList.add(Long.parseLong(idOfOrg));
        }
        Date generateBeginTime = new Date();

        /* Параметры для передачи в jasper */
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("beginDate", CalendarUtils.dateToString(startTime));
        parameterMap.put("endDate", CalendarUtils.dateToString(endTime));
        parameterMap.put("IS_IGNORE_PAGINATION", true);
        parameterMap.put("SUBREPORT_DIR", subReportDir);
        JRDataSource dataSource = buildDataSource(session, idOfOrgList, startTime, endTime);
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
    private JRDataSource buildDataSource(Session session, List<Long> idOfOrgList, Date startTime, Date endTime)
            throws Exception {

        //Результирующиц лист по которому строиться отчет
        List<DeviationPaymentItem> deviationPaymentItemList = new ArrayList<DeviationPaymentItem>();

        // Те кто получил бесплатное питание за один день
        List<PlanOrderItem> planOrderItemsPaidByOneDay;

        // Те кто получил бесплатное питание за период
        List<PlanOrderItem> planOrderItemsPaidByInterval;

        // План питания льготники
        String orderTypeLgotnick = "4,6,8";

        String conditionDetectedNotEat = "Проход по карте зафиксирован, питание не предоставлено";
        String conditionNotDetectedEat = "Проход по карте не зафиксирован, питание предоставлено";

        // Имена комплексов по заказам
        List<ComplexInfoForPlan> complexInfoForPlanList = new ArrayList<ComplexInfoForPlan>();

        if (CalendarUtils.truncateToDayOfMonth(startTime).equals(CalendarUtils.truncateToDayOfMonth(endTime))) {

            Date addOneDayEndTime = CalendarUtils.addOneDay(startTime);
            CalendarUtils.truncateToDayOfMonth(addOneDayEndTime);

            for (Long idOfOrg : idOfOrgList) {

                List<PlanOrderItem> resultSubtraction = new ArrayList<PlanOrderItem>(); // Разность
                List<PlanOrderItem> resultIntersection = new ArrayList<PlanOrderItem>(); // Пересечение

                // Оплаченные Заказы
                planOrderItemsPaidByOneDay = ClientsEntereventsService
                        .loadPaidPlanOrderInfo(session, orderTypeLgotnick, idOfOrg, startTime, addOneDayEndTime);

                complexInfoForPlanList = ClientsEntereventsService.loadComplexName(session, idOfOrg, orderTypeLgotnick);

                // Те кто дожны были получить бесплатное питание | Проход по карте зафиксирован
                List<PlanOrderItem> planOrderItemsToPayDetected = ClientsEntereventsService
                        .loadPlanOrderItemToPayDetected(session, startTime, addOneDayEndTime, idOfOrg);

                // Те кто дожны были получить бесплатное питание | Проход по карте не зафиксирован
                List<PlanOrderItem> planOrderItemsToPayNotDetected = ClientsEntereventsService
                        .loadPlanOrderItemToPayNotDetected(session, startTime, addOneDayEndTime, idOfOrg);

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
                        for (PlanOrderItem planOrderItem : planOrderItemsToPayNotDetected) {
                            if (planOrderItemsPaidByOneDay.contains(planOrderItem)) {
                                resultIntersection.add(planOrderItem);
                            }
                        }
                    }
                }

                if (!resultIntersection.isEmpty() || !resultSubtraction.isEmpty()) {

                    DeviationPaymentItem deviationPaymentItem = new DeviationPaymentItem();

                    if (!resultIntersection.isEmpty()) {
                        Client client = (Client) session.load(Client.class, resultIntersection.get(0).getIdOfClient());
                        Org org = (Org) session.load(Org.class, client.getOrg().getIdOfOrg());
                        deviationPaymentItem.setAddress(org.getAddress());
                        deviationPaymentItem.setOrgName(org.getShortName());
                    } else if (!resultSubtraction.isEmpty()) {
                        Client client = (Client) session.load(Client.class, resultSubtraction.get(0).getIdOfClient());
                        Org org = (Org) session.load(Org.class, client.getOrg().getIdOfOrg());
                        deviationPaymentItem.setAddress(org.getAddress());
                        deviationPaymentItem.setOrgName(org.getShortName());
                    }

                    List<DeviationPaymentSubReportItem> deviationPaymentSubReportItemList = new ArrayList<DeviationPaymentSubReportItem>();

                    if (!resultSubtraction.isEmpty()) {
                        fill(resultSubtraction, conditionDetectedNotEat, complexInfoForPlanList,
                                deviationPaymentSubReportItemList);

                    }

                    if (!resultIntersection.isEmpty()) {
                        fill(resultIntersection, conditionNotDetectedEat, complexInfoForPlanList,
                                deviationPaymentSubReportItemList);
                    }

                    deviationPaymentItem.setDeviationPaymentSubReportItemList(deviationPaymentSubReportItemList);

                    deviationPaymentItemList.add(deviationPaymentItem);
                }
            }
        } else {

            for (Long idOfOrg : idOfOrgList) {

                List<PlanOrderItem> resultSubtractionInterval = new ArrayList<PlanOrderItem>(); // Разность за интервал
                List<PlanOrderItem> resultIntersectionInterval = new ArrayList<PlanOrderItem>(); // Пересечение за интервал

                // Те кто дожны были получить бесплатное питание | Проход по карте зафиксирован - за интервал
                List<PlanOrderItem> planOrderItemsToPayDetectedInterval = new ArrayList<PlanOrderItem>();
                // Те кто дожны были получить бесплатное питание | Проход по карте не зафиксирован - за интервал
                List<PlanOrderItem> planOrderItemsToPayNotDetectedInterval = new ArrayList<PlanOrderItem>();

                planOrderItemsPaidByInterval = ClientsEntereventsService
                        .loadPaidPlanOrderInfo(session, orderTypeLgotnick, idOfOrg, startTime, endTime);

                complexInfoForPlanList = ClientsEntereventsService.loadComplexName(session, idOfOrg, orderTypeLgotnick);

                Date sTt = startTime;
                CalendarUtils.truncateToDayOfMonth(sTt);

                Date eTt = CalendarUtils.addOneDay(startTime);
                CalendarUtils.truncateToDayOfMonth(eTt);

                // План по тем кто отметился в здании за интервал
                List<PlanOrderItem> planOrderItemToPayDetectedIntervalList = ClientsEntereventsService
                        .loadPlanOrderItemToPayDetected(session, sTt, eTt, idOfOrg);
                if (planOrderItemToPayDetectedIntervalList != null) {
                    planOrderItemsToPayDetectedInterval.addAll(planOrderItemToPayDetectedIntervalList);
                }

                // План по тем кто не в здании за интервал
                List<PlanOrderItem> planOrderItemToPayNotDetectedIntervalList = ClientsEntereventsService
                        .loadPlanOrderItemToPayNotDetected(session, sTt, eTt, idOfOrg);
                if (planOrderItemToPayNotDetectedIntervalList != null) {
                    planOrderItemsToPayNotDetectedInterval.addAll(planOrderItemToPayNotDetectedIntervalList);
                }

                Date nextDateEndTime = CalendarUtils.addOneDay(endTime);
                CalendarUtils.truncateToDayOfMonth(nextDateEndTime);

                while (eTt.before(CalendarUtils.truncateToDayOfMonth(nextDateEndTime))) {
                    sTt = CalendarUtils.addOneDay(sTt);
                    eTt = CalendarUtils.addOneDay(eTt);

                    planOrderItemToPayDetectedIntervalList = ClientsEntereventsService
                            .loadPlanOrderItemToPayDetected(session, sTt, eTt, idOfOrg);
                    if (planOrderItemToPayDetectedIntervalList != null) {
                        planOrderItemsToPayDetectedInterval.addAll(planOrderItemToPayDetectedIntervalList);
                    }

                    // План по тем кто не в здании
                    planOrderItemToPayNotDetectedIntervalList = ClientsEntereventsService
                            .loadPlanOrderItemToPayNotDetected(session, sTt, eTt, idOfOrg);
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
                    for (PlanOrderItem planOrderItem : planOrderItemsToPayNotDetectedInterval) {
                        if (planOrderItemsPaidByInterval.contains(planOrderItem)) {
                            resultIntersectionInterval.add(planOrderItem);
                        }
                    }
                }

                if (!resultIntersectionInterval.isEmpty() || !resultSubtractionInterval.isEmpty()) {

                    DeviationPaymentItem deviationPaymentItem = new DeviationPaymentItem();

                    if (!resultIntersectionInterval.isEmpty()) {
                        Client client = (Client) session
                                .load(Client.class, resultIntersectionInterval.get(0).getIdOfClient());
                        Org org = (Org) session.load(Org.class, client.getOrg().getIdOfOrg());
                        deviationPaymentItem.setAddress(org.getAddress());
                        deviationPaymentItem.setOrgName(org.getShortName());
                    } else if (!resultSubtractionInterval.isEmpty()) {
                        Client client = (Client) session
                                .load(Client.class, resultSubtractionInterval.get(0).getIdOfClient());
                        Org org = (Org) session.load(Org.class, client.getOrg().getIdOfOrg());
                        deviationPaymentItem.setAddress(org.getAddress());
                        deviationPaymentItem.setOrgName(org.getShortName());
                    }

                    List<DeviationPaymentSubReportItem> deviationPaymentSubReportItemList = new ArrayList<DeviationPaymentSubReportItem>();

                    if (!resultSubtractionInterval.isEmpty()) {
                        fill(resultSubtractionInterval, conditionDetectedNotEat, complexInfoForPlanList,
                                deviationPaymentSubReportItemList);

                    }

                    if (!resultIntersectionInterval.isEmpty()) {
                        fill(resultIntersectionInterval, conditionNotDetectedEat, complexInfoForPlanList,
                                deviationPaymentSubReportItemList);
                    }

                    deviationPaymentItem.setDeviationPaymentSubReportItemList(deviationPaymentSubReportItemList);

                    deviationPaymentItemList.add(deviationPaymentItem);
                }
            }
        }

        return new JRBeanCollectionDataSource(deviationPaymentItemList);
    }

    public void fill(List<PlanOrderItem> result, String condition, List<ComplexInfoForPlan> complexInfoList,
            List<DeviationPaymentSubReportItem> deviationPaymentSubReportItemList) {

        for (PlanOrderItem planOrderItem : result) {

            DeviationPaymentSubReportItem deviationPaymentSubReportItem = new DeviationPaymentSubReportItem();

            deviationPaymentSubReportItem.setCondition(condition);
            deviationPaymentSubReportItem.setGroupName(planOrderItem.getGroupName());
            deviationPaymentSubReportItem.setPersonName(planOrderItem.getClientName());
            deviationPaymentSubReportItem.setOrderDate(planOrderItem.getOrderDate());
            deviationPaymentSubReportItem.setRuleId(planOrderItem.getIdOfRule());

            for (ComplexInfoForPlan complexInfoForPlan : complexInfoList) {
                if (complexInfoForPlan.getIdOfComplex().equals(planOrderItem.getIdOfComplex())&&complexInfoForPlan.getIdOfRule().equals(planOrderItem.getIdOfRule())) {
                    deviationPaymentSubReportItem.setComplexName(complexInfoForPlan.getComplexName());
                    break;
                }
            }
            deviationPaymentSubReportItemList.add(deviationPaymentSubReportItem);
        }
    }
}
