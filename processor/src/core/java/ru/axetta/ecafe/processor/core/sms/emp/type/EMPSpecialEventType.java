/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.persistence.Client;

import java.util.Map;

public class EMPSpecialEventType extends EMPAbstractEventType {
    protected static final String NOTIFICATION_START_SICK_NAME = "Рекомендация об освобождении";
    protected static final String NOTIFICATION_START_SICK_TEXT = "%surname% %name% получил рекомендацию об освобождении с %empDate% по %empTimeH%";
    protected static final String NOTIFICATION_CANCEL_START_SICK_NAME = "Аннулирование рекомендаций об освобождении";
    protected static final String NOTIFICATION_CANCEL_START_SICK_TEXT = "%surname% %name% получил аннулирование рекомендаций об освобождении с %empDate% по %empTimeH%";
    protected static final String NOTIFICATION_END_SICK_NAME = "Рекомендация о возможности посещать ОО";
    protected static final String NOTIFICATION_END_SICK_TEXT = "%surname% %name% получил рекомендация о возможности посещать ОО с %empTime%";
    protected static final String NOTIFICATION_CANCEL_END_SICK_NAME = "Аннулирование рекомендаций о возможности посещать ОО";
    protected static final String NOTIFICATION_CANCEL_END_SICK_TEXT = "%surname% %name% получил аннулирование рекомендаций о возможности посещать ОО %empTime%";

    public EMPSpecialEventType(Integer typeE) {
        stream = STREAM;
        type = EMPEventTypeFactory.SPECIAL_TYPE_EVENT;
        switch (typeE)
        {
            case 1:
                name = NOTIFICATION_START_SICK_NAME;
                text = NOTIFICATION_START_SICK_TEXT;
                break;
            case 2:
                name = NOTIFICATION_CANCEL_START_SICK_NAME;
                text = NOTIFICATION_CANCEL_START_SICK_TEXT;
                break;
            case 3:
                name = NOTIFICATION_END_SICK_NAME;
                text = NOTIFICATION_END_SICK_TEXT;
                break;
            case 4:
                name = NOTIFICATION_CANCEL_END_SICK_NAME;
                text = NOTIFICATION_CANCEL_END_SICK_TEXT;
                break;
            default:
                name = NOTIFICATION_START_SICK_NAME;
                text = NOTIFICATION_START_SICK_TEXT;
                break;
        }
    }

    @Override
    public void parse(Client client, Map<String, Object> additionalParams) {
        parseClientSimpleInfo(client);
    }

    @Override
    public void parse(Client child, Client guardian, Map<String, Object> additionalParams) {
        parseChildAndGuardianInfo(child, guardian);
    }
}