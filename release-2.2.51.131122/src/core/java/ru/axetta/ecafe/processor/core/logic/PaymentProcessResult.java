/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 22.11.13
 * Time: 15:27
 * To change this template use File | Settings | File Templates.
 */
public enum  PaymentProcessResult {
    OK(0, "Ok"),
    UNKNOWN_ERROR(100, "Unknown error"),
    CLIENT_NOT_FOUND(105, "Client not found"),
    SUB_BALANCE_NOT_FOUND(106, "Client sub balance not found"),
    CARD_NOT_FOUND(120, "Card acceptable for transfer not found"),
    CONTRAGENT_NOT_FOUND(130, "Contragent not found"),
    PAYMENT_ALREADY_REGISTERED(140, "Payment is already registered"),
    TSP_CONTRAGENT_IS_PROHIBITED(150, "Merchant (TSP) contragent is prohibited for this client"),
    PAYMENT_NOT_FOUND(300, "Payment not found"),;

    private final int code;
    private final String description;

    private PaymentProcessResult(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
