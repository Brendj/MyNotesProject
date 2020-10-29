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
 * Time: 18:44
 * To change this template use File | Settings | File Templates.
 */
public class EMPLeaveEventType extends EMPAbstractEventType {
    protected static final String NAME = "Выход из школы";
    protected static final String TEXT = "Выход из школы %time% (%account% %surname% %name%) Баланс: %balance%";

    public EMPLeaveEventType() {
        stream = STREAM;
        type = EMPEventTypeFactory.LEAVE_EVENT;
        name = NAME;
        text = TEXT;
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