/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.job;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.SchedulerJob;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.report.rule.ReportTypeMenu;

import org.hibernate.Session;

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
public class ReportJobCreatePage extends BasicWorkspacePage {

    private String jobName;
    private boolean enabled;
    private boolean showRules;
    private Integer[] preferentialComplexes;
    private String reportType;
    private String cronExpression;
    private final ReportTypeMenu reportTypeMenu = new ReportTypeMenu();

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

    public Integer[] getPreferentialComplexes() {
        return preferentialComplexes;
    }

    public void setPreferentialComplexes(Integer[] preferentialComplexes) {
        this.preferentialComplexes = preferentialComplexes;
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

    public void fill(Session session) throws Exception {

    }

    public void createReportJob() throws Exception {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        SchedulerJob schedulerJob = new SchedulerJob(this.jobName,
                AutoReportGenerator.getReportJobClass(this.reportType).getCanonicalName(), this.cronExpression,
                this.enabled);
        runtimeContext.getAutoReportGenerator().addJob(schedulerJob);
    }

    public List<SelectItem> getAvailableRules() {
        String reportType = getReportType();

        List<SelectItem> list = new ArrayList<SelectItem>();

        SelectItem selectItem = new SelectItem(0L, "Правило");
        list.add(selectItem);

        return list;
    }
}