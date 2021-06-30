/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.error;

public class WebApplicationException extends RuntimeException {
    private Integer errorCode = 500;

    public WebApplicationException(String message){
        super(message);
    }

    public WebApplicationException(String message, Throwable cause){
        super(message, cause);
    }

    public WebApplicationException(int code, String message){
        super(message);
        this.errorCode = code;
    }

    public WebApplicationException(int errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return this.getMessage();
    }

    @Override
    public String toString() {
        return String.format("%s: %s", errorCode, getMessage());
    }
}
