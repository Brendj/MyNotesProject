/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.event;

import ru.axetta.ecafe.processor.core.persistence.ReportHandleRule;
import ru.axetta.ecafe.processor.core.persistence.ReportHandleRuleRoute;
import ru.axetta.ecafe.processor.core.persistence.RuleCondition;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.ReportConditionItem;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class EventNotificationListPage extends BasicWorkspacePage {

    public static class RuleItem {

        private static final int RULE_NAME_MAX_LEN = 16;

        private final long idOfReportHandleRule;
        private final String ruleName;
        private final boolean enabled;
        private final String notificationType;
        private final int documentFormat;
        private final String subject;
        private final List<String> routeAddresses;
        private final List<ReportConditionItem> ruleConditionItems;

        public RuleItem(Session session, ReportHandleRule reportHandleRule) throws Exception {
            this.idOfReportHandleRule = reportHandleRule.getIdOfReportHandleRule();
            this.ruleName = cutRuleName(reportHandleRule.getRuleName());
            this.enabled = reportHandleRule.isEnabled();
            Set<RuleCondition> ruleConditions = reportHandleRule.getRuleConditions();
            String notificationType = reportHandleRule.findType(session);
            if (null == notificationType) {
                this.notificationType = EventConstants.UNKNOWN_EVENT_TYPE;
            } else {
                this.notificationType = cutRuleType(notificationType);
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
                    this.ruleConditionItems.add(new ReportConditionItem(currRuleCondition));
                }
            }
        }

        public boolean isEnabled() {
            return enabled;
        }

        private static String cutRuleName(String ruleName) {
            StringBuilder stringBuilder = new StringBuilder();
            if (StringUtils.isNotEmpty(ruleName)) {
                stringBuilder.append(ruleName);
            }
            int len = stringBuilder.length();
            if (len > RULE_NAME_MAX_LEN) {
                return stringBuilder.substring(0, RULE_NAME_MAX_LEN - EventConstants.ELIDE_FILL.length())
                        + EventConstants.ELIDE_FILL;
            }
            return stringBuilder.toString();
        }

        private static String cutRuleType(String reportType) {
            if (reportType.startsWith(RuleCondition.EVENT_TYPE_BASE_PART)) {
                return EventConstants.ELIDE_FILL + reportType.substring(RuleCondition.EVENT_TYPE_BASE_PART.length());
            }
            return reportType;
        }

        public String getNotificationType() {
            return notificationType;
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

        public List<ReportConditionItem> getRuleConditions() {
            return ruleConditionItems;
        }

    }

    private List<RuleItem> ruleItems = Collections.emptyList();

    public String getPageFilename() {
        return "event_notification/list";
    }

    public List<RuleItem> getRules() {
        return ruleItems;
    }

    public int getRuleCount() {
        return ruleItems.size();
    }

    public void fill(Session session) throws Exception {
        List<RuleItem> newRuleItems = new LinkedList<>();
        Criteria notificationRulesCriteria = ReportHandleRule.createAllEventNotificationsCriteria(session);
        List<ReportHandleRule> rules = notificationRulesCriteria.list();
        for (ReportHandleRule currRule : rules) {
            newRuleItems.add(new RuleItem(session, currRule));
        }
        this.ruleItems = newRuleItems;
    }

    public void removeEventNotification(Session session, Long idOfNotificationHandleRule) throws Exception {
        ReportHandleRule reportHandleRule = (ReportHandleRule) session
                .load(ReportHandleRule.class, idOfNotificationHandleRule);
        session.delete(reportHandleRule);
        fill(session);
    }

}