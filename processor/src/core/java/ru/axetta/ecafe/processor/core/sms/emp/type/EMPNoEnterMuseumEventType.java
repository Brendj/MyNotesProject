/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.persistence.Client;

import java.util.Map;

public class EMPNoEnterMuseumEventType extends EMPAbstractEventType {
    protected static final String NAME = "Возврат билета в музей";
    protected static final String TEXT = "%surname% %name% (л/с: %account%): возврат билета в музей %event_place_code%";


    public EMPNoEnterMuseumEventType() {
        stream = STREAM;
        type = EMPEventTypeFactory.NOENTER_MUSEUM_EVENT;
        name = NAME;
        text = TEXT;
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