/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.regularPaymentService;

/**
 * User: Shamil
 * Date: 30.01.15
 * Time: 18:50
 */
public class BadSumException extends Exception {
    public BadSumException() {
        super("Сумма платежа равна нулю.");
    }
}
