/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.service.ExternalEventNotificationService;

public class EMPLibraryEventType extends EMPAbstractEventType {

    public EMPLibraryEventType(String [] values) {
        stream = STREAM;
        type = EMPEventTypeFactory.ENTER_LIBRARY;
        String libraryName = findValueInParams(new String[]{ExternalEventNotificationService.ORG_NAME}, values);
        this.getParameters().put(ExternalEventNotificationService.ORG_NAME, libraryName);
        String libraryAddress = findValueInParams(new String[]{ExternalEventNotificationService.ADDRESS}, values);
        this.getParameters().put(ExternalEventNotificationService.ADDRESS, libraryAddress);
        String dateEvent = findValueInParams(new String[]{ExternalEventNotificationService.DATE}, values);
        this.getParameters().put(ExternalEventNotificationService.DATE, dateEvent);
        String timeEvent = findValueInParams(new String[]{ExternalEventNotificationService.TIME}, values);
        this.getParameters().put(ExternalEventNotificationService.TIME, timeEvent);
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