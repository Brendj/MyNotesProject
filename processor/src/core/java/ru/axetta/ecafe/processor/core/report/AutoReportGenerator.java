/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.JobRules;
import ru.axetta.ecafe.processor.core.persistence.SchedulerJob;
import ru.axetta.ecafe.processor.core.report.complianceWithOrderAndConsumption.CWOACReport;
import ru.axetta.ecafe.processor.core.report.feeding.SubscriptionFeedingJasperReport;
import ru.axetta.ecafe.processor.core.report.financialControlReports.AdjustmentPaymentReport;
import ru.axetta.ecafe.processor.core.report.financialControlReports.LatePaymentDetailedReport;
import ru.axetta.ecafe.processor.core.report.financialControlReports.LatePaymentReport;
import ru.axetta.ecafe.processor.core.report.kzn.BeneficiaryByAllOrgReport;
import ru.axetta.ecafe.processor.core.report.kzn.SalesReport;
import ru.axetta.ecafe.processor.core.report.maussp.ContragentOrderCategoryReport;
import ru.axetta.ecafe.processor.core.report.maussp.ContragentOrderReport;
import ru.axetta.ecafe.processor.core.report.msc.*;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.without.corps.DetailedDeviationsWithoutCorpsIntervalJasperReport;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.without.corps.DetailedDeviationsWithoutCorpsJasperReport;
import ru.axetta.ecafe.processor.core.report.summarySalesToSchools.SSTSReport;
import ru.axetta.ecafe.processor.core.utils.ExecutorServiceWrappedJob;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.io.FilenameUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 15.12.2009
 * Time: 13:55:14
 * To change this template use File | Settings | File Templates.
 */
public class AutoReportGenerator {


    public static class ReportDef {
        Class<? extends ExecutorServiceWrappedJob> buildJobClass;
        Class<? extends BasicReport> reportClass;
        JobDetailCreator jobDetailCreator;

        ReportDef(Class<? extends BasicReport> reportClass, Class<? extends ExecutorServiceWrappedJob> buildJobClass, JobDetailCreator jobDetailCreator) {
            this.buildJobClass = buildJobClass;
            this.jobDetailCreator = jobDetailCreator;
            this.reportClass = reportClass;
        }

        String getReportType() {
            return reportClass.getCanonicalName();
        }

        public JobDetailCreator getJobDetailCreator (){
            return jobDetailCreator;
        }
    }

    public static LinkedList<ReportDef> REPORT_DEFS=new LinkedList<ReportDef>();

    private static final String REPORT_JOBS_BASE_CLASS_NAME;

    static {
        String basicReportType = BasicReport.class.getCanonicalName();
        int i = basicReportType.lastIndexOf('.');
        if (i > 0) {
            REPORT_JOBS_BASE_CLASS_NAME = basicReportType.substring(0, i);
        } else {
            REPORT_JOBS_BASE_CLASS_NAME = basicReportType;
        }
    }

    public static Criteria createEnabledReportJobsCriteria(Session session) throws Exception {
        return session.createCriteria(SchedulerJob.class)
                .add(Restrictions.like("jobClass", REPORT_JOBS_BASE_CLASS_NAME + ".%", MatchMode.START)).addOrder(
                        Order.asc("jobName"));
    }

    public static interface JobDetailCreator {

        public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception;
    }

    static {
        REPORT_DEFS.add(new ReportDef(OrgBalanceReport.class, OrgBalanceReport.BuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = OrgBalanceReport.BuildJob.class;
                OrgBalanceReport.BuildJob.ExecuteEnvironment executeEnvironment = new OrgBalanceReport.BuildJob.ExecuteEnvironment(
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());
                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap().put(OrgBalanceReport.BuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(ClientGroupBalanceReport.class, ClientGroupBalanceReport.BuildJob.class,
                new JobDetailCreator() {
                    public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName)
                            throws Exception {
                        Class jobClass = ClientGroupBalanceReport.BuildJob.class;
                        ClientGroupBalanceReport.BuildJob.ExecuteEnvironment executeEnvironment = new ClientGroupBalanceReport.BuildJob.ExecuteEnvironment(
                                autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                                autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                                (DateFormat) autoReportGenerator.getDateFormat().clone(),
                                (DateFormat) autoReportGenerator.getTimeFormat().clone());
                        JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                        jobDetail.getJobDataMap()
                                .put(ClientGroupBalanceReport.BuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                        return jobDetail;
                    }
                }));

        REPORT_DEFS.add(new ReportDef(ContragentOrderReport.class, ContragentOrderReport.BuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                final String CONTRAGENT_ORDER_REPORT_TEMPLATE_KEY = "ContragentOrderReport.template";

                Class jobClass = ContragentOrderReport.BuildJob.class;

                //String reportTemplate = autoReportGenerator.getReportProperties()
                //        .getProperty(CONTRAGENT_ORDER_REPORT_TEMPLATE_KEY);
                //if (StringUtils.isEmpty(reportTemplate)) {
                //    throw new IllegalArgumentException(
                //            String.format("Report property \"%s\" not found. Can\'t schedule ContragentOrderReport",
                //                    CONTRAGENT_ORDER_REPORT_TEMPLATE_KEY));
                //}
                //reportTemplate = restoreFilename(autoReportGenerator.getBasePath(), reportTemplate);
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + ContragentOrderReport.class.getSimpleName() + ".jasper";

                ContragentOrderReport.BuildJob.ExecuteEnvironment executeEnvironment = new ContragentOrderReport.BuildJob.ExecuteEnvironment(
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap().put(ContragentOrderReport.BuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(ContragentOrderCategoryReport.class, ContragentOrderCategoryReport.BuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                final String CONTRAGENT_ORDER_CATEGORRY_REPORT_TEMPLATE_KEY = "ContragentOrderCategoryReport.template";

                Class jobClass = ContragentOrderCategoryReport.BuildJob.class;

                //String reportTemplate = autoReportGenerator.getReportProperties()
                //        .getProperty(CONTRAGENT_ORDER_CATEGORRY_REPORT_TEMPLATE_KEY);
                //if (StringUtils.isEmpty(reportTemplate)) {
                //    throw new IllegalArgumentException(String.format(
                //            "Report property \"%s\" not found. Can\'t schedule ContragentOrderCategoryReport",
                //            CONTRAGENT_ORDER_CATEGORRY_REPORT_TEMPLATE_KEY));
                //}
                //reportTemplate = restoreFilename(autoReportGenerator.getBasePath(), reportTemplate);
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + ContragentOrderCategoryReport.class.getSimpleName() + ".jasper";

                ContragentOrderCategoryReport.BuildJob.ExecuteEnvironment executeEnvironment = new ContragentOrderCategoryReport.BuildJob.ExecuteEnvironment(
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(ContragentOrderCategoryReport.BuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(OrgOrderCategoryReport.class, OrgOrderCategoryReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                final String REPORT_TEMPLATE_KEY = "OrgOrderCategoryReport.template";

                Class jobClass = BasicReportJob.AutoReportBuildJob.class;

                //String reportTemplate = autoReportGenerator.getReportProperties()
                //        .getProperty(REPORT_TEMPLATE_KEY);
                //if (StringUtils.isEmpty(reportTemplate)) {
                //    throw new IllegalArgumentException(String.format(
                //            "Report property \"%s\" not found. Can\'t schedule OrgOrderCategoryReport",
                //            REPORT_TEMPLATE_KEY));
                //}
                //reportTemplate = restoreFilename(autoReportGenerator.getBasePath(), reportTemplate);
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + OrgOrderCategoryReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new OrgOrderCategoryReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(OrgOrderCategoryReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(SalesReport.class, SalesReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + SalesReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new SalesReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(SalesReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(MscSalesReport.class, MscSalesReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + MscSalesReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new MscSalesReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(MscSalesReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(RegisterReport.class, RegisterReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + RegisterReport.class.getSimpleName() + ".jasper";
                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new RegisterReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(RegisterReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(ClientsReport.class, ClientsReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + ClientsReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new ClientsReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(ClientsReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(OrgOrderByDaysReport.class, OrgOrderByDaysReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + OrgOrderByDaysReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new OrgOrderByDaysReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(OrgOrderByDaysReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(AutoEnterEventReport.class, AutoEnterEventReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + AutoEnterEventReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new AutoEnterEventReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(AutoEnterEventReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(AutoEnterEventByDaysReport.class, AutoEnterEventByDaysReport.AutoReportBuildJob.class, new JobDetailCreator() {
                    public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                        Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                        // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                        String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + AutoEnterEventByDaysReport.class.getSimpleName() + ".jasper";

                        BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                                jobId,
                                jobName,
                                new AutoEnterEventByDaysReport(),
                                autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                                autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                                reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                                (DateFormat) autoReportGenerator.getDateFormat().clone(),
                                (DateFormat) autoReportGenerator.getTimeFormat().clone());

                        JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                        jobDetail.getJobDataMap()
                                .put(AutoEnterEventByDaysReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                        return jobDetail;
                    }
                }));

        REPORT_DEFS.add(new ReportDef(ReportOnNutritionByWeekReport.class, ReportOnNutritionByWeekReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + ReportOnNutritionByWeekReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new ReportOnNutritionByWeekReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(ReportOnNutritionByWeekReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));
                           //MenuDetailsGroupByMenuOriginReport
        REPORT_DEFS.add(new ReportDef(DailySalesByGroupsReport.class, DailySalesByGroupsReport.AutoReportBuildJob.class, new JobDetailCreator() {
                    public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                        Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                        // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                        String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + DailySalesByGroupsReport.class.getSimpleName() + ".jasper";

                        BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                                jobId,
                                jobName,
                                new DailySalesByGroupsReport(),
                                autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                                autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                                reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                                (DateFormat) autoReportGenerator.getDateFormat().clone(),
                                (DateFormat) autoReportGenerator.getTimeFormat().clone());

                        JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                        jobDetail.getJobDataMap()
                                .put(DailySalesByGroupsReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                        return jobDetail;
                    }
                }));

        REPORT_DEFS.add(new ReportDef(SubscriptionFeedingJasperReport.class, SubscriptionFeedingJasperReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + SubscriptionFeedingJasperReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new SubscriptionFeedingJasperReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(SubscriptionFeedingJasperReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(QuestionaryResultByOrgReport.class, QuestionaryResultByOrgReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + QuestionaryResultByOrgReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new QuestionaryResultByOrgReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(QuestionaryResultByOrgReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(ClientMigrationHistoryReport.class, ClientMigrationHistoryReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + ClientMigrationHistoryReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new ClientMigrationHistoryReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(ClientMigrationHistoryReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(ClientSelectedAnswerResultByOrgReport.class, ClientSelectedAnswerResultByOrgReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + ClientSelectedAnswerResultByOrgReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new ClientSelectedAnswerResultByOrgReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(ClientSelectedAnswerResultByOrgReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(MenuDetailsGroupByMenuOriginReport.class, MenuDetailsGroupByMenuOriginReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + MenuDetailsGroupByMenuOriginReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new MenuDetailsGroupByMenuOriginReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(MenuDetailsGroupByMenuOriginReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(ClientOrderDetailsByAllOrgReport.class, ClientOrderDetailsByAllOrgReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + ClientOrderDetailsByAllOrgReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new ClientOrderDetailsByAllOrgReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(ClientOrderDetailsByAllOrgReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(DashboardByAllOrgReport.class, DashboardByAllOrgReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + DashboardByAllOrgReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new DashboardByAllOrgReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(DashboardByAllOrgReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(OrderDetailsGroupByMenuOriginReport.class, OrderDetailsGroupByMenuOriginReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + OrderDetailsGroupByMenuOriginReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new OrderDetailsGroupByMenuOriginReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(OrderDetailsGroupByMenuOriginReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(ClientOrderDetailsByOneOrgReport.class, ClientOrderDetailsByOneOrgReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + ClientOrderDetailsByOneOrgReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new ClientOrderDetailsByOneOrgReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(ClientOrderDetailsByOneOrgReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(RegisterStampReport.class, RegisterStampReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + RegisterStampReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new RegisterStampReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(RegisterStampReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(DeliveredServicesReport.class, DeliveredServicesReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + DeliveredServicesReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new DeliveredServicesReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(DeliveredServicesReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(ActiveClientsReport.class, ActiveClientsReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + ActiveClientsReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new ActiveClientsReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(ActiveClientsReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(ActiveDiscountClientsReport.class, ActiveDiscountClientsReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + ActiveDiscountClientsReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new ActiveDiscountClientsReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(ActiveDiscountClientsReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(ComplaintCountByGoodReport.class, ComplaintCountByGoodReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + ComplaintCountByGoodReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new ComplaintCountByGoodReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(ComplaintCountByGoodReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(ComplaintCausesReport.class, ComplaintCausesReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + ComplaintCausesReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new ComplaintCausesReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(ComplaintCausesReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(ComplaintIterationsReport.class, ComplaintIterationsReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + ComplaintIterationsReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new ComplaintIterationsReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(ComplaintIterationsReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(ProductPopularityReport.class, ProductPopularityReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + ProductPopularityReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new ProductPopularityReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(ProductPopularityReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(ContragentPaymentReport.class, ContragentPaymentReport.AutoReportBuildJob.class, new  JobDetailCreator(){
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + ContragentPaymentReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new ContragentPaymentReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(ContragentPaymentReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        })
        );


        REPORT_DEFS.add(new ReportDef(ContragentCompletionReport.class, ContragentCompletionReport.AutoReportBuildJob.class, new  JobDetailCreator(){
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + ContragentCompletionReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new ContragentCompletionReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(ContragentCompletionReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        })
        );


        REPORT_DEFS.add(new ReportDef(HalfYearSummaryReport.class, HalfYearSummaryReport.Builder.class, new  JobDetailCreator(){
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + HalfYearSummaryReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new HalfYearSummaryReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap ()
                        .put (HalfYearSummaryReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        })
        );



        REPORT_DEFS.add(new ReportDef(BeneficiarySummaryReport.class, BeneficiarySummaryReport.Builder.class, new  JobDetailCreator(){
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + BeneficiarySummaryReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new BeneficiarySummaryReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap ()
                        .put (BeneficiarySummaryReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        })
        );

        REPORT_DEFS
                .add(new ReportDef(CWOACReport.class, CWOACReport.AutoReportBuildJob.class, new JobDetailCreator() {
                    public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId,
                            String jobName) throws Exception {
                        Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                        String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + "CWOACReport.jasper";
                        BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                                jobId,
                                jobName, new CWOACReport(), autoReportGenerator.getExecutorService(),
                                autoReportGenerator.getSessionFactory(), autoReportGenerator.getAutoReportProcessor(),
                                autoReportGenerator.getReportPath(), reportTemplate,
                                (Calendar) autoReportGenerator.getCalendar().clone(),
                                (DateFormat) autoReportGenerator.getDateFormat().clone(),
                                (DateFormat) autoReportGenerator.getTimeFormat().clone());
                        JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                        jobDetail.getJobDataMap()
                                .put(CWOACReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                        return jobDetail;
                    }
                }));

        REPORT_DEFS.add(new ReportDef(SSTSReport.class, SSTSReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName)
                    throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + "SSTSReport.jasper";
                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName, new SSTSReport(), autoReportGenerator.getExecutorService(),
                        autoReportGenerator.getSessionFactory(), autoReportGenerator.getAutoReportProcessor(),
                        autoReportGenerator.getReportPath(), reportTemplate,
                        (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());
                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap().put(SSTSReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(ClientBalanceByDayReport.class, ClientBalanceByDayReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName)
                    throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + "ClientBalanceByDayReport.jasper";
                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName, new ClientBalanceByDayReport(), autoReportGenerator.getExecutorService(),
                        autoReportGenerator.getSessionFactory(), autoReportGenerator.getAutoReportProcessor(),
                        autoReportGenerator.getReportPath(), reportTemplate,
                        (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());
                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap().put(ClientBalanceByDayReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));


        REPORT_DEFS.add(new ReportDef(BudgetMealsShippingReport.class, BudgetMealsShippingReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName)
                    throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + "BudgetMealsShippingReport.jasper";
                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName, new BudgetMealsShippingReport(), autoReportGenerator.getExecutorService(),
                        autoReportGenerator.getSessionFactory(), autoReportGenerator.getAutoReportProcessor(),
                        autoReportGenerator.getReportPath(), reportTemplate,
                        (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());
                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap().put(BudgetMealsShippingReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(StatisticsPaymentPreferentialSupplyJasperReport.class, StatisticsPaymentPreferentialSupplyJasperReport.AutoReportBuildJob.class, new  JobDetailCreator(){
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + StatisticsPaymentPreferentialSupplyJasperReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new StatisticsPaymentPreferentialSupplyJasperReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(StatisticsPaymentPreferentialSupplyJasperReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM,
                                executeEnvironment);
                return jobDetail;
            }
        })
        );

        REPORT_DEFS.add(new ReportDef(TelephoneNumberCountJasperReport.class, TelephoneNumberCountJasperReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + TelephoneNumberCountJasperReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new TelephoneNumberCountJasperReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(TelephoneNumberCountJasperReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(DiscrepanciesDataOnOrdersAndPaymentJasperReport.class, DiscrepanciesDataOnOrdersAndPaymentJasperReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName)
                    throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + "DiscrepanciesDataOnOrdersAndPaymentJasperReport.jasper";
                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName, new DiscrepanciesDataOnOrdersAndPaymentJasperReport(), autoReportGenerator.getExecutorService(),
                        autoReportGenerator.getSessionFactory(), autoReportGenerator.getAutoReportProcessor(),
                        autoReportGenerator.getReportPath(), reportTemplate,
                        (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());
                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap().put(
                        DiscrepanciesDataOnOrdersAndPaymentJasperReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM,
                        executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(TransactionsReport.class, TransactionsReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = TransactionsReport.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + TransactionsReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new TransactionsReport.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new TransactionsReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(TransactionsReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(TotalSalesReport.class, TotalSalesReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + TotalSalesReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new TotalSalesReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(TotalSalesReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(FeedingAndVisitReport.class, FeedingAndVisitReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + FeedingAndVisitReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new FeedingAndVisitReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(FeedingAndVisitReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));
        REPORT_DEFS.add(new ReportDef(FeedingAndVisitSReport.class, FeedingAndVisitSReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + FeedingAndVisitSReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new FeedingAndVisitSReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(FeedingAndVisitSReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(DetailedDeviationsWithoutCorpsJasperReport.class, DetailedDeviationsWithoutCorpsJasperReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + DetailedDeviationsWithoutCorpsJasperReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new DetailedDeviationsWithoutCorpsJasperReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(DetailedDeviationsWithoutCorpsJasperReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(DetailedDeviationsWithoutCorpsIntervalJasperReport.class, DetailedDeviationsWithoutCorpsIntervalJasperReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + DetailedDeviationsWithoutCorpsIntervalJasperReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new DetailedDeviationsWithoutCorpsIntervalJasperReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(DetailedDeviationsWithoutCorpsIntervalJasperReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(SMSDeliveryReport.class, SMSDeliveryReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + SMSDeliveryReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new SMSDeliveryReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(SMSDeliveryReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(RequestsAndOrdersReport.class, RequestsAndOrdersReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + RequestsAndOrdersReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new RequestsAndOrdersReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(RequestsAndOrdersReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(PaymentTotalsReport.class, PaymentTotalsReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + PaymentTotalsReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new PaymentTotalsReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(PaymentTotalsReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(BeneficiaryByAllOrgReport.class, BeneficiaryByAllOrgReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + BeneficiaryByAllOrgReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new BeneficiaryByAllOrgReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(BeneficiaryByAllOrgReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(BalanceLeavingReport.class, BalanceLeavingReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + BalanceLeavingReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new BalanceLeavingReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(BalanceLeavingReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(OutOfSynchronizationReport.class, OutOfSynchronizationReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + OutOfSynchronizationReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new OutOfSynchronizationReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(OutOfSynchronizationReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(TypesOfCardReport.class, TypesOfCardReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + TypesOfCardReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new TypesOfCardReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(TypesOfCardReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(LatePaymentDetailedReport.class, LatePaymentDetailedReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + LatePaymentDetailedReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new LatePaymentDetailedReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(LatePaymentDetailedReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(LatePaymentReport.class, LatePaymentReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + LatePaymentReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new LatePaymentReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(LatePaymentReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(AdjustmentPaymentReport.class, AdjustmentPaymentReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + AdjustmentPaymentReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new AdjustmentPaymentReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(AdjustmentPaymentReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(InteractiveCardDataReport.class, InteractiveCardDataReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + InteractiveCardDataReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new InteractiveCardDataReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(InteractiveCardDataReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(TotalBenefFeedReport.class, TotalBenefFeedReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + TotalBenefFeedReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new TotalBenefFeedReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(TotalBenefFeedReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(OrdersByManufacturerReport.class, OrdersByManufacturerReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + OrdersByManufacturerReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new OrdersByManufacturerReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(OrdersByManufacturerReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(MonitoringOfReport.class, MonitoringOfReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + MonitoringOfReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobId,
                        jobName,
                        new MonitoringOfReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobId, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(MonitoringOfReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

    } // static


    private static Class getJobClassForName(String canonicalJobClassName) throws Exception {
        for (ReportDef r : REPORT_DEFS) {
            if (r.buildJobClass.getCanonicalName().equals(canonicalJobClassName)) return r.buildJobClass;
        }
        return null;
    }

    public static Class getReportJobClass(String reportType) {
        for (ReportDef r : REPORT_DEFS) {
            if (r.getReportType().equals(reportType)) return r.buildJobClass;
        }
        return null;
    }

    public static String getReportType(String reportJobClass) {
        for (ReportDef r : REPORT_DEFS) {
            if (r.buildJobClass.getCanonicalName().equals(reportJobClass)) return r.reportClass.getCanonicalName();

        }
        return null;
    }

    private static final Logger logger = LoggerFactory.getLogger(AutoReportGenerator.class);
    private final String basePath;
    private final ExecutorService executorService;
    private final Scheduler scheduler;
    private final SessionFactory sessionFactory;
    private final AutoReportProcessor autoReportProcessor;
    private final Calendar calendar;
    private final String reportPath;
    private final DateFormat dateFormat;
    private final DateFormat timeFormat;
    private final Properties reportProperties;

    public AutoReportGenerator(String basePath, ExecutorService executorService, Scheduler scheduler,
            SessionFactory sessionFactory, AutoReportProcessor autoReportProcessor, Calendar calendar,
            String reportPath, DateFormat dateFormat, DateFormat timeFormat, Properties reportProperties) {
        this.basePath = basePath;
        this.executorService = executorService;
        this.scheduler = scheduler;
        this.sessionFactory = sessionFactory;
        this.autoReportProcessor = autoReportProcessor;
        this.calendar = calendar;
        this.reportPath = reportPath;
        this.dateFormat = dateFormat;
        this.timeFormat = timeFormat;
        this.reportProperties = reportProperties;
    }

    public String getBasePath() {
        return basePath;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public AutoReportProcessor getAutoReportProcessor() {
        return autoReportProcessor;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public String getReportPath() {
        return reportPath;
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    public DateFormat getTimeFormat() {
        return timeFormat;
    }

    public Properties getReportProperties() {
        return reportProperties;
    }

    public void start() throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = BasicReport.createTransaction(persistenceSession);
            persistenceTransaction.begin();
            Criteria jobsCriteria = createEnabledReportJobsCriteria(persistenceSession);
            jobsCriteria.add(Restrictions.eq("enabled", Boolean.TRUE));
            List rules = jobsCriteria.list();
            for (Object object : rules) {
                SchedulerJob schedulerJob = (SchedulerJob) object;
                try {
                    scheduleNewJob(schedulerJob);
                } catch (Exception e) {
                    logger.error("Failed to schedule job: "+schedulerJob.getJobClass()+". Removing it", e);
                    persistenceSession.delete(schedulerJob);
                }
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public void addJob(SchedulerJob schedulerJob) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = BasicReport.createTransaction(persistenceSession);
            persistenceTransaction.begin();
            persistenceSession.save(schedulerJob);
            if (schedulerJob.isEnabled()) {
                scheduleNewJob(schedulerJob);
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public void addJobRule(JobRules jobRules) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = BasicReport.createTransaction(persistenceSession);
            persistenceTransaction.begin();
            persistenceSession.persist(jobRules);
            persistenceSession.save(jobRules);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public void removeJob(Long idOfSchedulerJob) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = BasicReport.createTransaction(persistenceSession);
            persistenceTransaction.begin();
            SchedulerJob schedulerJob = (SchedulerJob) persistenceSession.load(SchedulerJob.class, idOfSchedulerJob);
            if (schedulerJob.isEnabled()) {
                cancelScheduledJob(schedulerJob);
            }
            persistenceSession.delete(schedulerJob);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public void removeJobRules(Long idOfSchedulerJob) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = BasicReport.createTransaction(persistenceSession);
            persistenceTransaction.begin();
            persistenceSession.createQuery("delete from JobRules where schedulerJob = :idOfSchedulerJob").setLong("idOfSchedulerJob", idOfSchedulerJob).executeUpdate();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

    }

    public void triggerJob(long idOfSchedulerJob, Date startDate, Date endDate) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = BasicReport.createTransaction(persistenceSession);
            persistenceTransaction.begin();
            SchedulerJob schedulerJob = (SchedulerJob) persistenceSession.load(SchedulerJob.class, idOfSchedulerJob);
            triggerJob(getJobId(schedulerJob), schedulerJob.getJobName(), startDate, endDate);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public void updateJob(Long idOfSchedulerJob, String jobName, String jobClass, String cronExpression,
            boolean enabled) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = BasicReport.createTransaction(persistenceSession);
            persistenceTransaction.begin();
            SchedulerJob schedulerJob = (SchedulerJob) persistenceSession.load(SchedulerJob.class, idOfSchedulerJob);
            if (schedulerJob.isEnabled()) {
                cancelScheduledJob(schedulerJob);
            }
            schedulerJob.setJobName(jobName);
            schedulerJob.setJobClass(jobClass);
            schedulerJob.setCronExpression(cronExpression);
            schedulerJob.setEnabled(enabled);
            if (schedulerJob.isEnabled()) {
                scheduleNewJob(schedulerJob);
            }
            persistenceSession.update(schedulerJob);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            persistenceSession.clear();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private JobDetail createJobDetail(Class jobClass, String jobId, String jobName) throws Exception {
        JobDetailCreator jobDetailCreator = getReportJobDetailCreator(jobClass);
        if (jobClass == null) {
            throw new IllegalArgumentException(String.format("Unknown report job class: %s", jobClass));
        }
        return jobDetailCreator.createJobDetail(this, jobId, jobName);
    }

    public JobDetailCreator getReportJobDetailCreator(Class jobClass) {
        String jobClassName = jobClass.getCanonicalName();
        for (ReportDef r : REPORT_DEFS) {
            if (r.buildJobClass.getCanonicalName().equals(jobClassName)) return r.jobDetailCreator;
        }
        return null;
    }

    private String getJobId(SchedulerJob schedulerJob) {
        return schedulerJob.getIdOfSchedulerJob().toString();
    }

    private void scheduleNewJob(SchedulerJob schedulerJob) throws Exception {
        scheduleNewJob(getJobClassForName(schedulerJob.getJobClass()), getJobId(schedulerJob),
                schedulerJob.getJobName(),
                schedulerJob.getCronExpression());
    }

    private void cancelScheduledJob(SchedulerJob schedulerJob) throws Exception {
        cancelScheduledJob(getJobId(schedulerJob));
    }

    private void scheduleNewJob(Class jobClass, String jobId, String jobName, String cronExpression) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Scheduling new report build job. jobClass: %s, jobName: %s, cronExpression: %s",
                    jobClass.getCanonicalName(), jobName, cronExpression));
        }
        JobDetail jobDetail = createJobDetail(jobClass, jobId, jobName);
        CronTrigger trigger = new CronTrigger(jobId, Scheduler.DEFAULT_GROUP, cronExpression);
        trigger.setTimeZone(this.calendar.getTimeZone());
        trigger.setStartTime(new Date());
        if (this.scheduler.getTrigger(jobId, Scheduler.DEFAULT_GROUP)!=null) {
            this.scheduler.deleteJob(jobId, Scheduler.DEFAULT_GROUP);
        }
        this.scheduler.scheduleJob(jobDetail, trigger);
    }

    private void cancelScheduledJob(String jobId) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Cancelling scheduled report build job. jobId: %s", jobId));
        }
        this.scheduler.deleteJob(jobId, Scheduler.DEFAULT_GROUP);
    }

    private void triggerJob(String jobId, String jobName, Date startDate, Date endDate) throws Exception {
        JobDetail jobDetail=this.scheduler.getJobDetail(jobId, Scheduler.DEFAULT_GROUP);
        if(jobDetail == null) throw new Exception(String.format("Задача с Названием задачи '%s' не включена попробуйте поставить галочку \"Включено\" и перезапустить сервер", jobName));
        if (startDate!=null) {
            Object executeEnvironmentObject = jobDetail.getJobDataMap().put(BasicReportJob.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, startDate);
            if (executeEnvironmentObject!=null && executeEnvironmentObject instanceof BasicReportJob.AutoReportBuildJob.ExecuteEnvironment) {
                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = (BasicReportJob.AutoReportBuildJob.ExecuteEnvironment)executeEnvironmentObject;
                executeEnvironment.setStartDate(startDate);
                executeEnvironment.setEndDate(endDate);
            }
        }
        this.scheduler.triggerJob(jobId, Scheduler.DEFAULT_GROUP);
    }

    public static String restoreFilename(String defaultPath, String filename) {
        File file = new File(filename);
        if (!file.isAbsolute()) {
            return FilenameUtils.concat(defaultPath, filename);
        }
        return filename;
    }

    public String getReportsTemplateFilePath() {
        String path = this.getReportProperties().getProperty("path");
        if (path==null) return null;
        if (!path.endsWith("/") && !path.endsWith("\\")) path+='/';
        return path;
    }

}
