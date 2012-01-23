/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 27.06.2009
 * Time: 14:02:41
 * To change this template use File | Settings | File Templates.
 */
public class SchedulerJob {

    private Long idOfSchedulerJob;
    private String jobName;
    private String jobClass;
    private String cronExpression;
    private boolean enabled;

    SchedulerJob() {
        // For Hibernate only
    }

    public SchedulerJob(String jobName, String jobClass, String cronExpression, boolean enabled) {
        this.jobName = jobName;
        this.jobClass = jobClass;
        this.cronExpression = cronExpression;
        this.enabled = enabled;
    }

    public Long getIdOfSchedulerJob() {
        return idOfSchedulerJob;
    }

    private void setIdOfSchedulerJob(Long idOfSchedulerJob) {
        // For Hibernate only
        this.idOfSchedulerJob = idOfSchedulerJob;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobClass() {
        return jobClass;
    }

    public void setJobClass(String jobClass) {
        this.jobClass = jobClass;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SchedulerJob)) {
            return false;
        }
        final SchedulerJob that = (SchedulerJob) o;
        return idOfSchedulerJob.equals(that.getIdOfSchedulerJob());
    }

    @Override
    public int hashCode() {
        return idOfSchedulerJob.hashCode();
    }

    @Override
    public String toString() {
        return "SchedulerJob{" + "IdOfSchedulerJob=" + idOfSchedulerJob + ", jobName='" + jobName + '\''
                + ", jobClass='" + jobClass + '\'' + ", cronExpression='" + cronExpression + '\'' + ", enabled="
                + enabled + '}';
    }
}