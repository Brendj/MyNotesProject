/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.service.ExternalEventNotificationService;

import java.util.Map;

public class EMPNoEnterMuseumEventType extends EMPAbstractEventType {
    protected static final String NAME = "Возврат билета в музей";
    protected static final String TEXT = "%surname% %name% (л/с: %account%): возврат билета в музей %event_place_code%";


    public EMPNoEnterMuseumEventType(String[] values) {
        stream = STREAM;
        type = EMPEventTypeFactory.NOENTER_MUSEUM_EVENT;
        name = NAME;
        text = TEXT;
        String eventPlaceCode = findValueInParams(new String[]{ExternalEventNotificationService.PLACE_CODE}, values);
        this.getParameters().put(ExternalEventNotificationService.PLACE_CODE, eventPlaceCode);
        String eventPlaceName = findValueInParams(new String[]{ExternalEventNotificationService.PLACE_NAME}, values);
        this.getParameters().put(ExternalEventNotificationService.PLACE_NAME, eventPlaceName);
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