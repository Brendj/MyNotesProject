/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuleProcessor;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.model.requestsandorders.FeedingPlanType;
import ru.axetta.ecafe.processor.core.report.requestsAndOrdersReport.RequestsAndOrdersReportService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
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
 */
public class RequestsAndOrdersReport extends BasicReportForAllOrgJob {
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
    public static final String REPORT_NAME = "Расхождение данных по заявкам и заказам на период";
    public static final String[] TEMPLATE_FILE_NAMES = {"RequestsAndOrdersReport_export.jasper",
                                                        "RequestsAndOrdersReport_summary.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{3, 32, 40, 41, 42, 43, 44};


    final public static String P_HIDE_MISSED_COLUMNS = "hideMissedColumns";
    final public static String P_USE_COLOR_ACCENT = "useColorAccent";
    final public static String P_SHOW_ONLY_DIVERGENCE = "showOnlyDivergence";
    final public static String P_FEEDING_PLAN_TYPE = "feedingPlanType";
    final public static String P_NO_NULL_REPORT = "noNullReport";
    final private static Logger logger = LoggerFactory.getLogger(RequestsAndOrdersReport.class);
    final private static long OVERALL = Long.MAX_VALUE - 10;
    final private static String OVERALL_TITLE = "ИТОГО";

    public RequestsAndOrdersReport() {
    }

    public RequestsAndOrdersReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    private static HashSet<FeedingPlanType> getFeedingPlanTypes(String feedingType) {
        HashSet<FeedingPlanType> feedingPlanTypes = new HashSet<FeedingPlanType>();
        if (feedingType.equals(FeedingPlanType.PAY_PLAN.toString())) {
            feedingPlanTypes.add(FeedingPlanType.PAY_PLAN);
        } else if (feedingType.equals(FeedingPlanType.REDUCED_PRICE_PLAN.toString())) {
            feedingPlanTypes.add(FeedingPlanType.REDUCED_PRICE_PLAN);
        } else if (feedingType.equals(FeedingPlanType.SUBSCRIPTION_FEEDING.toString())) {
            feedingPlanTypes.add(FeedingPlanType.SUBSCRIPTION_FEEDING);
        } else {
            feedingPlanTypes.add(FeedingPlanType.PAY_PLAN);
            feedingPlanTypes.add(FeedingPlanType.SUBSCRIPTION_FEEDING);
            feedingPlanTypes.add(FeedingPlanType.REDUCED_PRICE_PLAN);
        }
        return feedingPlanTypes;
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

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_PREV_MONTH;
    }

    @Override
    public AutoReportRunner getAutoReportRunner() {

        return new AutoReportRunner() {
            public void run(AutoReportBuildTask autoReportBuildTask) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug(String.format("Building auto reports \"%s\"", getMyClass().getCanonicalName()));
                }
                String classPropertyValue = getMyClass().getCanonicalName();
                List<AutoReport> autoReports = new ArrayList<AutoReport>();
                Session session = null;
                org.hibernate.Transaction transaction = null;
                try {
                    session = autoReportBuildTask.sessionFactory.openSession();
                    transaction = BasicReport.createTransaction(session);
                    transaction.begin();

                    String jobId = autoReportBuildTask.jobId;
                    Long idOfSchedulerJob = Long.valueOf(jobId);

                    List<RuleProcessor.Rule> thisReportRulesList = getThisReportRulesList(session, idOfSchedulerJob);
                    for (RuleProcessor.Rule rule : thisReportRulesList) {
                        Properties properties = new Properties();
                        ReportPropertiesUtils.addProperties(properties, getMyClass(), autoReportBuildTask);
                        String idOfOrgsString = rule.getExpressionValue(ReportPropertiesUtils.P_ID_OF_ORG);
                        properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG,
                                idOfOrgsString == null ? "" : idOfOrgsString);

                        String showOnlyDivergenceString = rule.getExpressionValue(RequestsAndOrdersReport.P_SHOW_ONLY_DIVERGENCE);
                        properties.setProperty(RequestsAndOrdersReport.P_SHOW_ONLY_DIVERGENCE, showOnlyDivergenceString);

                        String hideMissedColumnsString = rule.getExpressionValue(RequestsAndOrdersReport.P_HIDE_MISSED_COLUMNS);
                        properties.setProperty(RequestsAndOrdersReport.P_HIDE_MISSED_COLUMNS, hideMissedColumnsString);

                        String useColorAccentString = rule.getExpressionValue(RequestsAndOrdersReport.P_USE_COLOR_ACCENT);
                        properties.setProperty(RequestsAndOrdersReport.P_USE_COLOR_ACCENT, useColorAccentString);

                        String reportPeriodType = rule.getExpressionValue(ReportPropertiesUtils.P_REPORT_PERIOD_TYPE);
                        properties.setProperty(ReportPropertiesUtils.P_REPORT_PERIOD_TYPE,
                                reportPeriodType == null ? "5" : reportPeriodType);

                        String feedingPlanType = rule.getExpressionValue(RequestsAndOrdersReport.P_FEEDING_PLAN_TYPE);
                        properties.setProperty(RequestsAndOrdersReport.P_FEEDING_PLAN_TYPE,
                                feedingPlanType == null ? "Все" : feedingPlanType);

                        String noNullReport = rule.getExpressionValue(RequestsAndOrdersReport.P_NO_NULL_REPORT);
                        properties.setProperty(RequestsAndOrdersReport.P_NO_NULL_REPORT,
                                noNullReport == null ? "false" : noNullReport);

                        BasicReportForAllOrgJob report = createInstance();
                        report.initialize(autoReportBuildTask.startTime, autoReportBuildTask.endTime,
                                autoReportBuildTask.templateFileName, autoReportBuildTask.sessionFactory,
                                autoReportBuildTask.startCalendar);
                        autoReports.add(new AutoReport(report, properties));
                    }

                    List<Long> reportHandleRuleList = getRulesIdsByJobRules(session, idOfSchedulerJob);

                    transaction.commit();
                    transaction = null;
                    autoReportBuildTask.executorService.execute(
                            new AutoReportProcessor.ProcessTask(autoReportBuildTask.autoReportProcessor, autoReports,
                                    autoReportBuildTask.documentBuilders, reportHandleRuleList));
                } catch (Exception e) {
                    getLogger().error(String.format("Failed at building auto reports \"%s\"", classPropertyValue), e);
                } finally {
                    HibernateUtils.rollback(transaction, getLogger());
                    HibernateUtils.close(session, getLogger());
                }

            }
        };
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

            Date generateTime = new Date();

            JRDataSource dataSource = createDataSource(session, startTime, endTime);
            if (dataSource == null) return null;

            boolean useColorAccent = Boolean
                    .parseBoolean(getReportProperties().getProperty(P_USE_COLOR_ACCENT, "false"));
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);
            parameterMap.put("useColorAccent", useColorAccent);

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);

            Date generateEndTime = new Date();

            long generateDuration = generateEndTime.getTime() - generateTime.getTime();
            return new RequestsAndOrdersReport(generateTime, generateDuration, jasperPrint, startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime) throws Exception {

            String idOfOrgs = getReportProperties().getProperty(ReportPropertiesUtils.P_ID_OF_ORG, "");
            List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
            List<Long> idOfOrgList = new ArrayList<Long>(stringOrgList.size());
            for (String idOfOrg : stringOrgList) {
                idOfOrgList.add(Long.parseLong(idOfOrg));
            }

            String idOfMenuSourceOrgs = getReportProperties()
                    .getProperty(ReportPropertiesUtils.P_ID_OF_MENU_SOURCE_ORG, "");
            List<String> idOfMenuSourceOrgStrList = Arrays.asList(StringUtils.split(idOfMenuSourceOrgs, ','));
            List<Long> idOfMenuSourceOrgList = new ArrayList<Long>(idOfMenuSourceOrgStrList.size());
            for (String idOfMenuSourceOrg : idOfMenuSourceOrgStrList) {
                idOfMenuSourceOrgList.add(Long.parseLong(idOfMenuSourceOrg));
            }

            Boolean hideMissedColumns = Boolean
                    .parseBoolean(getReportProperties().getProperty(P_HIDE_MISSED_COLUMNS, "false"));

            Boolean useColorAccent = Boolean
                    .parseBoolean(getReportProperties().getProperty(P_USE_COLOR_ACCENT, "false"));

            Boolean showOnlyDivergence = Boolean
                    .parseBoolean(getReportProperties().getProperty(P_SHOW_ONLY_DIVERGENCE, "false"));

            HashSet<FeedingPlanType> feedingPlanTypes = getFeedingPlanTypes(
                    getReportProperties().getProperty(P_FEEDING_PLAN_TYPE, "Все"));

            Boolean noNullReport = Boolean
                    .parseBoolean(getReportProperties().getProperty(P_NO_NULL_REPORT, "false"));

            RequestsAndOrdersReportService service;
            service = new RequestsAndOrdersReportService(session, OVERALL, OVERALL_TITLE);
            List list = service.buildReportItems(startTime, endTime, idOfOrgList, idOfMenuSourceOrgList, hideMissedColumns,
                    useColorAccent, showOnlyDivergence, feedingPlanTypes, noNullReport);
            if (list == null)
                return null;
            else
                return new JRBeanCollectionDataSource(list);
        }
    }

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }
}
