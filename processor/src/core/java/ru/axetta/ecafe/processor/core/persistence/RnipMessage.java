/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by i.semenov on 30.05.2019.
 */
public class RnipMessage {
    private Long idOfRnipMessage;
    private long version;
    private Date eventTime;
    private RnipEventType eventType;
    private String request;
    private String response;
    private String requestId;
    private String responseMessage;
    private Contragent contragent;
    private Date lastRnipUpdate;
    private Boolean processed;
    private Date lastUpdate;

    public RnipMessage() {

    }

    public RnipMessage(Contragent contragent, RnipEventType eventType, String requestId) {
        processed = false;
        eventTime = new Date();
        lastUpdate = new Date();
        this.contragent = contragent;
        this.eventType = eventType;
        this.requestId = requestId;
    }

    public Long getIdOfRnipMessage() {
        return idOfRnipMessage;
    }

    public void setIdOfRnipMessage(Long idOfRnipMessage) {
        this.idOfRnipMessage = idOfRnipMessage;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public RnipEventType getEventType() {
        return eventType;
    }

    public void setEventType(RnipEventType eventType) {
        this.eventType = eventType;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Contragent getContragent() {
        return contragent;
    }

    public void setContragent(Contragent contragent) {
        this.contragent = contragent;
    }

    public Date getLastRnipUpdate() {
        return lastRnipUpdate;
    }

    public void setLastRnipUpdate(Date lastRnipUpdate) {
        this.lastRnipUpdate = lastRnipUpdate;
    }

    public Boolean getProcessed() {
        return processed;
    }

    public void setProcessed(Boolean processed) {
        this.processed = processed;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
