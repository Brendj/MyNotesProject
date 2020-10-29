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
 * Time: 19:03
 * To change this template use File | Settings | File Templates.
 */
public class EMPFillEventType extends EMPAbstractEventType {
    protected static final String NAME = "Зачисление на л/с";
    protected static final String TEXT = "Зачислено %amount%; баланс %balance% (%account% %surname% %name%)";

    public EMPFillEventType() {
        stream = STREAM;
        type = EMPEventTypeFactory.FILL_EVENT;
        name = NAME;
        text = TEXT;
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
