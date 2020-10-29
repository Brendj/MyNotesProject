/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.persistence.Client;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 08.09.15
 * Time: 13:13
 */

public class EMPPaymentEventType extends EMPAbstractEventType {
    protected static final String NAME = "Списание с л/с";
    protected static final String TEXT = "Списание %date% %time%, л/с: %account%. Буфет: %amountPrice%, Комплекс: %amountLunch%";


    public EMPPaymentEventType() {
        stream = STREAM;
        type = EMPEventTypeFactory.PAYMENT_EVENT;
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