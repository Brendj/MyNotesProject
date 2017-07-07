/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.sfk.latepayment;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.financialControlReports.LatePaymentReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Алмаз
 * Date: 03.09.15
 * Time: 14:46
 * To change this template use File | Settings | File Templates.
 */
public class LatePaymentReportBuilder extends BasicReportForAllOrgJob.Builder {

    private final String templateFilename;


    public LatePaymentReportBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    @Override
    public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        if (!(new File(this.templateFilename)).exists()) {
            throw new Exception(String.format("Не найден файл шаблона '%s'", templateFilename));
        }

        String idOfOrgs = StringUtils.trimToEmpty(getReportProperties().getProperty(ReportPropertiesUtils.P_ID_OF_ORG));

        String latePaymentDaysCountType = StringUtils.trimToEmpty(getReportProperties().getProperty("latePaymentDaysCountType"));

        String latePaymentByOneDayCountType = StringUtils.trimToEmpty(getReportProperties().getProperty("latePaymentByOneDayCountType"));

        List<Long> idOFOrgList = new ArrayList<Long>();
        for (String idOfOrg : Arrays.asList(StringUtils.split(idOfOrgs, ','))) {
            idOFOrgList.add(Long.parseLong(idOfOrg));
        }

        Date generateBeginTime = new Date();

        /* Параметры передачи в jasper */
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("beginDate", CalendarUtils.dateToString(startTime));
        parameterMap.put("endDate", CalendarUtils.dateToString(endTime));
        parameterMap.put("currentDate", CalendarUtils.dateToString(new Date()));
        JRDataSource dataSource = buildDataSource(session, idOFOrgList, startTime, endTime, latePaymentDaysCountType, latePaymentByOneDayCountType);
        JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
        Date generateEndTime = new Date();
        final long generateDuration = generateEndTime.getTime() - generateBeginTime.getTime();
        return new LatePaymentReport(generateBeginTime, generateDuration, jasperPrint, startTime, endTime);
    }

    private JRDataSource buildDataSource(Session session, List<Long> idOfOrg, Date startTime, Date endTime,
            String latePaymentDaysCountType, String latePaymentByOneDayCountType) {

        LatePaymentReportService latePaymentReportService = new LatePaymentReportService();
        List<LatePaymentReportModel> latePaymentReportModelList = latePaymentReportService.getCountOfBeneficiariesByOrg(session, idOfOrg, startTime, endTime, latePaymentDaysCountType, latePaymentByOneDayCountType);

        return new JRBeanCollectionDataSource(latePaymentReportModelList);
    }
}
