/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.service.ExternalEventNotificationService;

public class EMPCancelPreorderEventType extends EMPAbstractEventType {

    public EMPCancelPreorderEventType(String [] values) {
        stream = STREAM;
        type = EMPEventTypeFactory.CANCEL_PREORDER;
        this.saveStringtoMap(values);
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