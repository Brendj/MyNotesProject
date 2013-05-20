/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.SchedulerJob;
import ru.axetta.ecafe.processor.core.report.kzn.SalesReport;
import ru.axetta.ecafe.processor.core.report.maussp.ContragentOrderCategoryReport;
import ru.axetta.ecafe.processor.core.report.maussp.ContragentOrderReport;
import ru.axetta.ecafe.processor.core.report.msc.BeneficiarySummaryReport;
import ru.axetta.ecafe.processor.core.report.msc.HalfYearSummaryReport;
import ru.axetta.ecafe.processor.core.report.msc.MscSalesReport;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.io.FilenameUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.Session;
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

    static class ReportDef {
        Class buildJobClass;
        Class reportClass;
        JobDetailCreator jobDetailCreator;

        ReportDef(Class reportClass, Class buildJobClass, JobDetailCreator jobDetailCreator) {
            this.buildJobClass = buildJobClass;
            this.jobDetailCreator = jobDetailCreator;
            this.reportClass = reportClass;
        }

        String getReportType() {
            return reportClass.getCanonicalName();
        }
    }
    static LinkedList<ReportDef> REPORT_DEFS=new LinkedList<ReportDef>();

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

    private static interface JobDetailCreator {

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

        REPORT_DEFS.add(new ReportDef(QuestionaryResultByOrgReport.class, QuestionaryResultByOrgReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + QuestionaryResultByOrgReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
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

        REPORT_DEFS.add(new ReportDef(OrderDetailsGroupByMenuOriginReport.class, OrderDetailsGroupByMenuOriginReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + OrderDetailsGroupByMenuOriginReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobName,
                        new OrderDetailsGroupByMenuOriginReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, jobClass);
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
                        jobName,
                        new ClientOrderDetailsByOneOrgReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, jobClass);
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
                        jobName,
                        new RegisterStampReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, jobClass);
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
                        jobName,
                        new DeliveredServicesReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(DeliveredServicesReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(ComplaintCountByGoodReport.class, ComplaintCountByGoodReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobId, String jobName) throws Exception {
                Class jobClass = BasicReportJob.AutoReportBuildJob.class;
                // файл шаблона отчета по умолчанию: путь к шаблонам + имя класса + ".jasper"
                String reportTemplate = autoReportGenerator.getReportsTemplateFilePath() + ComplaintCountByGoodReport.class.getSimpleName() + ".jasper";

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        jobName,
                        new ComplaintCountByGoodReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, jobClass);
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
                        jobName,
                        new ComplaintCausesReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, jobClass);
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
                        jobName,
                        new ComplaintIterationsReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, jobClass);
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
                        jobName,
                        new ProductPopularityReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, jobClass);
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
                        jobName,
                        new HalfYearSummaryReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, jobClass);
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
                        jobName,
                        new BeneficiarySummaryReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap ()
                        .put (BeneficiarySummaryReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        })
        );
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
            persistenceSession = sessionFactory.openSession();
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

    public void removeJob(Long idOfSchedulerJob) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = sessionFactory.openSession();
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

    public void triggerJob(long idOfSchedulerJob, Date startDate, Date endDate) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = BasicReport.createTransaction(persistenceSession);
            persistenceTransaction.begin();
            SchedulerJob schedulerJob = (SchedulerJob) persistenceSession.load(SchedulerJob.class, idOfSchedulerJob);
            triggerJob(getJobId(schedulerJob), startDate, endDate);
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
            persistenceSession = sessionFactory.openSession();
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

    private JobDetailCreator getReportJobDetailCreator(Class jobClass) {
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

    private void triggerJob(String jobName, Date startDate, Date endDate) throws Exception {
        JobDetail jobDetail=this.scheduler.getJobDetail(jobName, Scheduler.DEFAULT_GROUP);
        if (startDate!=null) {
            if (jobDetail!=null) {
                Object executeEnvironmentObject = jobDetail.getJobDataMap().put(BasicReportJob.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, startDate);
                if (executeEnvironmentObject!=null && executeEnvironmentObject instanceof BasicReportJob.AutoReportBuildJob.ExecuteEnvironment) {
                    BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = (BasicReportJob.AutoReportBuildJob.ExecuteEnvironment)executeEnvironmentObject;
                    executeEnvironment.setStartDate(startDate);
                    executeEnvironment.setEndDate(endDate);
                }
            }
        }
        this.scheduler.triggerJob(jobName, Scheduler.DEFAULT_GROUP);
    }

    public static String restoreFilename(String defaultPath, String filename) {
        File file = new File(filename);
        if (!file.isAbsolute()) {
            return FilenameUtils.concat(defaultPath, filename);
        }
        return filename;
    }

    public String getReportsTemplateFilePath() {
        //TODO заменить "path" на константу
        String path = this.getReportProperties().getProperty("path");
        //String path = "D:\\home\\jbosser\\processor\\templates";
        if (path==null) return null;
        if (!path.endsWith("/") && !path.endsWith("\\")) path+='/';
        return path;
    }

    public static String getReportsTemplateFilePathWithDb (/*Session session*/) {
        /*RuntimeContext.getInstance().getPropertiesValue(AUTO_REPORT_PARAM_BASE + ".path");

        Criteria criteria = session.createCriteria(Option.class);
        criteria.add(Restrictions.eq("id", 1L));
        Option op = (Option) criteria.uniqueResult();
        String path = op.getOptionText();
        if (path==null) return null;
        if (!path.endsWith("/") && !path.endsWith("\\")) path+='/';
        return path;*/
        return "\\processor\\templates\\";
    }

}
