/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding.status;

import java.util.Date;

public class PreOrderFeedingStatusItem {
    private Date date;
    private String guid;
    private Integer status;
    private Integer storno;
    private Long version;
    private Boolean deletedState;

    public PreOrderFeedingStatusItem(Date date, String guid, Integer status, Integer storno, Long version,
            Boolean deletedState) {
        this.date = date;
        this.guid = guid;
        this.status = status;
        this.storno = storno;
        this.version = version;
        this.deletedState = deletedState;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStorno() {
        return storno;
    }

    public void setStorno(Integer storno) {
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
}
