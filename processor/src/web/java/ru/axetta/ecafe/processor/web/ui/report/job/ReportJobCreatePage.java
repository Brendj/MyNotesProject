/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.job;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ReportHandleRule;
import ru.axetta.ecafe.processor.core.persistence.SchedulerJob;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.report.rule.ReportTypeMenu;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ReportJobCreatePage extends BasicWorkspacePage {

    private String jobName;
    private boolean enabled;
    private boolean showRules;
    private Integer[] preferentialRules;
    private String reportType;
    private String cronExpression;
    private final ReportTypeMenu reportTypeMenu = new ReportTypeMenu();
    private List<ReportHandleRule> reportHandleRuleList;
    List<SelectItem> list;

    public String getPageFilename() {
        return "report/job/create";
    }

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

    public ReportTypeMenu getReportTypeMenu() {
        return reportTypeMenu;
    }

    public List<ReportHandleRule> getReportHandleRuleList() {
        return reportHandleRuleList;
    }

    public void setReportHandleRuleList(List<ReportHandleRule> reportHandleRuleList) {
        this.reportHandleRuleList = reportHandleRuleList;
    }

    public List<SelectItem> getList() {
        return list;
    }

    public void setList(List<SelectItem> list) {
        this.list = list;
    }

    public void fill(Session session) throws Exception {
        this.jobName = null;
        this.cronExpression = null;
        this.enabled = false;
        this.showRules = false;
    }

    public void createReportJob() throws Exception {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        SchedulerJob schedulerJob = new SchedulerJob(this.jobName,
                AutoReportGenerator.getReportJobClass(this.reportType).getCanonicalName(), this.cronExpression,
                this.enabled);
        runtimeContext.getAutoReportGenerator().addJob(schedulerJob);
    }

    public List<SelectItem> getAvailableCreateRules(Session session) {
        list = new ArrayList<SelectItem>();
        reportHandleRuleList = new ArrayList<ReportHandleRule>();

        String reportTypeStr = this.reportType;

        if (reportTypeStr != null) {
            String[] strings = StringUtils.split(reportTypeStr, '.');
            String reportTypeString = strings[strings.length - 1];

            Criteria criteria = session.createCriteria(ReportHandleRule.class);
            criteria.add(Restrictions.ilike("templateFileName", "%" + reportTypeString + "%"));
            List<ReportHandleRule> result = criteria.list();

            reportHandleRuleList = result;

            Long counter = 0L;

            for (ReportHandleRule reportHandleRule : result) {
                String str = reportHandleRule.getIdOfReportHandleRule() + ") " + reportHandleRule.getRuleName();
                list.add(new SelectItem(counter, str));
                counter++;
            }
        }
        return list;
    }
}