/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.contragent;

import ru.axetta.ecafe.processor.core.persistence.RnipEventType;

import java.util.Date;

/**
 * Created by i.semenov on 24.06.2019.
 */
public class ContragentRnipLogItem {
    private Date eventTime;
    private String eventType;
    private String messageId;
    private Date startDate;
    private Date endDate;
    private String responseMessage;
    private String succeeded;

    public ContragentRnipLogItem(Date eventTime, RnipEventType eventType, String messageId, Date startDate,
            Date endDate, String responseMessage, Boolean succeeded) {
        this.eventTime = eventTime;
        this.eventType = eventType.getDescription();
        this.messageId = messageId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.responseMessage = responseMessage;
        this.succeeded = succeeded ? "Да" : "Нет";
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getSucceeded() {
        return succeeded;
    }

    public void setSucceeded(String succeeded) {
        this.succeeded = succeeded;
    }
}
