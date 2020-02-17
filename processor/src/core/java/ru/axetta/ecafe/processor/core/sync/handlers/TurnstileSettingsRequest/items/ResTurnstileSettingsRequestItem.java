/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.TurnstileSettingsRequest.items;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

public abstract class ResTurnstileSettingsRequestItem implements AbstractToElement {

    private String errorMessage;
    private Integer resCode;

    public ResTurnstileSettingsRequestItem() {

    }

    public ResTurnstileSettingsRequestItem(Integer resCode) {
        this.resCode = resCode;
    }

    public ResTurnstileSettingsRequestItem(String errorMessage, Integer resCode) {
        this.errorMessage = errorMessage;
        this.resCode = resCode;
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
}
