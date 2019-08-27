/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.payment;

import ru.axetta.ecafe.processor.core.persistence.ClientPaymentAddon;

/**
 * Created by i.semenov on 26.08.2019.
 */
public interface IPaymentNotificator {

    public void sendNotification();

    public void addInitialValue(ClientPaymentAddon clientPaymentAddon);

}
