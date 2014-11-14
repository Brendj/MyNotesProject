/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ziganshin
 * Date: 28.10.14
 * Time: 13:23
 * To change this template use File | Settings | File Templates.
 */
public class RequestsAndOrdersReport extends BasicReportForAllOrgJob {

    final public static String P_HIDE_MISSED_COLUMNS = "hideMissedColumns";
    final public static String P_USE_COLOR_ACCENT = "useColorAccent";
    final public static String P_SHOW_ONLY_DIVERGENCE = "showOnlyDivergence";

    final private static Logger logger = LoggerFactory.getLogger(GoodRequestsNewReport.class);
    final private static long OVERALL = Long.MAX_VALUE - 10;
    final private static long OVERALL_TOTAL = Long.MAX_VALUE - 8;
    final private static String OVERALL_TOTAL_TITLE = "ВСЕГО";
    final private static String OVERALL_TITLE = "ИТОГО";

    public RequestsAndOrdersReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public RequestsAndOrdersReport() {
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new RequestsAndOrdersReport();
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {
            String reportsTemplateFilePath = RuntimeContext.getInstance().getAutoReportGenerator()
                    .getReportsTemplateFilePath();
            templateFilename = reportsTemplateFilePath + RequestsAndOrdersReport.class.getSimpleName() + ".jasper";
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            return build(session, startTime, endTime, calendar, false, false);  //To change body of implemented methods use File | Settings | File Templates.
        }

        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar,
                                Boolean useColorAccent, Boolean showOnlyDivergence) throws Exception {

            Date generateTime = new Date();

            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);
            parameterMap.put("useColorAccent", useColorAccent);

            // todo need complete testing - maybe useles code
            //calendar.setTime(startTime);
            //calendar.setTime(endTime);

            JRDataSource dataSource = createDataSource(session, startTime, endTime, parameterMap);

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);

            Date generateEndTime = new Date();

            long generateDuration = generateEndTime.getTime() - generateTime.getTime();
            return new GoodRequestsNewReport(generateTime, generateDuration, jasperPrint, startTime, endTime);
        }


        private JRDataSource createDataSource(Session session, Date startTime, Date endTime,
                Map<String, Object> parameterMap) throws Exception {

            String idOfOrgs = StringUtils.trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
            List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
            List<Long> idOfOrgList = new ArrayList<Long>(stringOrgList.size());
            for (String idOfOrg : stringOrgList) {
                idOfOrgList.add(Long.parseLong(idOfOrg));
            }

            String idOfMenuSourceOrgs = StringUtils
                    .trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_MENU_SOURCE_ORG));
            List<String> idOfMenuSourceOrgStrList = Arrays.asList(StringUtils.split(idOfMenuSourceOrgs, ','));
            List<Long> idOfMenuSourceOrgList = new ArrayList<Long>(idOfMenuSourceOrgStrList.size());
            for (String idOfMenuSourceOrg : idOfMenuSourceOrgStrList) {
                idOfMenuSourceOrgList.add(Long.parseLong(idOfMenuSourceOrg));
            }

            boolean hideMissedColumns = Boolean
                    .parseBoolean(reportProperties.getProperty(P_HIDE_MISSED_COLUMNS, "false"));

            boolean useColorAccent = Boolean
                    .parseBoolean(reportProperties.getProperty(P_USE_COLOR_ACCENT, "false"));

            boolean showOnlyDivergence = Boolean
                    .parseBoolean(reportProperties.getProperty(P_SHOW_ONLY_DIVERGENCE, "false"));

            RequestsAndOrdersReportService service;
            service = new RequestsAndOrdersReportService(session, OVERALL, OVERALL_TITLE);
            return new JRBeanCollectionDataSource(
                    service.buildReportItems(startTime, endTime, idOfOrgList, idOfMenuSourceOrgList, hideMissedColumns, useColorAccent, showOnlyDivergence));
        }
    }
}
