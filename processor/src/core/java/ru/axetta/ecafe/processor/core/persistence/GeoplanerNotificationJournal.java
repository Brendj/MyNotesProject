/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.sync.handlers.payment.registry.Payment;

import java.util.Date;

public class GeoplanerNotificationJournal {
    private Long idOfNotification;
    private Client client;
    private Org org;
    private Long idOfEnterEvents;
    private Long idOfOrder;
    private Long IdOfClientPayment;
    private Integer eventType;
    private Integer response;
    private Boolean isSend = false;
    private Date createDate;
    private String errorText;

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
        return IdOfClientPayment;
    }

    public void setIdOfClientPayment(Long idOfClientPayment) {
        IdOfClientPayment = idOfClientPayment;
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

    public static class Builder {
        public static GeoplanerNotificationJournal build(Client client, EnterEvent enterEvent){
            GeoplanerNotificationJournal journal = new GeoplanerNotificationJournal();
            journal.setClient(client);
            journal.setOrg(enterEvent.getOrg());
            journal.setIdOfEnterEvents(enterEvent.getCompositeIdOfEnterEvent().getIdOfEnterEvent());
            journal.setCreateDate(new Date());
            journal.setEventType(EventType.ENTER_EVENTS.ordinal());
            return journal;
        }

        public static GeoplanerNotificationJournal build(Client client, Payment payment, Org org){
            GeoplanerNotificationJournal journal = new GeoplanerNotificationJournal();
            journal.setClient(client);
            journal.setOrg(org);
            journal.setIdOfOrder(payment.getIdOfOrder());
            journal.setCreateDate(new Date());
            journal.setEventType(EventType.PURCHASES.ordinal());
            return journal;
        }

        public static GeoplanerNotificationJournal build(Client client, ClientPayment clientPayment){
            GeoplanerNotificationJournal journal = new GeoplanerNotificationJournal();
            journal.setClient(client);
            journal.setOrg(client.getOrg());
            journal.setIdOfClientPayment(clientPayment.getIdOfClientPayment());
            journal.setCreateDate(new Date());
            journal.setEventType(EventType.PAYMENTS.ordinal());
            return journal;
        }
    }

    public enum EventType {
        ENTER_EVENTS, PURCHASES, PAYMENTS;
    }
}
