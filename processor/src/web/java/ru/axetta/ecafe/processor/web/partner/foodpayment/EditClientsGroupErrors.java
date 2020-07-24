/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment;

public enum EditClientsGroupErrors {
    OK(0,"OK"),
    CLIENT_NOT_FROM_FRIENDLY_ORG(100,"Клиент не принадлежит дружеским организациям."),

    ;

    private int errorCode;
    private String errorMessage;

    private EditClientsGroupErrors(int errorCode, String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
