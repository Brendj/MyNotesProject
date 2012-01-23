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

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ReportRuleEditPage extends BasicWorkspacePage {

    private static String DELIMETER = ",";

    public static class ReportParamHint {

        private final String typeName;
        private final List<ReportRuleConstants.ParamHint> paramHints;

        public ReportParamHint(ReportRuleConstants.ReportHint reportHint) {
            this.typeName = reportHint.getTypeName();
            this.paramHints = new LinkedList<ReportRuleConstants.ParamHint>();
            for (int i : reportHint.getParamHints()) {
                this.paramHints.add(ReportRuleConstants.PARAM_HINTS[i]);
            }
        }

        public String getTypeName() {
            return typeName;
        }

        public List<ReportRuleConstants.ParamHint> getParamHints() {
            return paramHints;
        }
    }

    private long idOfReportHandleRule;
    private String ruleName;
    private String reportType;
    private int documentFormat;
    private String subject;
    private String routeAddresses;
    private String ruleConditionItems;
    private String shortName;
    private boolean enabled;
    private final ReportTypeMenu reportTypeMenu = new ReportTypeMenu();
    private final ReportFormatMenu reportFormatMenu = new ReportFormatMenu();
    private List<ReportParamHint> reportParamHints = Collections.emptyList();

    public String getPageFilename() {
        return "report/rule/edit";
    }

    public ReportRuleEditPage() {
        this.reportParamHints = new LinkedList<ReportParamHint>();
        for (ReportRuleConstants.ReportHint reportHint : ReportRuleConstants.REPORT_HINTS) {
            this.reportParamHints.add(new ReportParamHint(reportHint));
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<ReportParamHint> getReportParamHints() {
        return reportParamHints;
    }

    public void setReportParamHints(List<ReportParamHint> reportParamHints) {
        this.reportParamHints = reportParamHints;
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

    public long getIdOfReportHandleRule() {
        return idOfReportHandleRule;
    }

    public void setIdOfReportHandleRule(long idOfReportHandleRule) {
        this.idOfReportHandleRule = idOfReportHandleRule;
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

    public void fill(Session session, Long idOfReportHandleRule) throws Exception {
        ReportHandleRule reportHandleRule = (ReportHandleRule) session
                .load(ReportHandleRule.class, idOfReportHandleRule);
        fill(session, reportHandleRule);
    }

    public void updateReportRule(Session session, Long idOfReportHandleRule) throws Exception {
        ReportHandleRule reportHandleRule = (ReportHandleRule) session
                .load(ReportHandleRule.class, idOfReportHandleRule);
        reportHandleRule.setRuleName(this.ruleName);
        reportHandleRule.setDocumentFormat(this.documentFormat);
        reportHandleRule.setSubject(this.subject);
        reportHandleRule.setEnabled(this.enabled);

        String[] addressList = this.routeAddresses.split(DELIMETER);
        reportHandleRule.setRoute0(StringUtils.trim(getString(addressList, 0)));
        reportHandleRule.setRoute1(StringUtils.trim(getString(addressList, 1)));
        reportHandleRule.setRoute2(StringUtils.trim(getString(addressList, 2)));
        reportHandleRule.setRoute3(StringUtils.trim(getString(addressList, 3)));
        reportHandleRule.setRoute4(StringUtils.trim(getString(addressList, 4)));
        reportHandleRule.setRoute5(StringUtils.trim(getString(addressList, 5)));
        reportHandleRule.setRoute6(StringUtils.trim(getString(addressList, 6)));
        reportHandleRule.setRoute7(StringUtils.trim(getString(addressList, 7)));
        reportHandleRule.setRoute8(StringUtils.trim(getString(addressList, 8)));
        reportHandleRule.setRoute9(StringUtils.trim(getString(addressList, 9)));

        Set<RuleCondition> newRuleConditions = new HashSet<RuleCondition>();
        newRuleConditions.add(ReportRuleConstants.buildTypeCondition(reportHandleRule, this.reportType));

        String[] textRuleConditions = this.ruleConditionItems.split(DELIMETER);
        for (String textRuleCondition : textRuleConditions) {
            String trimmedTextRuleCondition = StringUtils.trim(textRuleCondition);
            if (StringUtils.isNotEmpty(trimmedTextRuleCondition)) {
                RuleConditionItem conditionItem = new RuleConditionItem(trimmedTextRuleCondition);
                newRuleConditions.add(new RuleCondition(reportHandleRule, conditionItem.getConditionOperation(),
                        conditionItem.getConditionArgument(), conditionItem.getConditionConstant()));
            }
        }

        Set<RuleCondition> superfluousRuleConditions = new HashSet<RuleCondition>();
        for (RuleCondition ruleCondition : reportHandleRule.getRuleConditions()) {
            if (!newRuleConditions.contains(ruleCondition)) {
                superfluousRuleConditions.add(ruleCondition);
            }
        }

        for (RuleCondition ruleCondition : superfluousRuleConditions) {
            reportHandleRule.removeRuleCondition(ruleCondition);
            session.delete(ruleCondition);
        }

        for (RuleCondition ruleCondition : newRuleConditions) {
            if (!reportHandleRule.getRuleConditions().contains(ruleCondition)) {
                session.save(ruleCondition);
                reportHandleRule.addRuleCondition(ruleCondition);
            }
        }
        session.update(reportHandleRule);
        fill(session, reportHandleRule);
    }

    private static String getString(String[] strings, int i) {
        if (0 <= i && i < strings.length) {
            return strings[i];
        }
        return null;
    }

    private void fill(Session session, ReportHandleRule reportHandleRule) throws Exception {
        this.idOfReportHandleRule = reportHandleRule.getIdOfReportHandleRule();
        this.ruleName = reportHandleRule.getRuleName();

        Set<RuleCondition> ruleConditions = reportHandleRule.getRuleConditions();
        this.reportType = reportHandleRule.findType(session);
        if (null == this.reportType) {
            this.reportType = ReportRuleConstants.UNKNOWN_REPORT_TYPE;
        }
        this.documentFormat = reportHandleRule.getDocumentFormat();
        this.subject = reportHandleRule.getSubject();

        StringBuilder routeAddresses = new StringBuilder();
        appendNotEmpty(routeAddresses, reportHandleRule.getRoute0());
        appendNotEmpty(routeAddresses, reportHandleRule.getRoute1());
        appendNotEmpty(routeAddresses, reportHandleRule.getRoute2());
        appendNotEmpty(routeAddresses, reportHandleRule.getRoute3());
        appendNotEmpty(routeAddresses, reportHandleRule.getRoute4());
        appendNotEmpty(routeAddresses, reportHandleRule.getRoute5());
        appendNotEmpty(routeAddresses, reportHandleRule.getRoute6());
        appendNotEmpty(routeAddresses, reportHandleRule.getRoute7());
        appendNotEmpty(routeAddresses, reportHandleRule.getRoute8());
        appendNotEmpty(routeAddresses, reportHandleRule.getRoute9());
        this.routeAddresses = routeAddresses.toString();

        StringBuilder ruleConditionItems = new StringBuilder();
        for (RuleCondition currRuleCondition : ruleConditions) {
            if (!currRuleCondition.isTypeCondition()) {
                appendNotEmpty(ruleConditionItems, new RuleConditionItem(currRuleCondition).buildText());
            }
        }
        this.ruleConditionItems = ruleConditionItems.toString();
        this.enabled = reportHandleRule.isEnabled();
        this.shortName = ReportRuleConstants.createShortName(reportHandleRule, 64);
    }

    private static void appendNotEmpty(StringBuilder stringBuilder, String value) {
        if (StringUtils.isNotEmpty(value)) {
            if (0 != stringBuilder.length()) {
                stringBuilder.append(DELIMETER).append(' ');
            }
            stringBuilder.append(value);
        }
    }
}