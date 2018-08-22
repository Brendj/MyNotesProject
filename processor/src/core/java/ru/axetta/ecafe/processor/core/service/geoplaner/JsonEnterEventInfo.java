/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.geoplaner;

import java.util.LinkedList;
import java.util.List;

public class JsonEnterEventInfo {
    private Long trackerUid;
    private Long trackerId;

    private List<JsonEnterEventInfoItem> events;

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

    public List<JsonEnterEventInfoItem> getEvents() {
        if(events == null){
            events = new LinkedList<JsonEnterEventInfoItem>();
        }
        return events;
    }

    public void setEvents(List<JsonEnterEventInfoItem> events) {
        this.events = events;
    }
}
