/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.job;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.SchedulerJob;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Transaction;
import org.hibernate.Session;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.print.attribute.standard.Severity;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ReportJobViewPage extends BasicWorkspacePage {

    private Long idOfSchedulerJob;
    private String jobName;
    private String reportType;
    private String cronExpression;
    private boolean enabled;
    private Date generateStartDate;

    public String getPageFilename() {
        return "report/job/view";
    }

    public Long getIdOfSchedulerJob() {
        return idOfSchedulerJob;
    }

    public String getJobName() {
        return jobName;
    }

    public String getReportType() {
        return reportType;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Date getGenerateStartDate() {
        return generateStartDate;
    }

    public void setGenerateStartDate(Date generateStartDate) {
        this.generateStartDate = generateStartDate;
    }

    public Object triggerJob() throws Exception {
        FacesContext fc = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            runtimeContext.getAutoReportGenerator().triggerJob(idOfSchedulerJob, generateStartDate);
        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка: "+e.toString(), ""));
        }
        finally {
            runtimeContext.release();
        }
        return null;
    }

    public void fill(Session session, Long idOfReportJob) throws Exception {
        SchedulerJob schedulerJob = (SchedulerJob) session.load(SchedulerJob.class, idOfReportJob);
        this.idOfSchedulerJob = schedulerJob.getIdOfSchedulerJob();
        this.jobName = schedulerJob.getJobName();
        this.reportType = AutoReportGenerator.getReportType(schedulerJob.getJobClass());
        this.cronExpression = schedulerJob.getCronExpression();
        this.enabled = schedulerJob.isEnabled();
    }
}