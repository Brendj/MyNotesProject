/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

public class SmartWatch {
    private Long idOfSmartWatch;
    private Long idOfCard;
    private Long idOfClient;
    private Long trackerUid;
    private Long trackerId;
    private Date trackerActivateTime;
    private Long trackerActivateUserId;
    private String status;
    private String simIccid;
    private String model;
    private String color;
    private String vendor;

    public Long getIdOfSmartWatch() {
        return idOfSmartWatch;
    }

    public void setIdOfSmartWatch(Long idOfSmartWatch) {
        this.idOfSmartWatch = idOfSmartWatch;
    }

    public Long getIdOfCard() {
        return idOfCard;
    }

    public void setIdOfCard(Long idOfCard) {
        this.idOfCard = idOfCard;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getTrackerUid() {
        return trackerUid;
    }

    public void setTrackerUid(Long trackerUid) {
        this.trackerUid = trackerUid;
    }

    public Long getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(Long trackerId) {
        this.trackerId = trackerId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Date getTrackerActivateTime() {
        return trackerActivateTime;
    }

    public void setTrackerActivateTime(Date trackerActivateTime) {
        this.trackerActivateTime = trackerActivateTime;
    }

    public Long getTrackerActivateUserId() {
        return trackerActivateUserId;
    }

    public void setTrackerActivateUserId(Long rackerActivateUserId) {
        this.trackerActivateUserId = rackerActivateUserId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSimIccid() {
        return simIccid;
    }

    public void setSimIccid(String simIccid) {
        this.simIccid = simIccid;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }
}
