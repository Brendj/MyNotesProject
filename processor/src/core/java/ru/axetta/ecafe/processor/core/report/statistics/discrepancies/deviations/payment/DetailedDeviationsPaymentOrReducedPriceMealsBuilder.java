package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
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

        // Те кто были в здании
        // Те кто дожны были получить бесплатное питание
        List<PlanOrderItem> planOrderItemsToPay = new ArrayList<PlanOrderItem>();
        // Те кто получил бесплатное питание
        List<PlanOrderItem> planOrderItemsPayd /*= new ArrayList<PlanOrderItem>()*/;

        // План питания льготники
        String orderTypeLgotnick = "4,6,8";

        if (CalendarUtils.truncateToDayOfMonth(startTime).equals(CalendarUtils.truncateToDayOfMonth(endTime))) {

            for (Long idOfOrg : idOfOrgList) {
                planOrderItemsToPay = ClientsEntereventsService.loadPlanOrderItemToPay(session, startTime, idOfOrg);
            }

            Date addOneDayEndTime = CalendarUtils.addOneDay(endTime);

            planOrderItemsPayd = ClientsEntereventsService
                    .loadPaidPlanOrderInfo(session, orderTypeLgotnick, idOfOrgList, startTime, addOneDayEndTime);

            if (!planOrderItemsPayd.isEmpty() && !planOrderItemsToPay.isEmpty()) {
                for (PlanOrderItem planOrderItem : planOrderItemsPayd) {
                    planOrderItem.getOrderDate();
                }
            }
        }
        //для тестов
        Integer i = new Integer(10);
        Long l = new Long(30L);
        Long l1 = new Long(50L);

        DeviationPaymentSubReportItem deviationPaymentSubReportItem = new DeviationPaymentSubReportItem("1Б",
                "Иван Васильевич", "Проход по карте зафиксирован, питание не предоставлено");
        DeviationPaymentSubReportItem deviationPaymentSubReportItem2 = new DeviationPaymentSubReportItem("1В",
                "Иван Васильевич1", "Проход по карте зафиксирован, питание не предоставлено");
        DeviationPaymentSubReportItem deviationPaymentSubReportItem3 = new DeviationPaymentSubReportItem("11А",
                "Иванов Василий Петрович", "Проход по карте зафиксирован, питание не предоставлено");
        DeviationPaymentSubReportItem deviationPaymentSubReportItem4 = new DeviationPaymentSubReportItem("11Г",
                "Иванов Петр", "Проход по карте не зафиксирован, питание предоставлено");
        DeviationPaymentSubReportItem deviationPaymentSubReportItem5 = new DeviationPaymentSubReportItem("11Г",
                "Сидоров Иван Генадьевич", "Проход по карте не зафиксирован, питание предоставлено");

        List<DeviationPaymentSubReportItem> deviationPaymentSubReportItemList = new ArrayList<DeviationPaymentSubReportItem>();
        deviationPaymentSubReportItemList.add(deviationPaymentSubReportItem);

        List<DeviationPaymentSubReportItem> deviationPaymentSubReportItemList2 = new ArrayList<DeviationPaymentSubReportItem>();
        deviationPaymentSubReportItemList2.add(deviationPaymentSubReportItem2);

        List<DeviationPaymentSubReportItem> deviationPaymentSubReportItemList3 = new ArrayList<DeviationPaymentSubReportItem>();
        deviationPaymentSubReportItemList3.add(deviationPaymentSubReportItem3);
        deviationPaymentSubReportItemList3.add(deviationPaymentSubReportItem4);
        deviationPaymentSubReportItemList3.add(deviationPaymentSubReportItem5);


        DeviationPaymentItem deviationPaymentItem1 = new DeviationPaymentItem("Новая организация", "Улица новая",
                deviationPaymentSubReportItemList2);
        deviationPaymentItemList.add(deviationPaymentItem1);

        DeviationPaymentItem deviationPaymentItem2 = new DeviationPaymentItem("ГОУ СОШ 495", "Белая Улица",
                deviationPaymentSubReportItemList3);
        deviationPaymentItemList.add(deviationPaymentItem2);

        DeviationPaymentItem deviationPaymentItem3 = new DeviationPaymentItem("ГОУ СОШ 499", "Победилова 5",
                deviationPaymentSubReportItemList3);
        deviationPaymentItemList.add(deviationPaymentItem3);

        DeviationPaymentItem deviationPaymentItem4 = new DeviationPaymentItem("Новая организация3", "Улица новая1",
                deviationPaymentSubReportItemList);
        deviationPaymentItemList.add(deviationPaymentItem4);

        return new JRBeanCollectionDataSource(deviationPaymentItemList);
    }
}
