/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

public class ClientDtisznDiscountInfo {
    private Long idOfClientDTISZNDiscountInfo;
    private Client client;
    private Long dtisznCode;
    private String dtisznDescription;
    private ClientDTISZNDiscountStatus status;
    private Date dateStart;
    private Date dateEnd;
    private Date createdDate;
    private Long version;
    private Boolean archived;
    private Date lastUpdate;

    public ClientDtisznDiscountInfo(Client client, Long dtisznCode, String dtisznDescription, ClientDTISZNDiscountStatus status,
            Date dateStart, Date dateEnd, Date createdDate, Long version) {
        this.client = client;
        this.dtisznCode = dtisznCode;
        this.dtisznDescription = dtisznDescription;
        this.status = status;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.createdDate = createdDate;
        this.version = version;
        this.archived = false;
        this.lastUpdate = new Date();
    }

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

    public Long getDtisznCode() {
        return dtisznCode;
    }

    public void setDtisznCode(Long dtisznCode) {
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
