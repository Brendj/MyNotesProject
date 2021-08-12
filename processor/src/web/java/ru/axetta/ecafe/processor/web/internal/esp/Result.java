/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.esp;

import java.util.Date;

public class Result {
    private String errorCode;
    private String errorMessage;

    public Result(String errorCode, String errorMessage, Date serverTimestamp) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public Result() {
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
