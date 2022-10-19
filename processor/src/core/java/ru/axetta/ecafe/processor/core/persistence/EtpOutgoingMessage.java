/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by nuc on 02.11.2018.
 */
public class EtpOutgoingMessage {
    private Long idOfEtpOutgoingMessage;
    private String etpMessageId;
    private String etpMessagePayload;
    private Date createdDate;
    private Date lastUpdate;
    private Boolean isSent;
    private String errorMessage;
    private Integer messageType;

    public EtpOutgoingMessage() {

    }

    public String getEtpMessageId() {
        return etpMessageId;
    }

    public void setEtpMessageId(String etpMessageId) {
        this.etpMessageId = etpMessageId;
    }

    public String getEtpMessagePayload() {
        return etpMessagePayload;
    }

    public void setEtpMessagePayload(String etpMessagePayload) {
        this.etpMessagePayload = etpMessagePayload;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Boolean getIsSent() {
        return isSent;
    }

    public void setIsSent(Boolean sent) {
        isSent = sent;
    }

    public Long getIdOfEtpOutgoingMessage() {
        return idOfEtpOutgoingMessage;
    }

    public void setIdOfEtpOutgoingMessage(Long idOfEtpOutgoingMessage) {
        this.idOfEtpOutgoingMessage = idOfEtpOutgoingMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }
}
