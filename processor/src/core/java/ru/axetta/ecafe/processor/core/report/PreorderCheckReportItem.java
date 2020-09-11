/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.PreorderCheck;

import java.util.Date;

public class PreorderCheckReportItem {
    private Date date;
    private Long preorderAmount;
    private Long goodRequestAmount;
    private Date createdDate;
    private Date lastUpdate;
    private Boolean alarm;

    public PreorderCheckReportItem() {

    }

    public PreorderCheckReportItem(PreorderCheck preorderCheck) {
        this.date = preorderCheck.getDate();
        this.preorderAmount = preorderCheck.getPreorderAmount();
        this.goodRequestAmount = preorderCheck.getGoodRequestAmount();
        this.createdDate = preorderCheck.getCreatedDate();
        this.lastUpdate = preorderCheck.getLastUpdate();
        this.alarm = preorderCheck.getAlarm();
    }

    public String getStyle() {
        return alarm ? "alarm-row" : "regular-row";
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
