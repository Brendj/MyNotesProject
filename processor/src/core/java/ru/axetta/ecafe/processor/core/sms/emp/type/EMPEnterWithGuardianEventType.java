/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.persistence.Client;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 26.08.14
 * Time: 18:46
 * To change this template use File | Settings | File Templates.
 */
public class EMPEnterWithGuardianEventType extends EMPAbstractEventType {
    protected static final String NAME = "Вход в школу с представителем";
    protected static final String TEXT = "Вход %time% %surname% %name% (%guardian_surname% %guardian_name%)";

    public EMPEnterWithGuardianEventType() {
        stream = STREAM;
        type = EMPEventTypeFactory.ENTER_WITH_GUARDIAN_EVENT;
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