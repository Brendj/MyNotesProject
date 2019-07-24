/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.fpsapi.dataflow;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;

public class Result {
    private Long errorCode;
    private String errorMessage;

    @JsonSerialize(using = JsonDateSerializer.class)
    private Date serverTimestamp;

    public Result(Long errorCode, String errorMessage, Date serverTimestamp) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.serverTimestamp = serverTimestamp;
    }

    public Result() {

    }

    public Long getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Long errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Date getServerTimestamp() {
        return serverTimestamp;
    }

    public void setServerTimestamp(Date serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }
}
