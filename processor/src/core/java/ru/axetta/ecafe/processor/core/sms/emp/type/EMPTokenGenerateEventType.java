/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.persistence.Client;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 13.11.14
 * Time: 14:21
 * To change this template use File | Settings | File Templates.
 */
public class EMPTokenGenerateEventType extends EMPAbstractEventType {
    protected static final String NAME = "Код активации";
    protected static final String TEXT = "Код активации: %token%";

    public EMPTokenGenerateEventType() {
        stream = INFORMATION_STREAM;
        type = EMPEventTypeFactory.TOKEN_GENERATED_EVENT;
        name = NAME;
        text = TEXT;
    }

    @Override
    public void parse(Client client) {
        parseClientSimpleInfo(client);

        Map<String, String> params = getParameters();
    }

    @Override
    public void parse(Client child, Client guardian) {
        parseChildAndGuardianInfo(child, guardian);

        Map<String, String> params = getParameters();
    }
}