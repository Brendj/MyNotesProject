/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.util;


public class RequestProcessingException extends Exception {
    private int errorCode;
    private String errorMessage;
    public RequestProcessingException(int errorCode, String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public RequestProcessingException(GroupManagementErrors error) {
        this.errorCode = error.getErrorCode();
        this.errorMessage = error.getErrorMessage();
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString(){
        return new StringBuilder(errorCode + ": "+errorMessage).toString();
    }
}