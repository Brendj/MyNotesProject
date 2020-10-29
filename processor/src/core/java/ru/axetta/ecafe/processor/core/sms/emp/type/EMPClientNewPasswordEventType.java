/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
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
//Этот тип сообщения отправляет клиенту пароль в смс и не должно использоваться.
// Вместо этого реализована схема с кодом активации - сообщение TOKEN_GENERATED_EVENT=901250001
@Deprecated
public class EMPClientNewPasswordEventType extends EMPAbstractEventType {
    protected static final String NAME = "Генерация нового пароля для клиента";

    protected static final String TEXT = "Новый пароль для л/с %account%";

    public EMPClientNewPasswordEventType() {
        stream = STREAM;
        type = EMPEventTypeFactory.CLIENT_NEWPASSWORD_EVENT;
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
