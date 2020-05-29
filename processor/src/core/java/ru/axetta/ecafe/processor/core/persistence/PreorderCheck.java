/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

public class PreorderCheck {
    private Long idOfPreorderCheck;
    private Date date;
    private Long preorderAmount;
    private Long goodRequestAmount;
    private Date createdDate;
    private Date lastUpdate;
    private Boolean alarm;

    public PreorderCheck() {
    }

    public PreorderCheck(Date date, Long preorderAmount, Long goodRequestAmount, boolean alarm) {
        this.date = date;
        this.preorderAmount = preorderAmount;
        this.goodRequestAmount = goodRequestAmount;
        this.createdDate = new Date();
        this.lastUpdate = new Date();
        this.alarm = alarm;
    }

    public Long getIdOfPreorderCheck() {
        return idOfPreorderCheck;
    }

    public void setIdOfPreorderCheck(Long idOfPreorderCheck) {
        this.idOfPreorderCheck = idOfPreorderCheck;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getPreorderAmount() {
        return preorderAmount;
    }

    public void setPreorderAmount(Long preorderAmount) {
        this.preorderAmount = preorderAmount;
    }

    public Long getGoodRequestAmount() {
        return goodRequestAmount;
    }

    public void setGoodRequestAmount(Long goodRequestAmount) {
        this.goodRequestAmount = goodRequestAmount;
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

    public Boolean getAlarm() {
        return alarm;
    }

    public void setAlarm(Boolean alarm) {
        this.alarm = alarm;
    }
}
