/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.job;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.JobRules;
import ru.axetta.ecafe.processor.core.persistence.RuleCondition;
import ru.axetta.ecafe.processor.core.persistence.SchedulerJob;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 11.02.16
 * Time: 14:08
 * To change this template use File | Settings | File Templates.
 */
public class QuartzJobsListPage extends BasicWorkspacePage {
    private static final Logger logger = LoggerFactory.getLogger(QuartzJobsListPage.class);
    private List<QuartzItem> items = new ArrayList<QuartzItem>();

    public List<QuartzItem> getItems() {
        return items;
    }

    public void fill(Session session) throws Exception {
        items.clear();

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Scheduler scheduler = runtimeContext.getAutoReportGenerator().getScheduler();
        for (String groupName : scheduler.getJobGroupNames()) {

            //loop all jobs by groupname
            for (String jobName : scheduler.getJobNames(groupName)) {
                Trigger[] triggers = scheduler.getTriggersOfJob(jobName,groupName);
                Date nextFireTime = triggers[0].getNextFireTime();

                Long jobId = Long.parseLong(jobName);
                SchedulerJob job = (SchedulerJob)session.load(SchedulerJob.class, jobId);
                Criteria reportJobRules = JobRules.createReportJobRulesCriteria(session, job);
                List<JobRules> loadJobRules = reportJobRules.list();
                String jobs = "";
                for (JobRules rule : loadJobRules) {
                    jobs += rule.getReportHandleRule().getIdOfReportHandleRule() + ", ";
                }
                if (!jobs.equals("")) {
                    jobs = jobs.substring(0, jobs.length()-2);
                } else {
                    jobs = "Все";
                }
                QuartzItem item = new QuartzItem();
                item.setScheduleId(jobId);
                item.setScheduleName(job.getJobName());
                item.setCronExpression(job.getCronExpression());
                item.setJobClass(cutReportType(job.getJobClass()));
                item.setNextRun(CalendarUtils.dateTimeToString(nextFireTime));
                item.setRuleIds(jobs);
                items.add(item);
            }
        }
    }

    public void reload() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обновлении данных: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return;
    }

    private static String cutReportType(String reportType) {
        if (reportType.startsWith(RuleCondition.REPORT_TYPE_BASE_PART)) {
            return ReportJobConstants.ELIDE_FILL + reportType
                    .substring(RuleCondition.REPORT_TYPE_BASE_PART.length());
        }
        return reportType;
    }

    public String getPageFilename() {
        return "report/job/quartz_list";
    }

    public class QuartzItem {
        private Long scheduleId;
        private String scheduleName;
        private String nextRun;
        private String cronExpression;
        private String jobClass;
        private String ruleIds;

        public Long getScheduleId() {
            return scheduleId;
        }

        public void setScheduleId(Long scheduleId) {
            this.scheduleId = scheduleId;
        }

        public String getScheduleName() {
            return scheduleName;
        }

        public void setScheduleName(String scheduleName) {
            this.scheduleName = scheduleName;
        }

        public String getNextRun() {
            return nextRun;
        }

        public void setNextRun(String nextRun) {
            this.nextRun = nextRun;
        }

        public String getCronExpression() {
            return cronExpression;
        }

        public void setCronExpression(String cronExpression) {
            this.cronExpression = cronExpression;
        }

        public String getJobClass() {
            return jobClass;
        }

        public void setJobClass(String jobClass) {
            this.jobClass = jobClass;
        }

        public String getRuleIds() {
            return ruleIds;
        }

        public void setRuleIds(String ruleIds) {
            this.ruleIds = ruleIds;
        }
    }
}
