/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.event;

import ru.axetta.ecafe.processor.core.persistence.ReportHandleRule;
import ru.axetta.ecafe.processor.core.persistence.ReportHandleRuleRoute;
import ru.axetta.ecafe.processor.core.persistence.RuleCondition;
import ru.axetta.ecafe.processor.core.report.RuleConditionItem;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.ReportConditionItem;
import ru.axetta.ecafe.processor.web.ui.ReportFormatMenu;

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
public class EventNotificationEditPage extends BasicWorkspacePage {

    private static String DELIMETER = ",";

    public static class EventParamHint {

        private final String typeName;
        private final List<EventConstants.ParamHint> paramHints;

        public EventParamHint(EventConstants.EventHint reportHint) {
            this.typeName = reportHint.getTypeName();
            this.paramHints = new LinkedList<EventConstants.ParamHint>();
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

    private long idOfReportHandleRule;
    private String notificationName;
    private boolean enabled;
    private String eventType;
    private int documentFormat;
    private String subject;
    private String routeAddresses;
    private String ruleConditionItems;
    private String shortName;
    private final EventTypeMenu eventTypeMenu = new EventTypeMenu();
    private final ReportFormatMenu reportFormatMenu = new ReportFormatMenu();
    private List<EventParamHint> eventParamHints = Collections.emptyList();
    private Map<String, ReportHandleRuleRoute> routeMap = new HashMap<>();

    public String getPageFilename() {
        return "event_notification/edit";
    }

    public EventNotificationEditPage() {
        this.eventParamHints = new LinkedList<EventParamHint>();
        for (EventConstants.EventHint reportHint : EventConstants.EVENT_HINTS) {
            this.eventParamHints.add(new EventParamHint(reportHint));
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<EventParamHint> getEventParamHints() {
        return eventParamHints;
    }

    public void setEventParamHints(List<EventParamHint> eventParamHints) {
        this.eventParamHints = eventParamHints;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public long getIdOfReportHandleRule() {
        return idOfReportHandleRule;
    }

    public void setIdOfReportHandleRule(long idOfReportHandleRule) {
        this.idOfReportHandleRule = idOfReportHandleRule;
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

    public void setRouteAddresses(String routeAddresses) {
        this.routeAddresses = routeAddresses;
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

    public EventTypeMenu getEventTypeMenu() {
        return eventTypeMenu;
    }

    public ReportFormatMenu getReportFormatMenu() {
        return reportFormatMenu;
    }

    public void fill(Session session, Long idOfReportHandleRule) throws Exception {
        ReportHandleRule reportHandleRule = (ReportHandleRule) session
                .load(ReportHandleRule.class, idOfReportHandleRule);
        fill(session, reportHandleRule);
    }

    public void updateEventNotification(Session session, Long idOfReportHandleRule) throws Exception {
        ReportHandleRule reportHandleRule = (ReportHandleRule) session
                .get(ReportHandleRule.class, idOfReportHandleRule);
        reportHandleRule.setRuleName(this.notificationName);
        reportHandleRule.setDocumentFormat(this.documentFormat);
        reportHandleRule.setSubject(this.subject);
        reportHandleRule.setEnabled(this.enabled);

        String[] addressList = this.routeAddresses.split(DELIMETER);
        for(String addr : addressList){
            String processedAddress = StringUtils.trim(addr);
            ReportHandleRuleRoute route = this.routeMap.get(processedAddress);
            if(route == null){
                route = new ReportHandleRuleRoute(processedAddress, reportHandleRule);
                reportHandleRule.getRoutes().add(route);
                session.save(route);
            }
            this.routeMap.remove(processedAddress);
        }

        for(ReportHandleRuleRoute deleted : this.routeMap.values()){
            reportHandleRule.getRoutes().remove(deleted);
            deleted = (ReportHandleRuleRoute) session.merge(deleted);
            session.delete(deleted);
        }

        Set<RuleCondition> newRuleConditions = new HashSet<>();
        newRuleConditions.add(EventConstants.buildTypeCondition(reportHandleRule, this.eventType));

        String[] textRuleConditions = this.ruleConditionItems.split(DELIMETER);
        for (String textRuleCondition : textRuleConditions) {
            String trimmedTextRuleCondition = StringUtils.trim(textRuleCondition);
            if (StringUtils.isNotEmpty(trimmedTextRuleCondition)) {
                ReportConditionItem conditionItem = new ReportConditionItem(trimmedTextRuleCondition);
                newRuleConditions.add(new RuleCondition(reportHandleRule, conditionItem.getConditionOperation(),
                        conditionItem.getConditionArgument(), conditionItem.getConditionConstant()));
            }
        }

        Set<RuleCondition> superfluousRuleConditions = new HashSet<>();
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

    private void fill(Session session, ReportHandleRule reportHandleRule) throws Exception {
        this.idOfReportHandleRule = reportHandleRule.getIdOfReportHandleRule();
        this.notificationName = reportHandleRule.getRuleName();

        this.routeMap = new HashMap<>();
        for(ReportHandleRuleRoute r : reportHandleRule.getRoutes()){
            routeMap.put(r.getRoute(), r);
        }

        Set<RuleCondition> ruleConditions = reportHandleRule.getRuleConditions();
        this.eventType = reportHandleRule.findType(session);
        if (null == this.eventType) {
            this.eventType = EventConstants.UNKNOWN_EVENT_TYPE;
        }
        this.documentFormat = reportHandleRule.getDocumentFormat();
        this.subject = reportHandleRule.getSubject();

        StringBuilder routeAddresses = new StringBuilder();
        for(ReportHandleRuleRoute r : reportHandleRule.getRoutes()) {
            appendNotEmpty(routeAddresses, r.getRoute());
        }
        this.routeAddresses = routeAddresses.toString();

        StringBuilder ruleConditionItems = new StringBuilder();
        for (RuleCondition currRuleCondition : ruleConditions) {
            if (!currRuleCondition.isTypeCondition()) {
                appendNotEmpty(ruleConditionItems, new RuleConditionItem(currRuleCondition).buildText());
            }
        }
        this.ruleConditionItems = ruleConditionItems.toString();
        this.enabled = reportHandleRule.isEnabled();
        this.shortName = EventConstants.createShortName(reportHandleRule, 64);
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