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

    public ExternalEvent() {
        //default constructor
    }

    public ExternalEvent(Client client, String orgCode, String orgName, ExternalEventType evtType, Date evtDateTime) {
        this.client = client;
        this.orgCode = orgCode;
        this.orgName = orgName;
        this.evtType = evtType;
        this.evtDateTime = evtDateTime;
        buildEnterName();
    }

    private void buildEnterName() {
        if (evtType.equals(ExternalEventType.MUSEUM)) {
            setEnterName("Посещение музея " + orgName);
        }
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
}
