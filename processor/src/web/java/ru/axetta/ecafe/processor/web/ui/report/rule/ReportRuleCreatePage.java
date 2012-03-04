/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.rule;

import ru.axetta.ecafe.processor.core.persistence.ReportHandleRule;
import ru.axetta.ecafe.processor.core.persistence.RuleCondition;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.ReportFormatMenu;
import ru.axetta.ecafe.processor.web.ui.RuleConditionItem;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ReportRuleCreatePage extends BasicWorkspacePage {

    private static String DELIMETER = ",";

    private String ruleName;
    private boolean enabled;
    private String reportType;
    private int documentFormat;
    private String subject;
    private String routeAddresses;
    private String ruleConditionItems;
    private String shortName;
    private final ReportTypeMenu reportTypeMenu = new ReportTypeMenu();
    private final ReportFormatMenu reportFormatMenu = new ReportFormatMenu();

    public String getPageFilename() {
        return "report/rule/create";
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ReportRuleCreatePage() {
        this.documentFormat = 0;
        this.enabled = true;
    }

    public ReportTypeMenu getReportTypeMenu() {
        return reportTypeMenu;
    }

    public ReportFormatMenu getReportFormatMenu() {
        return reportFormatMenu;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public int getDocumentFormat() {
        return documentFormat;
    }

    public void setDocumentFormat(int documentFormat) {
        this.documentFormat = documentFormat;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRouteAddresses() {
        return routeAddresses;
    }

    public void setRouteAddresses(String routeAdresses) {
        this.routeAddresses = routeAdresses;
    }

    public String getRuleConditionItems() {
        return ruleConditionItems;
    }

    public void setRuleConditionItems(String ruleConditionItems) {
        this.ruleConditionItems = ruleConditionItems;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void fill(Session session) throws Exception {
        this.documentFormat = 0;
    }

    public void createReportRule(Session session) throws Exception {
        String[] addressList = this.routeAddresses.split(DELIMETER);

        ReportHandleRule reportHandleRule = new ReportHandleRule(this.documentFormat, this.subject, addressList[0],
                this.enabled);
        reportHandleRule.setRuleName(this.ruleName);

        reportHandleRule.setRoute1(StringUtils.trim(getString(addressList, 1)));
        reportHandleRule.setRoute2(StringUtils.trim(getString(addressList, 2)));
        reportHandleRule.setRoute3(StringUtils.trim(getString(addressList, 3)));
        reportHandleRule.setRoute4(StringUtils.trim(getString(addressList, 4)));
        reportHandleRule.setRoute5(StringUtils.trim(getString(addressList, 5)));
        reportHandleRule.setRoute6(StringUtils.trim(getString(addressList, 6)));
        reportHandleRule.setRoute7(StringUtils.trim(getString(addressList, 7)));
        reportHandleRule.setRoute8(StringUtils.trim(getString(addressList, 8)));
        reportHandleRule.setRoute9(StringUtils.trim(getString(addressList, 9)));

        reportHandleRule.addRuleCondition(ReportRuleConstants.buildTypeCondition(reportHandleRule, this.reportType));
        String[] textRuleConditions = this.ruleConditionItems.split(DELIMETER);
        for (String textRuleCondition : textRuleConditions) {
            String trimmedTextRuleCondition = StringUtils.trim(textRuleCondition);
            if (StringUtils.isNotEmpty(trimmedTextRuleCondition)) {
                RuleConditionItem conditionItem = new RuleConditionItem(trimmedTextRuleCondition);
                reportHandleRule.addRuleCondition(
                        new RuleCondition(reportHandleRule, conditionItem.getConditionOperation(),
                                conditionItem.getConditionArgument(), conditionItem.getConditionConstant()));
            }
        }
        session.save(reportHandleRule);
    }

    private static String getString(String[] strings, int i) {
        if (0 <= i && i < strings.length) {
            return strings[i];
        }
        return null;
    }
}