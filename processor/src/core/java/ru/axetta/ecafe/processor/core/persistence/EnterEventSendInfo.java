/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class EnterEventSendInfo {
    private CompositeIdOfEnterEventSendInfo compositeIdOfEnterEventSendInfo;
    private EnterEvent enterEvent;
    private Org org;
    private Client client;
    private Card card;
    private Date evtDateTime;
    private Boolean sendToExternal;
    private Boolean responseCode;
    private Boolean directionType;

    public static final List<Integer> VALID_ENTER_CODES = Arrays.asList(
            EnterEvent.ENTRY,                   // вход
            EnterEvent.RE_ENTRY,                // повторный вход
            EnterEvent.TURNSTILE_IS_BROKEN,     // взлом турникета
            EnterEvent.DETECTED_INSIDE,         // обнаружен на подносе карты внутри здания
            EnterEvent.CHECKED_BY_TEACHER_EXT,  //отмечен в классном журнале через внешнюю систему
            EnterEvent.CHECKED_BY_TEACHER_INT );//отмечен учителем внутри здания

    public static final List<Integer> VALID_EXIT_CODES = Arrays.asList(
            EnterEvent.EXIT,
            EnterEvent.RE_EXIT
    );

    public boolean isEnterPassDirection(Integer direction){
        return VALID_ENTER_CODES.contains(direction);
    }

    public EnterEvent getEnterEvent() {
        return enterEvent;
    }

    public void setEnterEvent(EnterEvent enterEvent) {
        this.enterEvent = enterEvent;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Date getEvtDateTime() {
        return evtDateTime;
    }

    public void setEvtDateTime(Date evtDateTime) {
        this.evtDateTime = evtDateTime;
    }

    public Boolean getSendToExternal() {
        return sendToExternal;
    }

    public void setSendToExternal(Boolean sendToExternal) {
        this.sendToExternal = sendToExternal;
    }

    public Boolean getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Boolean responseCode) {
        this.responseCode = responseCode;
    }

    public Boolean getDirectionType() {
        return directionType;
    }

    public void setDirectionType(Boolean directionType) {
        this.directionType = directionType;
    }

    public void setDirectionType(Integer direction) {
        this.directionType = isEnterPassDirection(direction);
    }

    public CompositeIdOfEnterEventSendInfo getCompositeIdOfEnterEventSendInfo() {
        return compositeIdOfEnterEventSendInfo;
    }

    public void setCompositeIdOfEnterEventSendInfo(CompositeIdOfEnterEventSendInfo compositeIdOfEnterEventSendInfo) {
        this.compositeIdOfEnterEventSendInfo = compositeIdOfEnterEventSendInfo;
    }
}
