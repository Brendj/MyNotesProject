/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.TurnstileSettingsRequest.items;

public abstract class TurnstileSettingsRequestItem {

    public static final Integer ERROR_CODE_ALL_OK = 0;
    public static final Integer ERROR_CODE_NOT_VALID_ATTRIBUTE = 100;

    private String errorMessage;
    private Integer resCode;
    private String type;

    public TurnstileSettingsRequestItem (String type, String errorMessage) {
        this.type = type;
        this.errorMessage = errorMessage;
        if(errorMessage.equals("")){
            this.setResCode(ERROR_CODE_ALL_OK);
        } else {
            this.setResCode(ERROR_CODE_NOT_VALID_ATTRIBUTE);
        }
    }

    public static Integer getErrorCodeAllOk() {
        return ERROR_CODE_ALL_OK;
    }

    public static Integer getErrorCodeNotValidAttribute() {
        return ERROR_CODE_NOT_VALID_ATTRIBUTE;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getResCode() {
        return resCode;
    }

    public void setResCode(Integer resCode) {
        this.resCode = resCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
