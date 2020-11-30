/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.persistence.Client;

import java.util.Map;

/**
 * Created by i.semenov on 19.02.2018.
 */
public class EMPEnterWithCheckerEventType extends EMPAbstractEventType {
    protected static final String NAME = "Вход в школу, отметка сотрудником ОО";
    protected static final String TEXT = "Вашему ребенку (%surname% %name%, лицевой счет: %account%) отмечен вход в школу работником ОО в %time%";

    public EMPEnterWithCheckerEventType() {
        stream = STREAM;
        type = EMPEventTypeFactory.ENTER_WITH_CHECKER;
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
