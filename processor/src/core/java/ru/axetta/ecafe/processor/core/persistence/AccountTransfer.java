/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

public class AccountTransfer {

    Long idOfAccountTransfer;
    Date createTime;
    Client clientBenefactor;
    Client clientBeneficiary;
    String reason;
    User createdBy;
    AccountTransaction transactionOnBenefactor;
    AccountTransaction transactionOnBeneficiary;
    Long transferSum;
    
    AccountTransfer() {
    }

    public AccountTransfer(Date createTime, Client clientBenefactor, Client clientBeneficiary, String reason,
            User createdBy, AccountTransaction transactionOnBenefactor, AccountTransaction transactionOnBeneficiary,
            Long transferSum) {
        this.createTime = createTime;
        this.clientBenefactor = clientBenefactor;
        this.clientBeneficiary = clientBeneficiary;
        this.reason = reason;
        this.createdBy = createdBy;
        this.transactionOnBenefactor = transactionOnBenefactor;
        this.transactionOnBeneficiary = transactionOnBeneficiary;
        this.transferSum = transferSum;
    }

    public Long getIdOfAccountTransfer() {
        return idOfAccountTransfer;
    }

    public void setIdOfAccountTransfer(Long idOfAccountTransfer) {
        this.idOfAccountTransfer = idOfAccountTransfer;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Client getClientBenefactor() {
        return clientBenefactor;
    }

    public void setClientBenefactor(Client clientBenefactor) {
        this.clientBenefactor = clientBenefactor;
    }

    public Client getClientBeneficiary() {
        return clientBeneficiary;
    }

    public void setClientBeneficiary(Client clientBeneficiary) {
        this.clientBeneficiary = clientBeneficiary;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public AccountTransaction getTransactionOnBenefactor() {
        return transactionOnBenefactor;
    }

    public void setTransactionOnBenefactor(AccountTransaction transactionOnBenefactor) {
        this.transactionOnBenefactor = transactionOnBenefactor;
    }

    public AccountTransaction getTransactionOnBeneficiary() {
        return transactionOnBeneficiary;
    }

    public void setTransactionOnBeneficiary(AccountTransaction transactionOnBeneficiary) {
        this.transactionOnBeneficiary = transactionOnBeneficiary;
    }

    public Long getTransferSum() {
        return transferSum;
    }

    public void setTransferSum(Long transferSum) {
        this.transferSum = transferSum;
    }
}
