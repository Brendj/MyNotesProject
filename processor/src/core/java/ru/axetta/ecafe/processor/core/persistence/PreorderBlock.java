/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by nuc on 25.11.2019.
 */
public class PreorderBlock {
    private Long idOfPreorderBlock;
    private PreorderComplex preorderComplex;
    private Boolean storno;
    private Date createdDate;
    private Date lastUpdate;
    private Long idOfOrgOnCreate;

    public Long getIdOfPreorderBlock() {
        return idOfPreorderBlock;
    }

    public void setIdOfPreorderBlock(Long idOfPreorderBlock) {
        this.idOfPreorderBlock = idOfPreorderBlock;
    }

    public PreorderComplex getPreorderComplex() {
        return preorderComplex;
    }

    public void setPreorderComplex(PreorderComplex preorderComplex) {
        this.preorderComplex = preorderComplex;
    }

    public Boolean getStorno() {
        return storno;
    }

    public void setStorno(Boolean storno) {
        this.storno = storno;
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

    public Long getIdOfOrgOnCreate() {
        return idOfOrgOnCreate;
    }

    public void setIdOfOrgOnCreate(Long idOfOrgOnCreate) {
        this.idOfOrgOnCreate = idOfOrgOnCreate;
    }
}
