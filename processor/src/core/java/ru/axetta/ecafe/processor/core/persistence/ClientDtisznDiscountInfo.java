/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

public class ClientDtisznDiscountInfo {
    private Long idOfClientDTISZNDiscountInfo;
    private Client client;
    private Integer dtisznCode;
    private String dtisznDescription;
    private ClientDTISZNDiscountStatus status;
    private Date dateStart;
    private Date dateEnd;
    private Date createdDate;

    public ClientDtisznDiscountInfo() {

    }

    public Long getIdOfClientDTISZNDiscountInfo() {
        return idOfClientDTISZNDiscountInfo;
    }

    public void setIdOfClientDTISZNDiscountInfo(Long idOfClientDTISZNDiscountInfo) {
        this.idOfClientDTISZNDiscountInfo = idOfClientDTISZNDiscountInfo;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Integer getDtisznCode() {
        return dtisznCode;
    }

    public void setDtisznCode(Integer dtisznCode) {
        this.dtisznCode = dtisznCode;
    }

    public String getDtisznDescription() {
        return dtisznDescription;
    }

    public void setDtisznDescription(String dtisznDescription) {
        this.dtisznDescription = dtisznDescription;
    }

    public ClientDTISZNDiscountStatus getStatus() {
        return status;
    }

    public void setStatus(ClientDTISZNDiscountStatus status) {
        this.status = status;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
