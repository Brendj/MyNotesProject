/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

public class AccountRefund {

    private Long idOfAccountRefund;
    private Date createTime;
    private Client client;
    private String reason;
    private User createdBy;
    private AccountTransaction transaction;
    private Long refundSum;

    protected AccountRefund() {
    }

    public AccountRefund(Date createTime, Client client, String reason, User createdBy, AccountTransaction transaction,
            Long refundSum) {
        this.createTime = createTime;
        this.client = client;
        this.reason = reason;
        this.createdBy = createdBy;
        this.transaction = transaction;
        this.refundSum = refundSum;
    }

    public Long getRefundSum() {
        return refundSum;
    }

    public void setRefundSum(Long refundSum) {
        this.refundSum = refundSum;
    }

    public AccountTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(AccountTransaction transaction) {
        this.transaction = transaction;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getIdOfAccountRefund() {
        return idOfAccountRefund;
    }

    protected void setIdOfAccountRefund(Long idOfAccountRefund) {
        this.idOfAccountRefund = idOfAccountRefund;
    }
}
