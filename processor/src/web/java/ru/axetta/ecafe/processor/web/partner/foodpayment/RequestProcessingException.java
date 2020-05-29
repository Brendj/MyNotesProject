/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment;


public class RequestProcessingException extends Exception {
    private int errorCode;
    private String errorMessage;
    public RequestProcessingException(int errorCode, String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
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
