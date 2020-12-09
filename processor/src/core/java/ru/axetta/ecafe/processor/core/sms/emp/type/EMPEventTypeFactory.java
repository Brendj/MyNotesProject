/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.persistence.Client;

import java.util.Collections;
import java.util.Date;
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
    public static final int LOW_BALANCE_EVENT         = 901240012;
    public static final int ENTER_MUSEUM_EVENT        = 901240013;
    public static final int NOENTER_MUSEUM_EVENT      = 901240014;
    public static final int REGULAR_PAYMENT_EXPIRATION_EVENT  = 901240015;
    public static final int ENTER_WITH_CHECKER        = 901240016;
    public static final int LEAVE_WITH_CHECKER        = 901240017;
    public static final int INFO_MAILING_EVENT        = 901240056;
    public static final int CLIENT_NEWPASSWORD_EVENT  = 901240057;
    public static final int SPECIAL_TYPE_EVENT        = 901240018;
    public static final int END_BENEFIT               = 901240019;
    public static final int REFUSAL_PREFERENTIAL_FOOD = 901240020;
    public static final int ENTER_CULTURE             = 901240021;
    public static final int LEAVE_CULTURE             = 901240022;
    public static final int CANCEL_PREORDER           = 901240023;
    public static final int ENTER_LIBRARY             = 901240024;


    //Параметр modifired введен для определения: точно ли произошедшее событие соответствует коду события по умолчанию
    //Например для события прохода код только один (901240001), а могут произойти 2 события: проход в школу и проход в здание культуры

    public static final EMPEventType buildEvent(int type, Client client, int modifired, String[] values) {
        return buildEventMain(type, client,  modifired, values);
    }

    public static final EMPEventType buildEvent(int type, Client client, String[] values) {
        return buildEventMain(type, client, 0, values);
    }

    public static final EMPEventType buildEvent(int type, Client client) {
        return buildEventMain(type, client, 0, null);
    }

    public static final EMPEventType buildEvent(int type, Client child, Client guardian, int modifired, String[] values) {
        return buildEventMain(type, child, guardian, modifired, values);
    }

    public static final EMPEventType buildEvent(int type, Client child, Client guardian, String[] values) {
        return buildEventMain(type, child, guardian,  0, values);
    }

    public static final EMPEventType buildEvent(int type, Client child, Client guardian) {
        return buildEventMain(type, child, guardian, 0,  null);
    }

    public static final EMPEventType buildEventMain(int type, Client client, int modifired, String[] values) {
        EMPEventType event = getEmpEventType(type, modifired, values);
        event.parse(client);
        return event;
    }

    public static final EMPEventType buildEventMain(int type, Client child, Client guardian, int modifired, String[] values) {
        EMPEventType event = getEmpEventType(type, modifired, values);
        event.parse(child, guardian);
        return event;
    }

    private static EMPEventType getEmpEventType(int type, int modifired, String values[]) {
        EMPEventType event;
        switch (type) {
            case ENTER_EVENT:
                event = new EMPEnterEventType();
                break;
            case LEAVE_EVENT:
                event = new EMPLeaveEventType();
                break;
            case ENTER_CULTURE:
                event = new EMPEnterCultureEventType(values);
                break;
            case LEAVE_CULTURE:
                event = new EMPExitCultureEventType(values);
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
            case LOW_BALANCE_EVENT:
                event = new EMPLowBalanceEventType();
                break;
            case ENTER_MUSEUM_EVENT:
                event = new EMPEnterMuseumEventType(values);
                break;
            case NOENTER_MUSEUM_EVENT:
                event = new EMPNoEnterMuseumEventType(values);
                break;
            case CLIENT_NEWPASSWORD_EVENT:
                event = new EMPClientNewPasswordEventType();
                break;
            case REGULAR_PAYMENT_EXPIRATION_EVENT:
                event = new EMPRegularPaymentExpirationEventType();
                break;
            case ENTER_WITH_CHECKER:
                event = new EMPEnterWithCheckerEventType();
                break;
            case LEAVE_WITH_CHECKER:
                event = new EMPLeaveWithCheckerEventType();
                break;
            case END_BENEFIT:
                event = new EMPEndBenefitType(values);
                break;
            case REFUSAL_PREFERENTIAL_FOOD:
                event = new EMPRefusalPreferentialFoodType(values);
                break;
            case SPECIAL_TYPE_EVENT:
                switch (modifired)
                {
                    case 2:
                        event = new EMPSpecialEventType(1, values);
                        break;
                    case 3:
                        event = new EMPSpecialEventType(2, values);
                        break;
                    default:
                        event = new EMPSpecialEventType(1, values);
                        break;
                }
                break;
            case ENTER_LIBRARY:
                event = new EMPLibraryEventType(values);
                break;
            case CANCEL_PREORDER:
                event = new EMPCancelPreorderEventType(values);
                break;
            default:
                throw new IllegalArgumentException("Unknown type");
        }
        return event;
    }
}
