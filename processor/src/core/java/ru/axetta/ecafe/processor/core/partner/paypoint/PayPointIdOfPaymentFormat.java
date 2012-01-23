/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.paypoint;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Converter
 * PayPoint operationId to ECafe IdOfPaymentFormat
 */
public class PayPointIdOfPaymentFormat {

    private NumberFormat decimalFormat;

    public PayPointIdOfPaymentFormat() {
        decimalFormat = new DecimalFormat("0000000000000000000");
    }

    public String format(long operationId) {
        return decimalFormat.format(operationId);
    }


}