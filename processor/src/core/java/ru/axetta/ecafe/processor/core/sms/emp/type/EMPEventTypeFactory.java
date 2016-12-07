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
    public static final int TOKEN_GENERATED_EVENT     = 901250001;
    public static final int PAYMENT_EVENT             = 901240007;
    public static final int PAYMENT_PAY_EVENT         = 901240008;
    public static final int PAYMENT_REDUCED_EVENT     = 901240009;
    public static final int SUMMARY_DAILY_EVENT       = 901240010;
    public static final int SUMMARY_WEEKLY_EVENT      = 901240011;
    public static final int INFO_MAILING_EVENT        = 901240056;

    public static final EMPEventType buildEvent(int type, Client client) {
        return buildEvent(type, client, Collections.EMPTY_MAP);
    }

    public static final EMPEventType buildEvent(int type, Client child, Client guardian) {
        return buildEvent(type, child, guardian, Collections.EMPTY_MAP);
    }

    public static final EMPEventType buildEvent(int type, Client client, Map<String, Object> additionalParams) {
        EMPEventType event = getEmpEventType(type);
        event.parse(client, additionalParams);
        return event;
    }

    public static final EMPEventType buildEvent(int type, Client child, Client guardian, Map<String, Object> additionalParams) {
        EMPEventType event = getEmpEventType(type);
        event.parse(child, guardian, additionalParams);
        return event;
    }

    private static EMPEventType getEmpEventType(int type) {
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
            case TOKEN_GENERATED_EVENT:
                event = new EMPTokenGenerateEventType();
                break;
            case PAYMENT_EVENT:
                event = new EMPPaymentEventType();
                break;
            case PAYMENT_PAY_EVENT:
                event = new EMPPaymentPayEventType();
                break;
            case PAYMENT_REDUCED_EVENT:
                event = new EMPPaymentReduceEventType();
                break;
            case SUMMARY_DAILY_EVENT:
                event = new EMPSummaryDailyEventType();
                break;
            case SUMMARY_WEEKLY_EVENT:
                event = new EMPSummaryWeeklyEventType();
                break;
            case INFO_MAILING_EVENT:
                event = new EMPInfoMailingEventType();
                break;
            default:
                throw new IllegalArgumentException("Unknown type");
        }
        event.setTime(System.currentTimeMillis());
        return event;
    }

}
