/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch;

public class JsonSmartWatchInfo {
    private Long trackerId;
    private Long trackerUid;
    private String state;
    private String lifeState;

    public Long getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(Long trackerId) {
        this.trackerId = trackerId;
    }

    public Long getTrackerUid() {
        return trackerUid;
    }

    public void setTrackerUid(Long trackerUid) {
        this.trackerUid = trackerUid;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLifeState() {
        return lifeState;
    }

    public void setLifeState(String lifeState) {
        this.lifeState = lifeState;
    }
}
