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

    public DetailedDeviationsPaymentOrReducedPriceMealsBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
        subReportDir = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath();
    }

    public DetailedDeviationsPaymentOrReducedPriceMealsBuilder() {
        templateFilename = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath()
                + DetailedDeviationsPaymentOrReducedPriceMealsJasperReport.class.getSimpleName() + ".jasper";
        subReportDir = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath();
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
        JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
        Date generateEndTime = new Date();
        final long generateDuration = generateEndTime.getTime() - generateBeginTime.getTime();
        return new DetailedDeviationsPaymentOrReducedPriceMealsJasperReport(generateBeginTime, generateDuration,
                jasperPrint, startTime, endTime);
    }

    @SuppressWarnings("unchecked")
    private JRDataSource buildDataSource(Session session, List<Long> idOfOrgList, Date startTime, Date endTime)
            throws Exception {

        List<DeviationPaymentItem> deviationPaymentItemList = new ArrayList<DeviationPaymentItem>();

        // Те кто дожны были получить бесплатное питание | Проход по карте зафиксирован
        List<PlanOrderItem> planOrderItemsToPayDetected = new ArrayList<PlanOrderItem>();

        // Те кто дожны были получить бесплатное питание | Проход по карте не зафиксирован
        List<PlanOrderItem> planOrderItemsToPayNotDetected = new ArrayList<PlanOrderItem>();

        // Те кто получил бесплатное питание
        List<PlanOrderItem> planOrderItemsPayd;

        // План питания льготники
        String orderTypeLgotnick = "4,6,8";

        if (CalendarUtils.truncateToDayOfMonth(startTime).equals(CalendarUtils.truncateToDayOfMonth(endTime))) {

            Date addOneDayEndTime = CalendarUtils.addOneDay(startTime);
            CalendarUtils.truncateToDayOfMonth(addOneDayEndTime);

            for (Long idOfOrg : idOfOrgList) {
                List<PlanOrderItem> planOrderItemToPayDetectedList = ClientsEntereventsService
                        .loadPlanOrderItemToPayDetected(session, startTime, addOneDayEndTime, idOfOrg);
                if (planOrderItemToPayDetectedList != null) {
                    planOrderItemsToPayDetected.addAll(planOrderItemToPayDetectedList);
                }

                List<PlanOrderItem> planOrderItemToPayNotDetectedList = ClientsEntereventsService
                        .loadPlanOrderItemToPayNotDetected(session, startTime, addOneDayEndTime, idOfOrg);
                if (planOrderItemToPayNotDetectedList != null) {
                    planOrderItemsToPayNotDetected.addAll(planOrderItemToPayNotDetectedList);
                }
            }

            planOrderItemsPayd = ClientsEntereventsService
                    .loadPaidPlanOrderInfo(session, orderTypeLgotnick, idOfOrgList, startTime, addOneDayEndTime);

            String conditionDetectedNotEat = "Проход по карте зафиксирован, питание не предоставлено";
            String conditionNotDetectedEat = "Проход по карте не зафиксирован, питание предоставлено";

            List<PlanOrderItem> resultSubtraction = new ArrayList<PlanOrderItem>();
            if (!planOrderItemsPayd.isEmpty() && !planOrderItemsToPayDetected.isEmpty()) {
                for (PlanOrderItem planOrderItem : planOrderItemsToPayDetected) {
                    if (!planOrderItemsPayd.contains(planOrderItem)) {
                        resultSubtraction.add(planOrderItem);
                    }
                }

                DeviationPaymentItem deviationPaymentItem = null;

                List<DeviationPaymentSubReportItem> deviationPaymentSubReportItemList = new ArrayList<DeviationPaymentSubReportItem>();
                if (!resultSubtraction.isEmpty()) {
                    for (PlanOrderItem planOrderItemResult : resultSubtraction) {
                        Client client = (Client) session.load(Client.class, planOrderItemResult.getIdOfClient());
                        Org org = (Org) session.load(Org.class, client.getOrg().getIdOfOrg());
                        deviationPaymentItem = new DeviationPaymentItem();
                        deviationPaymentItem.setAddress(org.getAddress());
                        deviationPaymentItem.setOrgName(org.getShortName());

                        DeviationPaymentSubReportItem deviationPaymentSubReportItem = new DeviationPaymentSubReportItem();
                        deviationPaymentSubReportItem.setCondition(conditionDetectedNotEat);
                        deviationPaymentSubReportItem.setGroupName(planOrderItemResult.getGroupName());
                        deviationPaymentSubReportItem.setPersonName(planOrderItemResult.getClientName());

                        deviationPaymentSubReportItemList.add(deviationPaymentSubReportItem);

                        deviationPaymentItem.setDeviationPaymentSubReportItemList(deviationPaymentSubReportItemList);
                    }
                    deviationPaymentItemList.add(deviationPaymentItem);
                }
            }

            List<PlanOrderItem> resultIntersection = new ArrayList<PlanOrderItem>();
            if (!planOrderItemsPayd.isEmpty() && !planOrderItemsToPayNotDetected.isEmpty()) {

                for (PlanOrderItem planOrderItem : planOrderItemsToPayNotDetected) {
                    if (planOrderItemsPayd.contains(planOrderItem)) {
                        resultIntersection.add(planOrderItem);
                    }
                }

                DeviationPaymentItem deviationPaymentItemNot = null;

                List<DeviationPaymentSubReportItem> deviationPaymentSubReportItemNotList = new ArrayList<DeviationPaymentSubReportItem>();
                if (!resultIntersection.isEmpty()) {
                    for (PlanOrderItem planOrderItemResult : resultIntersection) {
                        Client client = (Client) session.load(Client.class, planOrderItemResult.getIdOfClient());
                        Org org = (Org) session.load(Org.class, client.getOrg().getIdOfOrg());
                        deviationPaymentItemNot = new DeviationPaymentItem();
                        deviationPaymentItemNot.setAddress(org.getAddress());
                        deviationPaymentItemNot.setOrgName(org.getShortName());

                        DeviationPaymentSubReportItem deviationPaymentSubReportItemNot = new DeviationPaymentSubReportItem();
                        deviationPaymentSubReportItemNot.setCondition(conditionNotDetectedEat);
                        deviationPaymentSubReportItemNot.setGroupName(planOrderItemResult.getGroupName());
                        deviationPaymentSubReportItemNot.setPersonName(planOrderItemResult.getClientName());

                        deviationPaymentSubReportItemNotList.add(deviationPaymentSubReportItemNot);

                        deviationPaymentItemNot
                                .setDeviationPaymentSubReportItemList(deviationPaymentSubReportItemNotList);
                    }
                    deviationPaymentItemList.add(deviationPaymentItemNot);
                }
            }
        }

        return new JRBeanCollectionDataSource(deviationPaymentItemList);
    }
}
