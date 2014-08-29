/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.persistence.Client;

import java.util.Collections;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 26.08.14
 * Time: 19:05
 * To change this template use File | Settings | File Templates.
 */
public class EMPEventTypeFactory {
    public static final int ENTER_EVENT               = 901240001;
    public static final int LEAVE_EVENT               = 901240002;
    public static final int ENTER_WITH_GUARDIAN_EVENT = 901240003;
    public static final int LEAVE_WITH_GUARDIAN_EVENT = 901240004;
    public static final int FILL_EVENT                = 901240005;

    public static final EMPEventType buildEvent(int type, Client client) {
        return buildEvent(type, client, Collections.EMPTY_MAP);
    }

    public static final EMPEventType buildEvent(int type, Client client, Map<String, Object> additionalParams) {
        EMPEventType event;
        switch (type) {
            case ENTER_EVENT:
                event = new EMPEnterEventType();
                break;
            case LEAVE_EVENT:
                event = new EMPLeaveEventType();
                break;
            case ENTER_WITH_GUARDIAN_EVENT:
                event = new EMPEnterWithGuardianEventType();
                break;
            case LEAVE_WITH_GUARDIAN_EVENT:
                event = new EMPLeaveWithGuardianEventType();
                break;
            case FILL_EVENT:
                event = new EMPFillEventType();
                break;
            default:
                throw new IllegalArgumentException("Unknown type");
        }
        event.parse(client, additionalParams);
        return event;
    }
}
