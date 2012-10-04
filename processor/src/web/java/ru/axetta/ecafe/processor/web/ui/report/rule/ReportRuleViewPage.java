/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.rule;

import ru.axetta.ecafe.processor.core.persistence.ReportHandleRule;
import ru.axetta.ecafe.processor.core.persistence.RuleCondition;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.RuleConditionItem;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ReportRuleViewPage extends BasicWorkspacePage {

    private long idOfReportHandleRule;
    private String ruleName;
    private String tag;
    private boolean enabled;
    private String reportType;
    private int documentFormat;
    private String subject;
    private List<String> routeAddresses = Collections.emptyList();
    private List<RuleConditionItem> ruleConditionItems = Collections.emptyList();
    private String shortName;
    private List<ReportRuleConstants.ParamHint> paramHints = Collections.emptyList();
    private String reportTemplateFileName;

    public String getPageFilename() {
        return "report/rule/view";
    }

    public long getIdOfReportHandleRule() {
        return idOfReportHandleRule;
    }

    public String getRuleName() {
        return ruleName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getReportType() {
        return reportType;
    }

    public int getDocumentFormat() {
        return documentFormat;
    }

    public String getSubject() {
        return subject;
    }

    public List<String> getRouteAddresses() {
        return routeAddresses;
    }

    public List<RuleConditionItem> getRuleConditionItems() {
        return ruleConditionItems;
    }

    public String getShortName() {
        return shortName;
    }

    public List<ReportRuleConstants.ParamHint> getParamHints() {
        return paramHints;
    }

    public void fill(Session session, Long idOfReportHandleRule) throws Exception {
        ReportHandleRule reportHandleRule = (ReportHandleRule) session
                .load(ReportHandleRule.class, idOfReportHandleRule);
        this.idOfReportHandleRule = reportHandleRule.getIdOfReportHandleRule();
        this.ruleName = reportHandleRule.getRuleName();
        this.tag = reportHandleRule.getTag();
        Set<RuleCondition> ruleConditions = reportHandleRule.getRuleConditions();
        this.reportType = reportHandleRule.findType(session);
        if (null == this.reportType) {
            this.reportType = ReportRuleConstants.UNKNOWN_REPORT_TYPE;
        }
        this.reportTemplateFileName = reportHandleRule.getTemplateFileName();
        if (null == this.reportTemplateFileName)
            this.reportTemplateFileName = ReportRuleConstants.DEFAULT_REPORT_TEMPLATE;
        this.enabled = reportHandleRule.isEnabled();
        this.documentFormat = reportHandleRule.getDocumentFormat();
        this.subject = reportHandleRule.getSubject();
        this.routeAddresses = new LinkedList<String>();
        addAddress(this.routeAddresses, reportHandleRule.getRoute0());
        addAddress(this.routeAddresses, reportHandleRule.getRoute1());
        addAddress(this.routeAddresses, reportHandleRule.getRoute2());
        addAddress(this.routeAddresses, reportHandleRule.getRoute3());
        addAddress(this.routeAddresses, reportHandleRule.getRoute4());
        addAddress(this.routeAddresses, reportHandleRule.getRoute5());
        addAddress(this.routeAddresses, reportHandleRule.getRoute6());
        addAddress(this.routeAddresses, reportHandleRule.getRoute7());
        addAddress(this.routeAddresses, reportHandleRule.getRoute8());
        addAddress(this.routeAddresses, reportHandleRule.getRoute9());
        this.ruleConditionItems = new LinkedList<RuleConditionItem>();
        for (RuleCondition currRuleCondition : ruleConditions) {
            if (!currRuleCondition.isTypeCondition()) {
                this.ruleConditionItems.add(new RuleConditionItem(currRuleCondition));
            }
        }
        this.shortName = ReportRuleConstants.createShortName(reportHandleRule, 64);

        this.paramHints = new LinkedList<ReportRuleConstants.ParamHint>();
        ReportRuleConstants.ReportHint reportHint = ReportRuleConstants.findReportHint(this.reportType);
        for (int i : reportHint.getParamHints()) {
            this.paramHints.add(ReportRuleConstants.PARAM_HINTS[i]);
        }
    }

    private static void addAddress(List<String> addresses, String newAddress) {
        if (StringUtils.isNotEmpty(newAddress)) {
            addresses.add(newAddress);
        }
    }

    public void setReportTemplateFileName(String reportTemplateFileName) {
        this.reportTemplateFileName = reportTemplateFileName;
    }

    public String getReportTemplateFileName() {
        return reportTemplateFileName;
    }

    public String getTag() {
        return tag;
    }
}