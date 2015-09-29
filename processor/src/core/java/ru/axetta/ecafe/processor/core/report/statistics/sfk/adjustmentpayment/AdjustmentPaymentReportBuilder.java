/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.sfk.adjustmentpayment;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

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

        String idOfOrgs = StringUtils.trimToEmpty(getReportProperties().getProperty(ReportPropertiesUtils.P_ID_OF_ORG));

        Boolean showReverse = Boolean
                .valueOf(StringUtils.trimToEmpty(getReportProperties().getProperty("showReserve")));


        /*List<Long> idOFOrgList = new ArrayList<Long>();
        for (String idOfOrg : Arrays.asList(StringUtils.split(idOfOrgs, ','))) {
            idOFOrgList.add(Long.parseLong(idOfOrg));
        }*/

        Date generateBeginTime = new Date();

        /* Параметры передачи в jasper */
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("beginDate", CalendarUtils.dateToString(startTime));
        parameterMap.put("endDate", CalendarUtils.dateToString(endTime));
        JRDataSource dataSource = buildDataSource(session, Long.parseLong(idOfOrgs), startTime, endTime, showReverse);
        JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
        Date generateEndTime = new Date();
        final long generateDuration = generateEndTime.getTime() - generateBeginTime.getTime();
        return new AdjustmentPaymentReport(generateBeginTime, generateDuration, jasperPrint, startTime, endTime);
    }

    private JRDataSource buildDataSource(Session session, Long idOfOrg, Date startTime, Date endTime, Boolean showReverse)
            throws Exception {

        AdjustmentPaymentReportService adjustmentPaymentReportService = new AdjustmentPaymentReportService();

        List<AdjustmentPaymentReportModel> adjustmentPaymentReportModelList = adjustmentPaymentReportService.getMainData(session, idOfOrg, startTime, endTime, showReverse);

        return new JRBeanCollectionDataSource(adjustmentPaymentReportModelList);
    }
}
