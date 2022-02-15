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
public class EventNotificationViewPage extends BasicWorkspacePage {

    private long idOfReportHandleRule;
    private String notificationName;
    private boolean enabled;
    private String eventType;
    private int documentFormat;
    private String subject;
    private List<String> routeAddresses = Collections.emptyList();
    private List<ReportConditionItem> ruleConditionItems = Collections.emptyList();
    private String shortName;
    private List<EventConstants.ParamHint> paramHints = Collections.emptyList();

    public String getPageFilename() {
        return "event_notification/view";
    }

    public long getIdOfReportHandleRule() {
        return idOfReportHandleRule;
    }

    public String getNotificationName() {
        return notificationName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getEventType() {
        return eventType;
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

    public List<ReportConditionItem> getRuleConditionItems() {
        return ruleConditionItems;
    }

    public String getShortName() {
        return shortName;
    }

    public List<EventConstants.ParamHint> getParamHints() {
        return paramHints;
    }

    public void fill(Session session, Long idOfReportHandleRule) throws Exception {
        ReportHandleRule reportHandleRule = (ReportHandleRule) session
                .load(ReportHandleRule.class, idOfReportHandleRule);
        this.idOfReportHandleRule = reportHandleRule.getIdOfReportHandleRule();
        this.notificationName = reportHandleRule.getRuleName();
        Set<RuleCondition> ruleConditions = reportHandleRule.getRuleConditions();
        this.eventType = reportHandleRule.findType(session);
        if (null == this.eventType) {
            this.eventType = EventConstants.UNKNOWN_EVENT_TYPE;
        }
        this.enabled = reportHandleRule.isEnabled();
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
            if (!currRuleCondition.isTypeCondition()) {
                this.ruleConditionItems.add(new ReportConditionItem(currRuleCondition));
            }
        }
        this.shortName = EventConstants.createShortName(reportHandleRule, 64);

        this.paramHints = new LinkedList<>();
        EventConstants.EventHint reportHint = EventConstants.findEventHint(this.eventType);
        for (int i : reportHint.getParamHints()) {
            this.paramHints.add(EventConstants.PARAM_HINTS[i]);
        }
    }
}