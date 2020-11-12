/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

public class GoodRequestsNewReport extends BasicReportForAllOrgJob {
    /*
   * Параметры отчета для добавления в правила и шаблоны
   *
   * При создании любого отчета необходимо добавить параметры:
   * REPORT_NAME - название отчета на русском
   * TEMPLATE_FILE_NAMES - названия всех jasper-файлов, созданных для отчета
   * IS_TEMPLATE_REPORT - добавлять ли отчет в шаблоны отчетов
   * PARAM_HINTS - параметры отчета (смотри ReportRuleConstants.PARAM_HINTS)
   * заполняется, если отчет добавлен в шаблоны (класс AutoReportGenerator)
   *
   * Затем КАЖДЫЙ класс отчета добавляется в массив ReportRuleConstants.ALL_REPORT_CLASSES
   */
    public static final String REPORT_NAME = "Сводный отчет по заявкам";
    public static final String[] TEMPLATE_FILE_NAMES = {"GoodRequestsNewReport.jasper", "GoodRequestsNewReport_export.jasper",
    "GoodRequestsNewReport_notify.jasper", "GoodRequestsNewReport_summary.jasper", "GoodRequestsReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};


    final private static Logger logger = LoggerFactory.getLogger(GoodRequestsNewReport.class);
    final private static String OVERALL = "";
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
    final public static String P_LAST_CREATE_OR_UPDATE_DATE = "lastCreateOrUpdateDate";
    final public static String P_HIDE_TOTAL_ROW = "hideTotalRow";
    final public static String P_NOTIFICATION = "notification";
    final public static String P_HIDE_PREORDERS = "hidePreorders";
    final public static String P_PREORDERS_ONLY = "preordersOnly";
    final public static String P_IS_EMAIL_NOTIFY = "isEmailNotify";
    final public static String P_NEED_FULL_GOOD_NAMES = "needFullGoodNames";


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
            JRDataSource dataSource = createDataSource(session, startTime, endTime, parameterMap, true);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
            Date generateEndTime = new Date();
            long generateDuration = generateEndTime.getTime() - generateTime.getTime();
            return new GoodRequestsNewReport(generateTime, generateDuration, jasperPrint, startTime, endTime);
        }

        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar,
                boolean isROSection) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startTime);
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);

            calendar.setTime(startTime);
            JRDataSource dataSource = createDataSource(session, startTime, endTime, parameterMap, isROSection);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
            Date generateEndTime = new Date();
            long generateDuration = generateEndTime.getTime() - generateTime.getTime();
            return new GoodRequestsNewReport(generateTime, generateDuration, jasperPrint, startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, Map<String,
                Object> parameterMap, boolean isROSection) throws Exception{
            boolean hideMissedColumns = Boolean.parseBoolean(reportProperties.getProperty(P_HIDE_MISSED_COLUMNS, "false"));
            boolean hideGeneratePeriod = Boolean.parseBoolean(reportProperties.getProperty(P_HIDE_GENERATE_PERIOD, "false"));
            String nameFilter = StringUtils.trim(reportProperties.getProperty(P_NAME_FILTER, ""));
            int orgFilter = Integer.parseInt(reportProperties.getProperty(P_ORG_REQUEST_FILTER, "1"));
            String hideDailySampleProperty = reportProperties.getProperty(P_HIDE_DAILY_SAMPLE_COUNT, "false");
            final int hideDailySampleValue = Boolean.parseBoolean(hideDailySampleProperty)?0:1;

            String fillDayOffProperty = reportProperties.getProperty(P_HIDE_DAILY_SAMPLE_COUNT, "false");
            final int fillDayOffValue = Boolean.parseBoolean(fillDayOffProperty)?0:1;

            String hideLastValueProperty = reportProperties.getProperty(P_HIDE_LAST_VALUE, "false");
            final int hideLastValue = Boolean.parseBoolean(hideLastValueProperty)?0:1;

            String hideTotalRowProperty = reportProperties.getProperty(P_HIDE_TOTAL_ROW, "false");
            final boolean hideTotalRow = Boolean.parseBoolean(hideTotalRowProperty);

            String defaultGenerateTime = Long.toString(System.currentTimeMillis());
            long generateBeginDate = Long.parseLong(reportProperties.getProperty(P_GENERATE_BEGIN_DATE, defaultGenerateTime));
            Date generateBeginTime = new Date(generateBeginDate);
            // на час
            long generateEndDate = Long.parseLong(reportProperties.getProperty(P_GENERATE_END_DATE, Long.toString(System.currentTimeMillis()+60*60*1000)));
            Date generateEndTime = new Date(generateEndDate);

            long lastCreateOrUpdateDate = Long.parseLong(reportProperties.getProperty(P_LAST_CREATE_OR_UPDATE_DATE, defaultGenerateTime));
            Date lastCreateOrUpdateDateTime = new Date(lastCreateOrUpdateDate);

            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy EE HH:mm:ss", new Locale("ru"));
            parameterMap.put(P_GENERATE_END_DATE, format.format(lastCreateOrUpdateDateTime));

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

            String notificationString = reportProperties.getProperty(P_NOTIFICATION, "false");
            final boolean notification = Boolean.parseBoolean(notificationString);

            String hidePreordersString = reportProperties.getProperty(P_HIDE_PREORDERS, "true");
            final boolean hidePreorders = Boolean.parseBoolean(hidePreordersString);

            String preordersOnlyString = reportProperties.getProperty(P_PREORDERS_ONLY, "false");
            final boolean preordersOnly = Boolean.parseBoolean(preordersOnlyString);

            String isEmailNotifyString = reportProperties.getProperty(P_IS_EMAIL_NOTIFY, "false");
            final boolean isEmailNotify = Boolean.parseBoolean(isEmailNotifyString);

            String needFullGoodNamesString = reportProperties.getProperty(P_NEED_FULL_GOOD_NAMES, "true");
            final boolean needFullGoodNames = Boolean.parseBoolean(needFullGoodNamesString);

            String idOfOrgString = StringUtils.trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
            if (isEmailNotify) {
                Long idOfOrg = Long.parseLong(idOfOrgString);

                parameterMap.put("idOfOrg", idOfOrg);
                Org org = (Org) session.load(Org.class, idOfOrg);

                if (null != org) {
                    parameterMap.put("address", org.getAddress());
                    parameterMap.put("shortName", org.getOfficialName());
                } else {
                    parameterMap.put("address", "не указан");
                    parameterMap.put("shortName", "не указано");
                }
            }

            // пока нет необходимости
            //parameterMap.put("overall",Long.toString(OVERALL));
            //parameterMap.put("overall_all",Long.toString(OVERALL_TOTAL));
            //GoodRequestsNewReportService service = new GoodRequestsNewReportService(session,
            //        OVERALL, OVERALL_TOTAL, OVERALL_TOTAL_TITLE, OVERALL_TITLE, hideTotalRow);
            GoodRequestsNewReportService service;
            service = new GoodRequestsNewReportService(session,OVERALL, OVERALL_TITLE, hideTotalRow);

            return new JRBeanCollectionDataSource(service.buildReportItems(startTime, endTime, nameFilter, orgFilter,
                    hideDailySampleValue, generateBeginTime, generateEndTime, idOfOrgList, idOfMenuSourceOrgList,
                    hideMissedColumns, hideGeneratePeriod, hideLastValue, notification, hidePreorders, preordersOnly,
                    needFullGoodNames, isROSection));
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