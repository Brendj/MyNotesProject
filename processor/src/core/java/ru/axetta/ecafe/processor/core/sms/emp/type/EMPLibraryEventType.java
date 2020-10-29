/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.service.ExternalEventNotificationService;

public class EMPLibraryEventType extends EMPAbstractEventType {

    public static String PLACE_NAME = "orgName";
    public static String DATE = "date";
    public static String TIME = "time";

    public EMPLibraryEventType(String [] values) {
        type = EMPEventTypeFactory.ENTER_LIBRARY;
        String libraryName = findValueInParams(new String[]{ExternalEventNotificationService.PLACE_NAME}, values);
        this.getParameters().put(PLACE_NAME, libraryName);
        String dateEvent = findValueInParams(new String[]{ExternalEventNotificationService.EMP_DATE}, values);
        this.getParameters().put(DATE, dateEvent);
        String timeEvent = findValueInParams(new String[]{ExternalEventNotificationService.EMP_TIME}, values);
        this.getParameters().put(TIME, timeEvent);
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