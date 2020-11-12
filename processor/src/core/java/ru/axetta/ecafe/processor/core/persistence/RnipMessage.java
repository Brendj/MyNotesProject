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
    private String messageId;
    private String responseMessageId;
    private Integer paging;
    private String responseMessage;
    private Contragent contragent;
    private Date startDate;
    private Date endDate;
    private Boolean processed;
    private Boolean ackSent;
    private Boolean succeeded;
    private Date lastUpdate;

    public RnipMessage() {

    }

    public RnipMessage(Contragent contragent, RnipEventType eventType, String request,
            String messageId, Date startDate, Date endDate, int paging) {
        processed = false;
        eventTime = new Date();
        lastUpdate = new Date();
        this.contragent = contragent;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventType = eventType;
        this.request = request;
        this.messageId = messageId;
        this.ackSent = false;
        this.succeeded = false;
        this.paging = paging;
    }

    @Override
    public int hashCode() {
        return idOfRnipMessage.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RnipMessage)) {
            return false;
        }
        final RnipMessage that = (RnipMessage) o;
        return idOfRnipMessage.equals(that.getIdOfRnipMessage());
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

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
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

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date lastRnipUpdate) {
        this.endDate = lastRnipUpdate;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Integer getPaging() {
        return paging;
    }

    public void setPaging(Integer paging) {
        this.paging = paging;
    }

    public Boolean getAckSent() {
        return ackSent;
    }

    public void setAckSent(Boolean ackSent) {
        this.ackSent = ackSent;
    }

    public Boolean getSucceeded() {
        return succeeded;
    }

    public void setSucceeded(Boolean succeeded) {
        this.succeeded = succeeded;
    }

    public String getResponseMessageId() {
        return responseMessageId;
    }

    public void setResponseMessageId(String responseMessageId) {
        this.responseMessageId = responseMessageId;
    }
}
