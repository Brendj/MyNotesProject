/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.event;

import ru.axetta.ecafe.processor.core.event.PaymentProcessEvent;
import ru.axetta.ecafe.processor.core.event.SyncEvent;
import ru.axetta.ecafe.processor.core.persistence.ReportHandleRule;
import ru.axetta.ecafe.processor.core.persistence.RuleCondition;

import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class EventConstants {

    public static final String ELIDE_FILL = "...";
    public static final String UNKNOWN_EVENT_TYPE = "Неизвестный";

    static public String createShortName(ReportHandleRule reportHandleRule, int maxLen) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(reportHandleRule.getIdOfReportHandleRule().toString());
        String ruleName = reportHandleRule.getRuleName();
        if (StringUtils.isNotEmpty(ruleName)) {
            stringBuilder.append(": ").append(ruleName);
        }
        int len = stringBuilder.length();
        if (len > maxLen) {
            return stringBuilder.substring(0, maxLen - ELIDE_FILL.length()) + ELIDE_FILL;
        }
        return stringBuilder.toString();
    }

    public static class ParamHint {

        private final String name;
        private final String description;

        public ParamHint(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    public static class EventHint {

        private final String typeName;
        private final int[] paramHints;

        public EventHint(String typeName, int[] paramHints) {
            this.typeName = typeName;
            this.paramHints = paramHints;
        }

        public String getTypeName() {
            return typeName;
        }

        public int[] getParamHints() {
            return paramHints;
        }
    }

    //todo - add more
    public static final ParamHint[] PARAM_HINTS = {
            new ParamHint("eventTime", "Дата и время появления события"), new ParamHint("user.idOfUser",
                    "Идентификатор пользователя, от имени которого проводилась обработка реестра платежей"),
            new ParamHint("user.userName",
                    "Имя пользователя, от имени которого проводилась обработка реестра платежей"),
            new ParamHint("contragent.idOfContragent",
                    "Идентификатор контрагента по приему платежей, от имени которого проводилась обработка реестра платежей"),
            new ParamHint("contragent.contragentName",
                    "Имя контрагента по приему платежей, от имени которого проводилась обработка реестра платежей"),
            new ParamHint("org.idOfOrg", "Идентификатор организации, по которой проводилась синхронизация"),
            new ParamHint("org.shortName", "Краткое название организации, по которой проводилась синхронизация"),
            new ParamHint("org.officialName",
                    "Официальное название организации, по которой проводилась синхронизация")};

    //todo - add more
    public static final EventHint[] EVENT_HINTS = {
            new EventHint(PaymentProcessEvent.class.getCanonicalName(), new int[]{0, 1, 2, 3, 4}),
            new EventHint(SyncEvent.class.getCanonicalName(), new int[]{0, 5, 6, 7})};

    public static RuleCondition buildTypeCondition(ReportHandleRule reportHandleRule, String eventType) {
        return new RuleCondition(reportHandleRule, RuleCondition.EQUAL_OPERTAION, RuleCondition.TYPE_CONDITION_ARG,
                eventType);
    }

    public static EventHint findEventHint(String eventTypeName) {
        for (EventHint eventHint : EVENT_HINTS) {
            if (StringUtils.equals(eventHint.getTypeName(), eventTypeName)) {
                return eventHint;
            }
        }
        return null;
    }

}