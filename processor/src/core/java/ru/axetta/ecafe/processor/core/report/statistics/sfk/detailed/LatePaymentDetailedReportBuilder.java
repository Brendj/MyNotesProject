/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.sfk.detailed;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.financialControlReports.LatePaymentDetailedReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 07.09.15
 * Time: 10:08
 */

public class LatePaymentDetailedReportBuilder extends BasicReportForAllOrgJob.Builder {

    private final String templateFilename;
    private final String subReportDir;

    public LatePaymentDetailedReportBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
        subReportDir = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath();
    }

    @Override
    public BasicReportForAllOrgJob build(Session session, Date startTime, Date endTime, Calendar calendar)
            throws Exception {
        if (!(new File(this.templateFilename)).exists()) {
            throw new Exception(String.format("Не найден файл шаблона '%s'", templateFilename));
        }

        String idOfOrgString = StringUtils
                .trimToEmpty(getReportProperties().getProperty(ReportPropertiesUtils.P_ID_OF_ORG));

        Long idOfOrg = Long.parseLong(idOfOrgString);

        Boolean showReserve = Boolean
                .valueOf(StringUtils.trimToEmpty(getReportProperties().getProperty("showReserve")));

        Boolean showRecycling = Boolean.
                valueOf(StringUtils.trimToEmpty(getReportProperties().getProperty("showRecycling")));

        Date generateBeginTime = new Date();

        /* Параметры для передачи в jasper */
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("startDate", CalendarUtils.dateToString(startTime));
        parameterMap.put("endDate", CalendarUtils.dateToString(endTime));
        parameterMap.put("IS_IGNORE_PAGINATION", true);
        parameterMap.put("SUBREPORT_DIR", subReportDir);
        JRDataSource recyclingDS = null;
        JRDataSource changeDS = null;
        List<LatePaymentDetailedReportModel> recyclingModelList = new ArrayList<LatePaymentDetailedReportModel>();
        List<LatePaymentDetailedReportModel> changeModelList = new ArrayList<LatePaymentDetailedReportModel>();
        JRDataSource dataSource = buildDataSource(session, idOfOrg, startTime, endTime, showReserve, showRecycling,
                recyclingModelList, changeModelList);

        if (!recyclingModelList.isEmpty())
            recyclingDS = new JRBeanCollectionDataSource(recyclingModelList);

        if (!changeModelList.isEmpty())
            changeDS = new JRBeanCollectionDataSource(changeModelList);

        parameterMap.put("recyclingDS", recyclingDS);
        parameterMap.put("changeDS", changeDS);
        JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
        Date generateEndTime = new Date();
        final long generateDuration = generateEndTime.getTime() - generateBeginTime.getTime();
        return new LatePaymentDetailedReport(generateBeginTime, generateDuration, jasperPrint, startTime, endTime);
    }

    private JRDataSource buildDataSource(Session session, Long idOfOrg, Date startTime, Date endTime,
            Boolean showReverse, Boolean showRecycling, List<LatePaymentDetailedReportModel> recyclingModelList,
            List<LatePaymentDetailedReportModel> changeModelList) throws Exception {

        LatePaymentDetailedReportService latePaymentDetailedReportService = new LatePaymentDetailedReportService();

        List<LatePaymentDetailedReportModel> latePaymentDetailedReportModelList = new ArrayList<LatePaymentDetailedReportModel>();

        latePaymentDetailedReportService.getMainData(session, idOfOrg, startTime, endTime, showReverse, showRecycling,
                        latePaymentDetailedReportModelList, recyclingModelList, changeModelList);

        return new JRBeanCollectionDataSource(latePaymentDetailedReportModelList);
    }
}
