/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by nuc on 13.02.2020.
 */
public class PreorderStatus {
    private Long idOfPreorderStatus;
    private Date date;
    private String guid;
    private PreorderStatusType status;
    private Boolean storno;
    private Long version;
    private Boolean deletedState;
    private Date createdDate;
    private Date lastUpdate;
    private Long idOfOrgOnCreate;

    public PreorderStatus() {
        this.createdDate = new Date();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public PreorderStatusType getStatus() {
        return status;
    }

    public void setStatus(PreorderStatusType status) {
        this.status = status;
    }

    public Boolean getStorno() {
        return storno;
    }

    public void setStorno(Boolean storno) {
        this.storno = storno;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
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

    public Long getIdOfPreorderStatus() {
        return idOfPreorderStatus;
    }

    public void setIdOfPreorderStatus(Long idOfPreorderStatus) {
        this.idOfPreorderStatus = idOfPreorderStatus;
    }

    public Long getIdOfOrgOnCreate() {
        return idOfOrgOnCreate;
    }

    public void setIdOfOrgOnCreate(Long idOfOrgOnCreate) {
        this.idOfOrgOnCreate = idOfOrgOnCreate;
    }
}
