/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

public class SmartWatch {
    private Long idOfSmartWatch;
    private Long idOfCard;
    private Long idOfClient;
    private Long trackerUid;
    private Long trackerId;
    private String model;
    private String color;

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
}
