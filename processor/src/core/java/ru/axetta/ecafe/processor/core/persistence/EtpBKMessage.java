/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by i.semenov on 13.11.2018.
 */
public class EtpBKMessage {
    private Long idOfEtpBKMessage;
    private String message;
    private Date createdDate;
    private Date lastUpdate;
    private Boolean isSent;

    public EtpBKMessage() {

    }

    public Long getIdOfEtpBKMessage() {
        return idOfEtpBKMessage;
    }

    public void setIdOfEtpBKMessage(Long idOfEtpBKMessage) {
        this.idOfEtpBKMessage = idOfEtpBKMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
}
