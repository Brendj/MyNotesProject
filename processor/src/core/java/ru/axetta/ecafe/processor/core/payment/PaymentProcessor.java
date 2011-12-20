/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.payment;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 28.07.2009
 * Time: 10:33:22
 * To change this template use File | Settings | File Templates.
 */
public interface PaymentProcessor {

    PaymentResponse processPayRequest(PaymentRequest request) throws Exception;
}