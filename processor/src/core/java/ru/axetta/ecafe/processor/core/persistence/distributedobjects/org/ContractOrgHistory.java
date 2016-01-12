/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.org;

import ru.axetta.ecafe.processor.core.persistence.Org;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 29.12.15
 * Time: 12:35
 * To change this template use File | Settings | File Templates.
 */
public class ContractOrgHistory {

    private Long idOfContractOrg;
    private Contract contract;
    private Org org;
    private Long lastVersionOfContract;
    private Date createdDate;
    private Date lastUpdate;
    private Boolean deletedState;
    private Date deleteDate;

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public Long getLastVersionOfContract() {
        return lastVersionOfContract;
    }

    public void setLastVersionOfContract(Long lastVersionOfContract) {
        this.lastVersionOfContract = lastVersionOfContract;
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

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public Date getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }

    public Long getIdOfContractOrg() {
        return idOfContractOrg;
    }

    public void setIdOfContractOrg(Long idOfContractOrg) {
        this.idOfContractOrg = idOfContractOrg;
    }
}
