/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.payment;

import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.persistence.ClientPaymentAddon;

/**
 * Created by i.semenov on 26.08.2019.
 */
public interface IPaymentNotificator {

    public boolean isToSave(ClientPayment clientPayment);

    public void sendNotifications();

    public void addNotificatorValues(ClientPaymentAddon clientPaymentAddon, ClientPayment clientPayment);

}
