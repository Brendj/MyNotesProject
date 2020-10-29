/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.service.ExternalEventNotificationService;

import java.util.Map;

public class EMPSpecialEventType extends EMPAbstractEventType {
    protected static final String NOTIFICATION_START_OR_CANCEL_SICK_NAME = "Рекомендация или аннултрование освобождения";
    protected static final String NOTIFICATION_START_OR_CANCEL_SICK_TEXT = "%surname% %name% получил рекомендацию (аннулирование) об освобождении с %empDate% по %empTimeH%";
    protected static final String NOTIFICATION_END_OR_CANCEL_END_SICK_NAME = "Рекомендация (отмена рекомендации) о возможности посещать ОО";
    protected static final String NOTIFICATION_END_OR_CANCEL_END_SICK_TEXT = "%surname% %name% получил рекомендация (отмену рекомендации) о возможности посещать ОО с %empTime%";

    public EMPSpecialEventType(Integer typeE, String[] values) {
        stream = STREAM;
        type = EMPEventTypeFactory.SPECIAL_TYPE_EVENT;
        switch (typeE)
        {
            case 1:
                name = NOTIFICATION_START_OR_CANCEL_SICK_NAME;
                text = NOTIFICATION_START_OR_CANCEL_SICK_TEXT;
                this.getParameters().put(ExternalEventNotificationService.EMP_DATE,
                        findValueInParams(new String[]{ExternalEventNotificationService.EMP_DATE}, values));
                this.getParameters().put(ExternalEventNotificationService.EMP_TIME_H,
                        findValueInParams(new String[]{ExternalEventNotificationService.EMP_TIME_H}, values));
                break;
            case 2:
                name = NOTIFICATION_END_OR_CANCEL_END_SICK_NAME;
                text = NOTIFICATION_END_OR_CANCEL_END_SICK_TEXT;
                this.getParameters().put(ExternalEventNotificationService.EMP_TIME,
                        findValueInParams(new String[]{ExternalEventNotificationService.EMP_TIME}, values));
                break;
            default:
                name = NOTIFICATION_START_OR_CANCEL_SICK_NAME;
                text = NOTIFICATION_START_OR_CANCEL_SICK_TEXT;
                this.getParameters().put(ExternalEventNotificationService.EMP_DATE,
                        findValueInParams(new String[]{ExternalEventNotificationService.EMP_DATE}, values));
                this.getParameters().put(ExternalEventNotificationService.EMP_TIME_H,
                        findValueInParams(new String[]{ExternalEventNotificationService.EMP_TIME_H}, values));
                break;
        }
    }

    @Override
    public void parse(Client client) {
        parseClientSimpleInfo(client);
    }

    @Override
    public void parse(Client child, Client guardian) {
        parseChildAndGuardianInfo(child, guardian);
    }
}