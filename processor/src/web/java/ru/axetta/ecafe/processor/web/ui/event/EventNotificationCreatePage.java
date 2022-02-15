/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.event;

import ru.axetta.ecafe.processor.core.persistence.ReportHandleRule;
import ru.axetta.ecafe.processor.core.persistence.ReportHandleRuleRoute;
import ru.axetta.ecafe.processor.core.persistence.RuleCondition;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.ReportConditionItem;
import ru.axetta.ecafe.processor.web.ui.ReportFormatMenu;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class EventNotificationCreatePage extends BasicWorkspacePage {

    private static String DELIMETER = ",";

    public static class EventParamHint {

        private final String typeName;
        private final List<EventConstants.ParamHint> paramHints;

        public EventParamHint(EventConstants.EventHint reportHint) {
            this.typeName = reportHint.getTypeName();
            this.paramHints = new LinkedList<>();
            for (int i : reportHint.getParamHints()) {
                this.paramHints.add(EventConstants.PARAM_HINTS[i]);
            }
        }

        public String getTypeName() {
            return typeName;
        }

        public List<EventConstants.ParamHint> getParamHints() {
            return paramHints;
        }
    }

    private String notificationName;
    private boolean enabled;
    private String eventType;
    private int documentFormat;
    private String subject;
    private String routeAddresses;
    private String ruleConditionItems;
    private final EventTypeMenu eventTypeMenu = new EventTypeMenu();
    private final ReportFormatMenu reportFormatMenu = new ReportFormatMenu();
    private List<EventParamHint> eventParamHints = Collections.emptyList();

    public String getPageFilename() {
        return "event_notification/create";
    }

    public EventNotificationCreatePage() {
        this.eventParamHints = new LinkedList<>();
        for (EventConstants.EventHint eventHint : EventConstants.EVENT_HINTS) {
            this.eventParamHints.add(new EventParamHint(eventHint));
        }
        this.documentFormat = 0;
        this.enabled = true;
    }

    public List<EventParamHint> getEventParamHints() {
        return eventParamHints;
    }

    public void setEventParamHints(List<EventParamHint> eventParamHints) {
        this.eventParamHints = eventParamHints;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public EventTypeMenu getEventTypeMenu() {
        return eventTypeMenu;
    }

    public ReportFormatMenu getReportFormatMenu() {
        return reportFormatMenu;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getNotificationName() {
        return notificationName;
    }

    public void setNotificationName(String notificationName) {
        this.notificationName = notificationName;
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

    public void fill(Session session) throws Exception {
        this.documentFormat = 0;
    }

    public void createEventNotification(Session session) throws Exception {
        String[] addressList = this.routeAddresses.split(DELIMETER);

        ReportHandleRule reportHandleRule = new ReportHandleRule(this.documentFormat, this.subject,
                this.enabled);
        reportHandleRule.setRuleName(this.notificationName);

        for(String addr : addressList){
            String processedAddress = StringUtils.trim(addr);

            ReportHandleRuleRoute route = new ReportHandleRuleRoute(processedAddress, reportHandleRule);
            reportHandleRule.getRoutes().add(route);
        }

        reportHandleRule.addRuleCondition(EventConstants.buildTypeCondition(reportHandleRule, this.eventType));
        String[] textRuleConditions = this.ruleConditionItems.split(DELIMETER);
        for (String textRuleCondition : textRuleConditions) {
            String trimmedTextRuleCondition = StringUtils.trim(textRuleCondition);
            if (StringUtils.isNotEmpty(trimmedTextRuleCondition)) {
                ReportConditionItem conditionItem = new ReportConditionItem(trimmedTextRuleCondition);
                reportHandleRule.addRuleCondition(
                        new RuleCondition(reportHandleRule, conditionItem.getConditionOperation(),
                                conditionItem.getConditionArgument(), conditionItem.getConditionConstant()));
            }
        }
        session.save(reportHandleRule);
    }
}