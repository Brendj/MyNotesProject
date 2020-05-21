/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment;

import java.util.Date;

public class Result {
    private int errorCode;
    private String errorMessage;

    public Result(int errorCode, String errorMessage, Date serverTimestamp) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public Result() {
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
