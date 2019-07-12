/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by i.semenov on 26.06.2017.
 */
public class ExternalEvent {
    private Long idOfExternalEvent;
    private String orgCode;
    private String orgName;
    private String enterName;
    private Client client;
    private Date evtDateTime;
    private ExternalEventType evtType;
    private ExternalEventStatus evtStatus;
    private Long version;
    private Long cardNo;
    private Integer cardType;

    public ExternalEvent() {
        //default constructor
    }

    public ExternalEvent(Client client, String orgCode, String orgName, ExternalEventType evtType,
            Date evtDateTime, ExternalEventStatus evtStatus, Long cardNo, Integer cardType,
            ISetExternalEventVersion handlerVersion) throws IllegalArgumentException {
        this.client = client;
        this.orgCode = orgCode;
        this.orgName = orgName;
        this.evtType = evtType;
        this.evtDateTime = evtDateTime;
        this.evtStatus = evtStatus;
        this.cardNo = cardNo;
        this.cardType = cardType;
        this.version = handlerVersion.getVersion();
        buildEnterName(evtStatus);
    }

    protected void buildEnterName(ExternalEventStatus evtStatus) throws IllegalArgumentException {
        String name = "";
        if (evtType == null) throw new IllegalArgumentException("Неверный тип события");
        if (evtStatus == null) throw new IllegalArgumentException("Неверный статус");
        if (evtType.equals(ExternalEventType.MUSEUM)) {
            if (getEvtStatus().equals(ExternalEventStatus.TICKET_GIVEN)) {
                name = String.format("Вход (%s)", getOrgName());
            } else if (getEvtStatus().equals(ExternalEventStatus.TICKET_BACK)) {
                name = String.format("Возврат билета (%s)", getOrgName());
            }
        }
        if (name.length() > 255) {
            name = name.substring(0, 255);
        }
        setEnterName(name);
    }

    public Long getIdOfExternalEvent() {
        return idOfExternalEvent;
    }

    public void setIdOfExternalEvent(Long idOfExternalEvent) {
        this.idOfExternalEvent = idOfExternalEvent;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getEnterName() {
        return enterName;
    }

    public void setEnterName(String enterName) {
        this.enterName = enterName;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Date getEvtDateTime() {
        return evtDateTime;
    }

    public void setEvtDateTime(Date evtDateTime) {
        this.evtDateTime = evtDateTime;
    }

    public ExternalEventType getEvtType() {
        return evtType;
    }

    public void setEvtType(ExternalEventType evtType) {
        this.evtType = evtType;
    }

    public ExternalEventStatus getEvtStatus() {
        return evtStatus;
    }

    public void setEvtStatus(ExternalEventStatus evtStatus) {
        this.evtStatus = evtStatus;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getCardNo() {
        return cardNo;
    }

    public void setCardNo(Long cardNo) {
        this.cardNo = cardNo;
    }

    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }
}
