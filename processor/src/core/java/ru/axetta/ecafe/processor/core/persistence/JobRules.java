/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: T800
 * Date: 08.12.15
 * Time: 16:34
 * To change this template use File | Settings | File Templates.
 */
public class JobRules {

    private Long idOfJobRule;
    private ReportHandleRule reportHandleRule;
    private SchedulerJob schedulerJob;

    public JobRules() {
    }

    public JobRules(ReportHandleRule reportHandleRule, SchedulerJob schedulerJob) {
        this.reportHandleRule = reportHandleRule;
        this.schedulerJob = schedulerJob;
    }

    public Long getIdOfJobRule() {
        return idOfJobRule;
    }

    public void setIdOfJobRule(Long idOfJobRule) {
        this.idOfJobRule = idOfJobRule;
    }

    public ReportHandleRule getReportHandleRule() {
        return reportHandleRule;
    }

    public void setReportHandleRule(ReportHandleRule reportHandleRule) {
        this.reportHandleRule = reportHandleRule;
    }

    public SchedulerJob getSchedulerJob() {
        return schedulerJob;
    }

    public void setSchedulerJob(SchedulerJob schedulerJob) {
        this.schedulerJob = schedulerJob;
    }
}
