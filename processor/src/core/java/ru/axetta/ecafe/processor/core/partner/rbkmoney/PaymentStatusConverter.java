/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.rbkmoney;

import ru.axetta.ecafe.processor.core.persistence.ClientPaymentOrder;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 01.12.2009
 * Time: 14:36:37
 * To change this template use File | Settings | File Templates.
 */
public class PaymentStatusConverter {

    private PaymentStatusConverter() {

    }

    public static int paymentStatusToOrderStatus(int paymentStatus) throws IllegalArgumentException {
        if (3 == paymentStatus) {
            return ClientPaymentOrder.ORDER_STATUS_TRANSFER_ACCEPTED;
        }
        if (5 == paymentStatus) {
            return ClientPaymentOrder.ORDER_STATUS_TRANSFER_COMPLETED;
        }
        throw new IllegalArgumentException();
    }

}