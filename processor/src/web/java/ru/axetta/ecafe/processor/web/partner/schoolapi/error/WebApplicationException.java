/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.error;

public class WebApplicationException extends RuntimeException {
    private Integer errorCode = 500;
    private Integer rawHttpStatus = 500;

    public WebApplicationException(String message){
        super(message);
    }

    public WebApplicationException(String message, Throwable cause){
        super(message, cause);
    }

    public WebApplicationException(int code, String message, int rawHttpStatus){
        super(message);
        this.errorCode = code;
        this.rawHttpStatus = rawHttpStatus;
    }

    public WebApplicationException(int code, String message){
        super(message);
        this.errorCode = code;
    }

    public WebApplicationException(int errorCode, String errorMessage, int rawHttpStatus, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.rawHttpStatus = rawHttpStatus;
    }

    public static WebApplicationException badRequest(int code, String message){
        return new WebApplicationException(code, message, 400);
    }

    public static WebApplicationException internalServerError(int code, String message) {
        return new WebApplicationException(code, message, 500);
    }

    public static WebApplicationException notFound(int code, String message) {
        return new WebApplicationException(code, message, 404);
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public Integer getRawHttpStatus() {
        return rawHttpStatus;
    }

    public String getErrorMessage() {
        return this.getMessage();
    }

    @Override
    public String toString() {
        return String.format("%s: %s", errorCode, getMessage());
    }
}
