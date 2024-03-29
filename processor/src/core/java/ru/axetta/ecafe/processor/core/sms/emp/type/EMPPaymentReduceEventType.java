/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.persistence.Client;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 11.03.16
 * Time: 18:18
 * To change this template use File | Settings | File Templates.
 */
public class EMPPaymentReduceEventType extends EMPAbstractEventType {
    protected static final String NAME = "Льготное питание";
    protected static final String TEXT = "???";//"Списание %date% %time%, л/с: %account%. Буфет: %amountPrice%, Комплекс: %amountLunch%";


    public EMPPaymentReduceEventType() {
        stream = STREAM;
        type = EMPEventTypeFactory.PAYMENT_REDUCED_EVENT;
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