/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.persistence.Client;

import java.util.Map;

public class EMPEnterCultureEventType extends EMPAbstractEventType {
    protected static final String NAME = "Посещение здания культуры";
    protected static final String TEXT = "%empDate% в %empTime% %surname% %name% зашел в здание культуры по адресу: %address%(%shortnameinfoservice%)";


    public EMPEnterCultureEventType() {
        stream = STREAM;
        type = EMPEventTypeFactory.ENTER_EVENT;
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