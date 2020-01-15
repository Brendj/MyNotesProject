/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by nuc on 03.09.2019.
 */
public class AtolPacket {
    private Long idOfAtolPacket;
    private ClientPaymentAddon clientPaymentAddon;
    private String request;
    private String response;
    private String atolUUid;
    private Date createdDate;
    private Date lastUpdate;

    public AtolPacket() {

    }

    public AtolPacket(ClientPaymentAddon clientPaymentAddon, String request) {
        this.clientPaymentAddon = clientPaymentAddon;
        this.request = request;
        this.createdDate = new Date();
    }

    public Long getIdOfAtolPacket() {
        return idOfAtolPacket;
    }

    public void setIdOfAtolPacket(Long idOfAtolPacket) {
        this.idOfAtolPacket = idOfAtolPacket;
    }

    public ClientPaymentAddon getClientPaymentAddon() {
        return clientPaymentAddon;
    }

    public void setClientPaymentAddon(ClientPaymentAddon clientPaymentAddon) {
        this.clientPaymentAddon = clientPaymentAddon;
    }

    public String getAtolUUid() {
        return atolUUid;
    }

    public void setAtolUUid(String atolUUid) {
        this.atolUUid = atolUUid;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
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
}
