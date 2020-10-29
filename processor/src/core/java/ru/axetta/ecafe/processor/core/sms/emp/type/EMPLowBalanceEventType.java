/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.persistence.Client;

import java.util.Map;

public class EMPLowBalanceEventType extends EMPAbstractEventType {
    protected static final String NAME = "Баланс ниже установленного порога";
    protected static final String TEXT = "%surname% %name% (л/с: %account%): баланс ниже %balanceToNotify% руб.";


    public EMPLowBalanceEventType() {
        stream = STREAM;
        type = EMPEventTypeFactory.LOW_BALANCE_EVENT;
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