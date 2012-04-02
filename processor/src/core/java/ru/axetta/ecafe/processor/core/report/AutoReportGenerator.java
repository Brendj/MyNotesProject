/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.SchedulerJob;
import ru.axetta.ecafe.processor.core.report.kzn.SalesReport;
import ru.axetta.ecafe.processor.core.report.maussp.ContragentOrderCategoryReport;
import ru.axetta.ecafe.processor.core.report.maussp.ContragentOrderReport;
import ru.axetta.ecafe.processor.core.report.msc.MscSalesReport;
import ru.axetta.ecafe.processor.core.utils.ExecutorServiceWrappedJob;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
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
                .add(Restrictions.like("jobClass", REPORT_JOBS_BASE_CLASS_NAME + ".%", MatchMode.START));
    }

    private static interface JobDetailCreator {

        public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobName) throws Exception;
    }

    static {
        REPORT_DEFS.add(new ReportDef(OrgBalanceReport.class, OrgBalanceReport.BuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobName) throws Exception {
                Class jobClass = OrgBalanceReport.BuildJob.class;
                OrgBalanceReport.BuildJob.ExecuteEnvironment executeEnvironment = new OrgBalanceReport.BuildJob.ExecuteEnvironment(
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());
                JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap().put(OrgBalanceReport.BuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(ClientGroupBalanceReport.class, ClientGroupBalanceReport.BuildJob.class,
                new JobDetailCreator() {
                    public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobName)
                            throws Exception {
                        Class jobClass = ClientGroupBalanceReport.BuildJob.class;
                        ClientGroupBalanceReport.BuildJob.ExecuteEnvironment executeEnvironment = new ClientGroupBalanceReport.BuildJob.ExecuteEnvironment(
                                autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                                autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                                (DateFormat) autoReportGenerator.getDateFormat().clone(),
                                (DateFormat) autoReportGenerator.getTimeFormat().clone());
                        JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, jobClass);
                        jobDetail.getJobDataMap()
                                .put(ClientGroupBalanceReport.BuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                        return jobDetail;
                    }
                }));

        REPORT_DEFS.add(new ReportDef(ContragentOrderReport.class, ContragentOrderReport.BuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobName) throws Exception {
                final String CONTRAGENT_ORDER_REPORT_TEMPLATE_KEY = "ContragentOrderReport.template";

                Class jobClass = ContragentOrderReport.BuildJob.class;

                String reportTemplate = autoReportGenerator.getReportProperties()
                        .getProperty(CONTRAGENT_ORDER_REPORT_TEMPLATE_KEY);
                if (StringUtils.isEmpty(reportTemplate)) {
                    throw new IllegalArgumentException(
                            String.format("Report property \"%s\" not found. Can\'t schedule ContragentOrderReport",
                                    CONTRAGENT_ORDER_REPORT_TEMPLATE_KEY));
                }
                reportTemplate = restoreFilename(autoReportGenerator.getBasePath(), reportTemplate);

                ContragentOrderReport.BuildJob.ExecuteEnvironment executeEnvironment = new ContragentOrderReport.BuildJob.ExecuteEnvironment(
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap().put(ContragentOrderReport.BuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(ContragentOrderCategoryReport.class, ContragentOrderCategoryReport.BuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobName) throws Exception {
                final String CONTRAGENT_ORDER_CATEGORRY_REPORT_TEMPLATE_KEY = "ContragentOrderCategoryReport.template";

                Class jobClass = ContragentOrderCategoryReport.BuildJob.class;

                String reportTemplate = autoReportGenerator.getReportProperties()
                        .getProperty(CONTRAGENT_ORDER_CATEGORRY_REPORT_TEMPLATE_KEY);
                if (StringUtils.isEmpty(reportTemplate)) {
                    throw new IllegalArgumentException(String.format(
                            "Report property \"%s\" not found. Can\'t schedule ContragentOrderCategoryReport",
                            CONTRAGENT_ORDER_CATEGORRY_REPORT_TEMPLATE_KEY));
                }
                reportTemplate = restoreFilename(autoReportGenerator.getBasePath(), reportTemplate);

                ContragentOrderCategoryReport.BuildJob.ExecuteEnvironment executeEnvironment = new ContragentOrderCategoryReport.BuildJob.ExecuteEnvironment(
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(ContragentOrderCategoryReport.BuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(OrgOrderCategoryReport.class, OrgOrderCategoryReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobName) throws Exception {
                final String REPORT_TEMPLATE_KEY = "OrgOrderCategoryReport.template";

                Class jobClass = BasicReportJob.AutoReportBuildJob.class;

                String reportTemplate = autoReportGenerator.getReportProperties()
                        .getProperty(REPORT_TEMPLATE_KEY);
                if (StringUtils.isEmpty(reportTemplate)) {
                    throw new IllegalArgumentException(String.format(
                            "Report property \"%s\" not found. Can\'t schedule OrgOrderCategoryReport",
                            REPORT_TEMPLATE_KEY));
                }
                reportTemplate = restoreFilename(autoReportGenerator.getBasePath(), reportTemplate);

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        new OrgOrderCategoryReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(OrgOrderCategoryReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(SalesReport.class, SalesReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobName) throws Exception {
                final String REPORT_TEMPLATE_KEY = "SalesReport.template";

                Class jobClass = BasicReportJob.AutoReportBuildJob.class;

                String reportTemplate = autoReportGenerator.getReportProperties()
                        .getProperty(REPORT_TEMPLATE_KEY);
                if (StringUtils.isEmpty(reportTemplate)) {
                    throw new IllegalArgumentException(String.format(
                            "Report property \"%s\" not found. Can\'t schedule SalesReport",
                            REPORT_TEMPLATE_KEY));
                }
                reportTemplate = restoreFilename(autoReportGenerator.getBasePath(), reportTemplate);

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        new SalesReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(SalesReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(MscSalesReport.class, MscSalesReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobName) throws Exception {
                final String REPORT_TEMPLATE_KEY = "MscSalesReport.template";

                Class jobClass = BasicReportJob.AutoReportBuildJob.class;

                String reportTemplate = autoReportGenerator.getReportProperties()
                        .getProperty(REPORT_TEMPLATE_KEY);
                if (StringUtils.isEmpty(reportTemplate)) {
                    throw new IllegalArgumentException(String.format(
                            "Report property \"%s\" not found. Can\'t schedule MscSalesReport",
                            REPORT_TEMPLATE_KEY));
                }
                reportTemplate = restoreFilename(autoReportGenerator.getBasePath(), reportTemplate);

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        new MscSalesReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(MscSalesReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(RegisterReport.class, RegisterReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobName) throws Exception {
                final String REPORT_TEMPLATE_KEY = "RegisterReport.template";

                Class jobClass = BasicReportJob.AutoReportBuildJob.class;

                String reportTemplate = autoReportGenerator.getReportProperties()
                        .getProperty(REPORT_TEMPLATE_KEY);
                if (StringUtils.isEmpty(reportTemplate)) {
                    throw new IllegalArgumentException(String.format(
                            "Report property \"%s\" not found. Can\'t schedule RegisterReport",
                            REPORT_TEMPLATE_KEY));
                }
                reportTemplate = restoreFilename(autoReportGenerator.getBasePath(), reportTemplate);

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        new RegisterReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(RegisterReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(ClientsReport.class, ClientsReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobName) throws Exception {
                final String REPORT_TEMPLATE_KEY = "ClientsReport.template";

                Class jobClass = BasicReportJob.AutoReportBuildJob.class;

                String reportTemplate = autoReportGenerator.getReportProperties()
                        .getProperty(REPORT_TEMPLATE_KEY);
                if (StringUtils.isEmpty(reportTemplate)) {
                    throw new IllegalArgumentException(String.format(
                            "Report property \"%s\" not found. Can\'t schedule ClientsReport",
                            REPORT_TEMPLATE_KEY));
                }
                reportTemplate = restoreFilename(autoReportGenerator.getBasePath(), reportTemplate);

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        new ClientsReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(ClientsReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
                return jobDetail;
            }
        }));

        REPORT_DEFS.add(new ReportDef(OrgOrderByDaysReport.class, OrgOrderByDaysReport.AutoReportBuildJob.class, new JobDetailCreator() {
            public JobDetail createJobDetail(AutoReportGenerator autoReportGenerator, String jobName) throws Exception {
                final String REPORT_TEMPLATE_KEY = "OrgOrderByDaysReport.template";

                Class jobClass = BasicReportJob.AutoReportBuildJob.class;

                String reportTemplate = autoReportGenerator.getReportProperties()
                        .getProperty(REPORT_TEMPLATE_KEY);
                if (StringUtils.isEmpty(reportTemplate)) {
                    throw new IllegalArgumentException(String.format(
                            "Report property \"%s\" not found. Can\'t schedule OrgOrderByDaysReport",
                            REPORT_TEMPLATE_KEY));
                }
                reportTemplate = restoreFilename(autoReportGenerator.getBasePath(), reportTemplate);

                BasicReportJob.AutoReportBuildJob.ExecuteEnvironment executeEnvironment = new BasicReportJob.AutoReportBuildJob.ExecuteEnvironment(
                        new OrgOrderByDaysReport(),
                        autoReportGenerator.getExecutorService(), autoReportGenerator.getSessionFactory(),
                        autoReportGenerator.getAutoReportProcessor(), autoReportGenerator.getReportPath(),
                        reportTemplate, (Calendar) autoReportGenerator.getCalendar().clone(),
                        (DateFormat) autoReportGenerator.getDateFormat().clone(),
                        (DateFormat) autoReportGenerator.getTimeFormat().clone());

                JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, jobClass);
                jobDetail.getJobDataMap()
                        .put(OrgOrderByDaysReport.AutoReportBuildJob.ENVIRONMENT_JOB_PARAM, executeEnvironment);
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
                    logger.error("Failed to schedule job: "+schedulerJob.getJobClass()+". Removing it");
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

    public void triggerJob(long idOfSchedulerJob, Date startDate) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = BasicReport.createTransaction(persistenceSession);
            persistenceTransaction.begin();
            SchedulerJob schedulerJob = (SchedulerJob) persistenceSession.load(SchedulerJob.class, idOfSchedulerJob);
            triggerJob(getJobName(schedulerJob), startDate);
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

    private JobDetail createJobDetail(Class jobClass, String jobName) throws Exception {
        JobDetailCreator jobDetailCreator = getReportJobDetailCreator(jobClass);
        if (jobClass == null) {
            throw new IllegalArgumentException(String.format("Unknown report job class: %s", jobClass));
        }
        return jobDetailCreator.createJobDetail(this, jobName);
    }

    private JobDetailCreator getReportJobDetailCreator(Class jobClass) {
        String jobClassName = jobClass.getCanonicalName();
        for (ReportDef r : REPORT_DEFS) {
            if (r.buildJobClass.getCanonicalName().equals(jobClassName)) return r.jobDetailCreator;
        }
        return null;
    }

    private String getJobName(SchedulerJob schedulerJob) {
        return schedulerJob.getIdOfSchedulerJob().toString();
    }

    private void scheduleNewJob(SchedulerJob schedulerJob) throws Exception {
        scheduleNewJob(getJobClassForName(schedulerJob.getJobClass()), getJobName(schedulerJob),
                schedulerJob.getCronExpression());
    }

    private void cancelScheduledJob(SchedulerJob schedulerJob) throws Exception {
        cancelScheduledJob(getJobName(schedulerJob));
    }

    private void scheduleNewJob(Class jobClass, String jobName, String cronExpression) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Scheduling new report build job. jobClass: %s, jobName: %s, cronExpression: %s",
                    jobClass.getCanonicalName(), jobName, cronExpression));
        }
        JobDetail jobDetail = createJobDetail(jobClass, jobName);
        CronTrigger trigger = new CronTrigger(jobName, Scheduler.DEFAULT_GROUP, cronExpression);
        trigger.setTimeZone(this.calendar.getTimeZone());
        trigger.setStartTime(new Date());
        if (this.scheduler.getTrigger(jobName, Scheduler.DEFAULT_GROUP)!=null) {
            this.scheduler.deleteJob(jobName, Scheduler.DEFAULT_GROUP);
        }
        this.scheduler.scheduleJob(jobDetail, trigger);
    }

    private void cancelScheduledJob(String jobName) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Cancelling scheduled report build job. jobName: %s", jobName));
        }
        this.scheduler.deleteJob(jobName, Scheduler.DEFAULT_GROUP);
    }

    private void triggerJob(String jobName, Date startDate) throws Exception {
        if (startDate!=null) {
            JobDetail jobDetail=this.scheduler.getJobDetail(jobName, Scheduler.DEFAULT_GROUP);
            if (jobDetail!=null) jobDetail.getJobDataMap().put(BasicReport.JOB_PARAM_START_DATE, startDate);
        }
        this.scheduler.triggerJob(jobName, Scheduler.DEFAULT_GROUP);
    }

    private static String restoreFilename(String defaultPath, String filename) {
        File file = new File(filename);
        if (!file.isAbsolute()) {
            return FilenameUtils.concat(defaultPath, filename);
        }
        return filename;
    }

}
