/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.persistence.Client;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 24.03.16
 * Time: 11:04
 * To change this template use File | Settings | File Templates.
 */
public class EMPInfoMailingEventType extends EMPAbstractEventType {
    protected static final String NAME = "Информационное сообщение (рассылка)";

    protected static final String TEXT = "%infoText%";

    public EMPInfoMailingEventType() {
        stream = STREAM;
        type = EMPEventTypeFactory.INFO_MAILING_EVENT;
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
