/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.rbkmoney;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 01.12.2009
 * Time: 14:36:37
 * To change this template use File | Settings | File Templates.
 */
public class PaymentMethodConverter {

    private PaymentMethodConverter() {

    }

    public static String getPaymentMethodCodeName(int payType) throws IllegalArgumentException {
        int i = Arrays.binarySearch(RBKConstants.SUPPORTED_PAYMENT_METHODS, payType);
        if (i < 0) {
            throw new IllegalArgumentException();
        }
        return RBKConstants.PAYMENT_METHOD_CODE_NAMES[i];
    }

    public static String getPaymentMethodRateName(int payType) throws IllegalArgumentException {
        int i = Arrays.binarySearch(RBKConstants.SUPPORTED_PAYMENT_METHODS, payType);
        if (i < 0) {
            throw new IllegalArgumentException();
        }
        return RBKConstants.PAYMENT_METHOD_CODE_NAMES[i];
    }

}
