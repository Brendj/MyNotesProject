/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.job;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ReportHandleRule;
import ru.axetta.ecafe.processor.core.persistence.SchedulerJob;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.report.rule.ReportTypeMenu;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ReportJobEditPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(ReportJobEditPage.class);

    private Long idOfSchedulerJob;
    private String jobName;
    private boolean enabled;
    private String reportType;
    private String cronExpression;
    private final ReportTypeMenu reportTypeMenu = new ReportTypeMenu();
    private boolean showRules;
    private Integer[] preferentialRules;
    //private String reportTemplate;
    //
    //
    //public String getReportTemplate() {
    //    return reportTemplate;
    //}
    //
    //public void setReportTemplate(String reportTemplate) {
    //    this.reportTemplate = reportTemplate;
    //}

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Long getIdOfSchedulerJob() {
        return idOfSchedulerJob;
    }

    public ReportTypeMenu getReportTypeMenu() {
        return reportTypeMenu;
    }

    public boolean isShowRules() {
        return showRules;
    }

    public void setShowRules(boolean showRules) {
        this.showRules = showRules;
    }

    public Integer[] getPreferentialRules() {
        return preferentialRules;
    }

    public void setPreferentialRules(Integer[] preferentialRules) {
        this.preferentialRules = preferentialRules;
    }

    public String getPageFilename() {
        return "report/job/edit";
    }

    public void fill(Session session, Long idOfReportJob) throws Exception {
        SchedulerJob schedulerJob = (SchedulerJob) session.load(SchedulerJob.class, idOfReportJob);
        fill(session, schedulerJob);
    }

    public void updateReportJob(Long idOfReportJob) throws Exception {
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();

            runtimeContext.getAutoReportGenerator().updateJob(idOfReportJob, this.jobName,
                    AutoReportGenerator.getReportJobClass(this.reportType).getCanonicalName(), this.cronExpression,
                    this.enabled);

            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            SchedulerJob schedulerJob = (SchedulerJob) persistenceSession.load(SchedulerJob.class, idOfReportJob);
            fill(persistenceSession, schedulerJob);

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private void fill(Session session, SchedulerJob schedulerJob) throws Exception {
        this.idOfSchedulerJob = schedulerJob.getIdOfSchedulerJob();
        this.jobName = schedulerJob.getJobName();
        this.reportType = AutoReportGenerator.getReportType(schedulerJob.getJobClass());
        this.cronExpression = schedulerJob.getCronExpression();
        this.enabled = schedulerJob.isEnabled();
    }

    public List<SelectItem> getAvailableEditRules(Session session) {
        List<SelectItem> list = new ArrayList<SelectItem>();

        String [] strings = StringUtils.split(this.reportType, '.');
        String reportTypeString = strings[strings.length - 1];

        Criteria criteria = session.createCriteria(ReportHandleRule.class);
        criteria.add(Restrictions.ilike("templateFileName", "%" + reportTypeString + "%"));
        List<ReportHandleRule> result = criteria.list();

        Long counter = 0L;

        for (ReportHandleRule reportHandleRule: result) {
            String str = reportHandleRule.getIdOfReportHandleRule() + ") " + reportHandleRule.getRuleName();
            list.add(new SelectItem(counter, str));
            counter++;
        }

        return list;
    }
}