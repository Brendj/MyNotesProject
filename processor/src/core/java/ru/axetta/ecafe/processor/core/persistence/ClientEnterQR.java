/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by i.semenov on 27.09.2017.
 */
public class ClientEnterQR {
    private Long idQRCode;
    private Client client;
    private byte[] qr;
    private Date startDate;
    private Date endDate;
    private Date createDate;



    public ClientEnterQR() {

    }

    public ClientEnterQR(Client client, byte[] qr, Date startDate, Date endDate, Date createDate) {
        this.client = client;
        this.qr = qr;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createDate = createDate;
    }


    public Long getIdQRCode() {
        return idQRCode;
    }

    public void setIdQRCode(Long idQRCode) {
        this.idQRCode = idQRCode;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public byte[] getQr() {
        return qr;
    }

    public void setQr(byte[] qr) {
        this.qr = qr;
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
