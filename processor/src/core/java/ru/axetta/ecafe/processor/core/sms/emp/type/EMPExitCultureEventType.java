/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.service.EventNotificationService;
import ru.axetta.ecafe.processor.core.service.ExternalEventNotificationService;

import java.util.Map;

public class EMPExitCultureEventType extends EMPAbstractEventType {
    protected static final String NAME = "Выход из здания культуры";
    protected static final String TEXT = "%empDate% в %empTimeH% %surname% %name% вышел из здания культуры по адресу: %address%(%shortnameinfoservice%)";


    public EMPExitCultureEventType(String [] values) {
        stream = STREAM;
        type = EMPEventTypeFactory.LEAVE_CULTURE;
        name = NAME;
        text = TEXT;
        String eventAdress = findValueInParams(new String[]{ExternalEventNotificationService.ADDRESS}, values);
        this.getParameters().put(ExternalEventNotificationService.ADDRESS, eventAdress);
        String eventShortName = findValueInParams(new String[]{ExternalEventNotificationService.SHORTNAMEINFOSERVICE}, values);
        this.getParameters().put(ExternalEventNotificationService.SHORTNAMEINFOSERVICE, eventShortName);
        String eventDate = findValueInParams(new String[]{ExternalEventNotificationService.EMP_DATE}, values);
        this.getParameters().put(ExternalEventNotificationService.EMP_DATE, eventDate);
        String eventTime = findValueInParams(new String[]{ExternalEventNotificationService.EMP_TIME_H}, values);
        this.getParameters().put(ExternalEventNotificationService.EMP_TIME_H, eventTime);
    }

    @Override
    public void parse(Client client) {
        parseClientSimpleInfo(client, type);
    }

    @Override
    public void parse(Client child, Client guardian) {
        parseChildAndGuardianInfo(child, guardian, type);
    }
}