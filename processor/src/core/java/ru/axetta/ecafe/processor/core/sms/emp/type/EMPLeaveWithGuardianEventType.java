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
 * Time: 18:50
 * To change this template use File | Settings | File Templates.
 */
public class EMPLeaveWithGuardianEventType extends EMPAbstractEventType {
    protected static final String NAME = "Выход из школы с представителем";
    protected static final String TEXT = "Выход из школы %time% %surname% %name% (%guardian_surname% %guardian_name%)";
    public static final String GUARDIAN_SURNAME_PARAM = "guardian_surname";
    public static final String GUARDIAN_NAME_PARAM = "guardian_name";

    public EMPLeaveWithGuardianEventType() {
        stream = STREAM;
        type = EMPEventTypeFactory.LEAVE_WITH_GUARDIAN_EVENT;
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
