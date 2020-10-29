/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.persistence.Client;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 23.03.16
 * Time: 10:28
 * To change this template use File | Settings | File Templates.
 */
public class EMPRegularPaymentExpirationEventType extends EMPAbstractEventType {
    protected static final String NAME = "Уведомление об окончании срока действия автоплатежа";

    protected static final String TEXT = "Заканчивается период действия подключенного автоплатежа "
            + "(%account% %surname% %name%) Баланс: %balance%";

    public EMPRegularPaymentExpirationEventType() {
        stream = STREAM;
        type = EMPEventTypeFactory.REGULAR_PAYMENT_EXPIRATION_EVENT;
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
