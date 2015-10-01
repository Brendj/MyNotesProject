/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.sfk.adjustmentpayment;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.utils.FriendlyOrganizationsInfoModel;
import ru.axetta.ecafe.processor.core.persistence.utils.OrgUtils;
import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.financialControlReports.AdjustmentPaymentReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 28.09.15
 * Time: 11:54
 */

public class AdjustmentPaymentReportBuilder extends BasicReportForAllOrgJob.Builder {

    private final String templateFilename;

    public AdjustmentPaymentReportBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    @Override
    public BasicReportForAllOrgJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        if (!(new File(this.templateFilename)).exists()) {
            throw new Exception(String.format("Не найден файл шаблона '%s'", templateFilename));
        }

        String idOfOrg = StringUtils.trimToEmpty(getReportProperties().getProperty(ReportPropertiesUtils.P_ID_OF_ORG));

        List<Long> idOfOrgList = new ArrayList<Long>();
        idOfOrgList.add(Long.parseLong(idOfOrg));

        Set<FriendlyOrganizationsInfoModel> friendlyOrganizationsInfoModels = OrgUtils.getMainBuildingAndFriendlyOrgsList(session, idOfOrgList);

        Boolean showReverse = Boolean
                .valueOf(StringUtils.trimToEmpty(getReportProperties().getProperty("showReserve")));

        Date generateBeginTime = new Date();

        /* Параметры передачи в jasper */
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("beginDate", CalendarUtils.dateToString(startTime));
        parameterMap.put("endDate", CalendarUtils.dateToString(endTime));
        JRDataSource dataSource = buildDataSource(session, friendlyOrganizationsInfoModels, Long.parseLong(idOfOrg), startTime, endTime, showReverse);
        JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
        Date generateEndTime = new Date();
        final long generateDuration = generateEndTime.getTime() - generateBeginTime.getTime();
        return new AdjustmentPaymentReport(generateBeginTime, generateDuration, jasperPrint, startTime, endTime);
    }

    private JRDataSource buildDataSource(Session session, Set<FriendlyOrganizationsInfoModel> friendlyOrganizationsInfoModels, Long idOfOrg, Date startTime, Date endTime, Boolean showReverse)
            throws Exception {

        AdjustmentPaymentReportService adjustmentPaymentReportService = new AdjustmentPaymentReportService();

        FriendlyOrganizationsInfoModel friendlyOrganizationsInfoModel = null;

        for (FriendlyOrganizationsInfoModel organizationsInfoModel: friendlyOrganizationsInfoModels) {
            friendlyOrganizationsInfoModel = organizationsInfoModel;
        }

        AdjustmentPaymentReportModel adjustmentPaymentReportModel = adjustmentPaymentReportService.getMainData(session, startTime, endTime, friendlyOrganizationsInfoModel, idOfOrg);

        // Результирующий лист
        List<AdjustmentPaymentReportModel> adjustmentPaymentReportModelList = new ArrayList<AdjustmentPaymentReportModel>();

        adjustmentPaymentReportModelList.add(adjustmentPaymentReportModel);

        return new JRBeanCollectionDataSource(adjustmentPaymentReportModelList);
    }
}
