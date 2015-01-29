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
import ru.axetta.ecafe.processor.core.persistence.utils.DetailedDeviationWithoutCorpsService;
import ru.axetta.ecafe.processor.core.persistence.utils.FriendlyOrganizationsInfoModel;
import ru.axetta.ecafe.processor.core.persistence.utils.OrgUtils;
import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.ClientInfo;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.DeviationPaymentItem;
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

                    // Оплаченные Заказы
                    List<PlanOrderItem> planOrderItemsPaidByOneDay = DetailedDeviationWithoutCorpsService
                            .loadPaidPlanOrderInfo(session, orderTypeLgotnick, idOfOrgList, startTime,
                                    addOneDayEndTime);

                    System.out.println("");

                    List<ClientInfo> clientInfoList = DetailedDeviationWithoutCorpsService
                            .loadClientsInfoToPayDetected(session, startTime, addOneDayEndTime, idOfOrgList);

                    System.out.println("");

                    List<PlanOrderItem> loadPlanOrderItemToPayDetectedList = DetailedDeviationWithoutCorpsService
                            .loadPlanOrderItemToPayDetected(session, startTime, addOneDayEndTime, idOfOrgList);

                    System.out.println("");
                }
            }


        } else {
            Long rowNum = 0L;
        }

        return new JRBeanCollectionDataSource(deviationPaymentItemList);
    }
}
