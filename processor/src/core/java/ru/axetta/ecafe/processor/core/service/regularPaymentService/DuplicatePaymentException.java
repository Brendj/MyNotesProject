/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.regularPaymentService;

/**
 * User: Shamil
 * Date: 16.12.14
 */
public class DuplicatePaymentException extends Exception {
    public DuplicatePaymentException() {
        super("Данный платеж уже зарегистрирован.");
    }
}
