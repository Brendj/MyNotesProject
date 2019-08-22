/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.notificator;

import ru.axetta.ecafe.processor.core.persistence.ClientPayment;

/**
 * Created by nuc on 22.08.2019.
 */
public interface IPaymentNotificator {
    public void notifyPartner(ClientPayment clientPayment);
}
