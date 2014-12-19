/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.regularPaymentService.kzn_bankkazani;

/**
 * User: Shamil
 * Date: 16.12.14
 */
public class NotFoundPaymentException extends Exception {
    public NotFoundPaymentException() {
        super("Данный платеж не найден.");
    }
}
