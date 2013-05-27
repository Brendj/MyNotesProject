/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.job;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.SchedulerJob;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Transaction;
import org.hibernate.Session;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.print.attribute.standard.Severity;
import javax.servlet.http.HttpSession;
import java.util.Calendar;
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
    private Date generateEndDate;
    private Calendar localCalendar;
    private RuntimeContext runtimeContext;

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

    public Date getGenerateEndDate() {
        return generateEndDate;
    }

    public void setGenerateEndDate(Date generateEndDate) {
        if(generateEndDate!=null){
            localCalendar.setTime(generateEndDate);
            localCalendar.add(Calendar.DAY_OF_MONTH,1);
            localCalendar.add(Calendar.SECOND, -1);
            this.generateEndDate = localCalendar.getTime();
        } else {
            this.generateEndDate = generateEndDate;
        }
    }

    public Object triggerJob() throws Exception {
        try {
            if(generateStartDate==null) throw new Exception("Не задан период выборки");
            if (generateEndDate==null) {
                generateEndDate = CalendarUtils.addDays(generateStartDate, 1);
            }
            runtimeContext.getAutoReportGenerator().triggerJob(idOfSchedulerJob, generateStartDate, generateEndDate);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Задача запущена успешно перейдите во вкладку Репозиторий отчетов/Просмотр и дождитесь результата работы", ""));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка: "+(e.getMessage()==null?e.toString():e.getMessage()), ""));
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
        runtimeContext = RuntimeContext.getInstance();
        localCalendar = runtimeContext
                .getDefaultLocalCalendar((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false));

    }
}