/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by i.semenov on 29.08.2018.
 */
public class ClientBalanceHold {
    private Long idOfClientBalanceHold;
    private String guid;
    private Client client;
    private Long holdSum;
    private AccountTransaction accountTransaction;
    private Org oldOrg;
    private Org newOrg;
    private Contragent oldContragent;
    private Contragent newContragent;
    private Date createdDate;
    private Date lastUpdate;
    private Long version;
    private ClientBalanceHoldCreateStatus createStatus;
    private ClientBalanceHoldRequestStatus requestStatus;

    public ClientBalanceHold() {

    }

    public Long getIdOfClientBalanceHold() {
        return idOfClientBalanceHold;
    }

    public void setIdOfClientBalanceHold(Long idOfClientBalanceHold) {
        this.idOfClientBalanceHold = idOfClientBalanceHold;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Org getOldOrg() {
        return oldOrg;
    }

    public void setOldOrg(Org oldOrg) {
        this.oldOrg = oldOrg;
    }

    public Org getNewOrg() {
        return newOrg;
    }

    public void setNewOrg(Org newOrg) {
        this.newOrg = newOrg;
    }

    public Contragent getOldContragent() {
        return oldContragent;
    }

    public void setOldContragent(Contragent oldContragent) {
        this.oldContragent = oldContragent;
    }

    public Contragent getNewContragent() {
        return newContragent;
    }

    public void setNewContragent(Contragent newContragent) {
        this.newContragent = newContragent;
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

    public Long getHoldSum() {
        return holdSum;
    }

    public void setHoldSum(Long holdSum) {
        this.holdSum = holdSum;
    }

    public AccountTransaction getAccountTransaction() {
        return accountTransaction;
    }

    public void setAccountTransaction(AccountTransaction accountTransaction) {
        this.accountTransaction = accountTransaction;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public ClientBalanceHoldCreateStatus getCreateStatus() {
        return createStatus;
    }

    public void setCreateStatus(ClientBalanceHoldCreateStatus createStatus) {
        this.createStatus = createStatus;
    }

    public ClientBalanceHoldRequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(ClientBalanceHoldRequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
}
