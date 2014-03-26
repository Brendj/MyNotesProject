/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
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

public class GoodRequestsNewReport extends BasicReportForAllOrgJob {

    final private static Logger logger = LoggerFactory.getLogger(GoodRequestsNewReport.class);
    final private static long OVERALL = Long.MAX_VALUE - 10;
    final private static long OVERALL_TOTAL = Long.MAX_VALUE - 8;
    final private static String OVERALL_TOTAL_TITLE = "ВСЕГО";
    final private static String OVERALL_TITLE = "ИТОГО";
    final public static String P_HIDE_MISSED_COLUMNS = "hideMissedColumns";
    final public static String P_HIDE_GENERATE_PERIOD = "hideGeneratePeriod";
    final public static String P_NAME_FILTER = "nameFilter";
    final public static String P_ORG_REQUEST_FILTER = "orgRequestFilter";
    final public static String P_HIDE_DAILY_SAMPLE_COUNT = "hideDailySampleCount";
    final public static String P_HIDE_LAST_VALUE = "hideLastValue";
    final public static String P_GENERATE_BEGIN_DATE = "generateBeginDate";
    final public static String P_GENERATE_END_DATE = "generateEndDate";

    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {
            String reportsTemplateFilePath = RuntimeContext.getInstance().getAutoReportGenerator()
                    .getReportsTemplateFilePath();
            templateFilename = reportsTemplateFilePath + GoodRequestsNewReport.class.getSimpleName() + ".jasper";
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startTime);
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);

            calendar.setTime(startTime);
            JRDataSource dataSource = createDataSource(session, startTime, endTime, parameterMap);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
            Date generateEndTime = new Date();
            long generateDuration = generateEndTime.getTime() - generateTime.getTime();
            return new GoodRequestsNewReport(generateTime, generateDuration, jasperPrint, startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, Map<String, Object> parameterMap) throws Exception{
            boolean hideMissedColumns = Boolean.parseBoolean(reportProperties.getProperty(P_HIDE_MISSED_COLUMNS, "false"));
            boolean hideGeneratePeriod = Boolean.parseBoolean(reportProperties.getProperty(P_HIDE_GENERATE_PERIOD, "false"));
            String nameFilter = StringUtils.trim(reportProperties.getProperty(P_NAME_FILTER, ""));
            int orgFilter = Integer.parseInt(reportProperties.getProperty(P_ORG_REQUEST_FILTER, "1"));
            String hideDailySampleProperty = reportProperties.getProperty(P_HIDE_DAILY_SAMPLE_COUNT, "false");
            final int hideDailySampleValue = Boolean.parseBoolean(hideDailySampleProperty)?0:1;

            String hideLastValueProperty = reportProperties.getProperty(P_HIDE_LAST_VALUE, "false");
            final int hideLastValue = Boolean.parseBoolean(hideLastValueProperty)?0:1;

            String defaultGenerateTime = Long.toString(System.currentTimeMillis());
            long generateBeginDate = Long.parseLong(reportProperties.getProperty(P_GENERATE_BEGIN_DATE, defaultGenerateTime));
            Date generateBeginTime = new Date(generateBeginDate);
            // на час
            long generateEndDate = Long.parseLong(reportProperties.getProperty(P_GENERATE_END_DATE, defaultGenerateTime+60*60*1000));
            Date generateEndTime = new Date(generateEndDate);

            String idOfOrgs = StringUtils.trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
            List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
            List<Long> idOfOrgList = new ArrayList<Long>(stringOrgList.size());
            for (String idOfOrg : stringOrgList) {
                idOfOrgList.add(Long.parseLong(idOfOrg));
            }

            String idOfMenuSourceOrgs = StringUtils.trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_MENU_SOURCE_ORG));
            List<String> idOfMenuSourceOrgStrList = Arrays.asList(StringUtils.split(idOfMenuSourceOrgs, ','));
            List<Long> idOfMenuSourceOrgList = new ArrayList<Long>(idOfMenuSourceOrgStrList.size());
            for (String idOfMenuSourceOrg : idOfMenuSourceOrgStrList) {
                idOfMenuSourceOrgList.add(Long.parseLong(idOfMenuSourceOrg));
            }

            // пока нет необходимости
            //parameterMap.put("overall",Long.toString(OVERALL));
            //parameterMap.put("overall_all",Long.toString(OVERALL_TOTAL));
            GoodRequestsNewReportService service = new GoodRequestsNewReportService(session,
                    OVERALL, OVERALL_TOTAL, OVERALL_TOTAL_TITLE, OVERALL_TITLE);

            return new JRBeanCollectionDataSource(service.buildRepotItems(startTime, endTime, nameFilter, orgFilter,
                    hideDailySampleValue, generateBeginTime, generateEndTime, idOfOrgList, idOfMenuSourceOrgList,
                    hideMissedColumns, hideGeneratePeriod, hideLastValue));
        }



    }

    public GoodRequestsNewReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public GoodRequestsNewReport() {}

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new GoodRequestsNewReport();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BasicReportJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;  //To change body of implemented methods use File | Settings | File Templates.
    }
}