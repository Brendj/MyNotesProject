/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.rule;

import ru.axetta.ecafe.processor.core.persistence.ReportHandleRule;
import ru.axetta.ecafe.processor.core.persistence.RuleCondition;
import ru.axetta.ecafe.processor.core.report.ReportRuleConstants;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.RuleConditionItem;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ReportRuleListPage extends BasicWorkspacePage {

    public static class RuleItem {

        private static final int RULE_NAME_MAX_LEN = 50;

        private final long idOfReportHandleRule;
        private final String ruleName;
        private final String tag;
        private final boolean enabled;
        private final String reportType;
        private final int documentFormat;
        private final String subject;
        private final List<String> routeAddresses;
        private final List<RuleConditionItem> ruleConditionItems;

        public RuleItem(Session session, ReportHandleRule reportHandleRule) throws Exception {
            this.idOfReportHandleRule = reportHandleRule.getIdOfReportHandleRule();
            this.ruleName = cutRuleName(reportHandleRule.getRuleName());
            this.tag = reportHandleRule.getTag();
            this.enabled = reportHandleRule.isEnabled();
            Set<RuleCondition> ruleConditions = reportHandleRule.getRuleConditions();
            String reportType = reportHandleRule.findType(session);
            if (null == reportType) {
                this.reportType = ReportRuleConstants.UNKNOWN_REPORT_TYPE;
            } else {
                this.reportType = cutReportType(reportType);
            }
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
                if (currRuleCondition.isTypeCondition()) {
                    this.ruleConditionItems.add(new RuleConditionItem(currRuleCondition));
                }
            }
        }

        private static void addAddress(List<String> addresses, String newAddress) {
            if (StringUtils.isNotEmpty(newAddress)) {
                addresses.add(newAddress);
            }
        }

        private static String cutRuleName(String ruleName) {
            StringBuilder stringBuilder = new StringBuilder();
            if (StringUtils.isNotEmpty(ruleName)) {
                stringBuilder.append(ruleName);
            }
            int len = stringBuilder.length();
            if (len > RULE_NAME_MAX_LEN) {
                return stringBuilder.substring(0, RULE_NAME_MAX_LEN - ReportRuleConstants.ELIDE_FILL.length())
                        + ReportRuleConstants.ELIDE_FILL;
            }
            return stringBuilder.toString();
        }

        private static String cutReportType(String reportType) {
            if (reportType.startsWith(RuleCondition.REPORT_TYPE_BASE_PART)) {
                return ReportRuleConstants.ELIDE_FILL + reportType
                        .substring(RuleCondition.REPORT_TYPE_BASE_PART.length());
            }
            return reportType;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public List<RuleConditionItem> getRuleConditionItems() {
            return ruleConditionItems;
        }

        public String getReportType() {
            return reportType;
        }

        public long getIdOfReportHandleRule() {
            return idOfReportHandleRule;
        }

        public String getRuleName() {
            return ruleName;
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

        public List<RuleConditionItem> getRuleConditions() {
            return ruleConditionItems;
        }

        public String getTag() {
            return tag;
        }
    }

    private List<RuleItem> items = Collections.emptyList();

    public String getPageFilename() {
        return "report/rule/list";
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    public List<RuleItem> getRules() {
        return items;
    }

    public int getRuleCount() {
        return items.size();
    }

    public void fill(Session session) throws Exception {
        List<RuleItem> newRuleItems = new LinkedList<RuleItem>();
        Criteria reportRulesCriteria = ReportHandleRule.createAllReportRulesCriteria(session);
        List rules = reportRulesCriteria.list();
        for (Object currObject : rules) {
            ReportHandleRule currRule = (ReportHandleRule) currObject;
            newRuleItems.add(new RuleItem(session, currRule));
        }
        this.items = newRuleItems;
    }

    public void removeReportRule(Session session, Long idOfReportHandleRule) throws Exception {
        ReportHandleRule reportHandleRule = (ReportHandleRule) session
                .load(ReportHandleRule.class, idOfReportHandleRule);
        session.delete(reportHandleRule);
        fill(session);
    }

}