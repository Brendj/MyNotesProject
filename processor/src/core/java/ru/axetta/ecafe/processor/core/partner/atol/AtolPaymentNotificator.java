/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.atol;

import ru.axetta.ecafe.processor.core.payment.IPaymentNotificator;
import ru.axetta.ecafe.processor.core.persistence.ClientPaymentAddon;

import java.util.Date;

/**
 * Created by i.semenov on 26.08.2019.
 */
public class AtolPaymentNotificator implements IPaymentNotificator {
    public final static Integer ATOL_NEW = 0;
    public final static Integer ATOL_SENT = 1;

    public void sendNotification() {

    }

    public void addInitialValue(ClientPaymentAddon clientPaymentAddon) {
        clientPaymentAddon.setAtolStatus(ATOL_NEW);
        clientPaymentAddon.setAtolUpdate(new Date());
    }
}
