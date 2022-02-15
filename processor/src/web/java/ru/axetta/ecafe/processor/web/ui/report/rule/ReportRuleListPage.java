/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.rule;

import ru.axetta.ecafe.processor.core.persistence.ReportHandleRule;
import ru.axetta.ecafe.processor.core.persistence.ReportHandleRuleRoute;
import ru.axetta.ecafe.processor.core.persistence.RuleCondition;
import ru.axetta.ecafe.processor.core.report.ReportRuleConstants;
import ru.axetta.ecafe.processor.core.report.RuleConditionItem;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
            this.routeAddresses = new LinkedList<>();

            this.routeAddresses.addAll(
                    reportHandleRule.getRoutes()
                            .stream()
                            .map(ReportHandleRuleRoute::getRoute)
                            .collect(Collectors.toList())
            );

            this.ruleConditionItems = new LinkedList<>();
            for (RuleCondition currRuleCondition : ruleConditions) {
                if (currRuleCondition.isTypeCondition()) {
                    this.ruleConditionItems.add(new RuleConditionItem(currRuleCondition));
                }
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
        List<RuleItem> newRuleItems = new LinkedList<>();
        Criteria reportRulesCriteria = ReportHandleRule.createAllReportRulesCriteria(session);
        List<ReportHandleRule> rules = reportRulesCriteria.list();
        for (ReportHandleRule currRule : rules) {
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