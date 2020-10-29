/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.service.EventNotificationService;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 26.08.14
 * Time: 18:12
 * To change this template use File | Settings | File Templates.
 */
public class EMPEnterEventType extends EMPAbstractEventType {
    protected static final String NAME = "Вход в школу";
    protected static final String TEXT = "Вход в школу %time% (%account% %surname% %name%) Баланс: %balance%";

    public EMPEnterEventType() {
        //previousId = EventNotificationService.NOTIFICATION_ENTER_EVENT;
        stream = STREAM;
        type = EMPEventTypeFactory.ENTER_EVENT;
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
