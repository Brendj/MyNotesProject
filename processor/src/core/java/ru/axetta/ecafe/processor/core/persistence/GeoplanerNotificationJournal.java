/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

public class GeoplanerNotificationJournal {
    private Long idOfNotification;
    private Client client;
    private Org org;
    private Long idOfEnterEvents;
    private Long idOfOrder;
    private Long idOfClientPayment;
    private Integer eventType;
    private Integer response;
    private Boolean isSend = false;
    private Date createDate;
    private String errorText;
    private String nodeName;
    private SmartWatchVendor vendor;

    public GeoplanerNotificationJournal() {
        // for hibernate
    }

    public Long getIdOfNotification() {
        return idOfNotification;
    }

    public void setIdOfNotification(Long idOfNotification) {
        this.idOfNotification = idOfNotification;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public Long getIdOfEnterEvents() {
        return idOfEnterEvents;
    }

    public void setIdOfEnterEvents(Long idOfEnterEvents) {
        this.idOfEnterEvents = idOfEnterEvents;
    }

    public Long getIdOfOrder() {
        return idOfOrder;
    }

    public void setIdOfOrder(Long idOfOrder) {
        this.idOfOrder = idOfOrder;
    }

    public Long getIdOfClientPayment() {
        return idOfClientPayment;
    }

    public void setIdOfClientPayment(Long idOfClientPayment) {
        this.idOfClientPayment = idOfClientPayment;
    }

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    public Integer getResponse() {
        return response;
    }

    public void setResponse(Integer response) {
        this.response = response;
    }

    public Boolean getIsSend() {
        return isSend;
    }

    public void setIsSend(Boolean send) {
        isSend = send;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public SmartWatchVendor getVendor() {
        return vendor;
    }

    public void setVendor(SmartWatchVendor vendor) {
        this.vendor = vendor;
    }

    public static class Builder {
        public static GeoplanerNotificationJournal build(String errorText, Integer responseCode, Boolean isSend,
                Client client, Org org, Integer eventType, Long idOfEnterEvents, Long idOfOrder,
                Long idOfClientPayment, String nodeName, SmartWatchVendor vendor){
            GeoplanerNotificationJournal journal = new GeoplanerNotificationJournal();
            journal.setClient(client);
            journal.setOrg(org);
            journal.setIdOfEnterEvents(idOfEnterEvents);
            journal.setIdOfOrder(idOfOrder);
            journal.setIdOfClientPayment(idOfClientPayment);
            journal.setCreateDate(new Date());
            journal.setErrorText(errorText);
            journal.setIsSend(isSend);
            journal.setResponse(responseCode);
            journal.setEventType(eventType);
            journal.setNodeName(nodeName);
            journal.setVendor(vendor);

            return journal;
        }
    }
}
