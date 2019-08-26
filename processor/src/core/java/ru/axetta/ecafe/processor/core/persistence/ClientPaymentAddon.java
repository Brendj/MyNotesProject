/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by i.semenov on 26.08.2019.
 */
public class ClientPaymentAddon {
    private Long idOfClientPaymentAddon;
    private ClientPayment clientPayment;
    private Date createdDate;
    private Integer atolStatus;
    private Date atolUpdate;

    public ClientPaymentAddon() {

    }

    public ClientPaymentAddon(ClientPayment clientPayment) {
        this.clientPayment = clientPayment;
        this.createdDate = new Date();
    }

    public Long getIdOfClientPaymentAddon() {
        return idOfClientPaymentAddon;
    }

    public void setIdOfClientPaymentAddon(Long idOfClientPaymentAddon) {
        this.idOfClientPaymentAddon = idOfClientPaymentAddon;
    }

    public ClientPayment getClientPayment() {
        return clientPayment;
    }

    public void setClientPayment(ClientPayment clientPayment) {
        this.clientPayment = clientPayment;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getAtolStatus() {
        return atolStatus;
    }

    public void setAtolStatus(Integer atolStatus) {
        this.atolStatus = atolStatus;
    }

    public Date getAtolUpdate() {
        return atolUpdate;
    }

    public void setAtolUpdate(Date atolUpdate) {
        this.atolUpdate = atolUpdate;
    }
}
