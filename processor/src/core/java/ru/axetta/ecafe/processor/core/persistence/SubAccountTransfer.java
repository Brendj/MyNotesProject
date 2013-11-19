/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

public class SubAccountTransfer {

    private Long idOfSubAccountTransfer;
    private Date createTime;
    private Client clientTransfer;
    private Long balanceBenefactor;
    private Long balanceBeneficiary;
    private String reason;
    private AccountTransaction transactionOnBenefactor;
    private AccountTransaction transactionOnBeneficiary;
    private Long transferSum;

    protected SubAccountTransfer() {}

    public SubAccountTransfer(Date createTime, Client clientTransfer, Long balanceBenefactor, Long balanceBeneficiary, String reason,
            AccountTransaction transactionOnBenefactor, AccountTransaction transactionOnBeneficiary, Long transferSum) {
        this.createTime = createTime;
        this.clientTransfer = clientTransfer;
        this.balanceBenefactor = balanceBenefactor;
        this.balanceBeneficiary = balanceBeneficiary;
        this.reason = reason;
        this.transactionOnBenefactor = transactionOnBenefactor;
        this.transactionOnBeneficiary = transactionOnBeneficiary;
        this.transferSum = transferSum;
    }

    public Long getIdOfSubAccountTransfer() {
        return idOfSubAccountTransfer;
    }

    public void setIdOfSubAccountTransfer(Long idOfAccountTransfer) {
        this.idOfSubAccountTransfer = idOfAccountTransfer;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Client getClientTransfer() {
        return clientTransfer;
    }

    public void setClientTransfer(Client clientTransfer) {
        this.clientTransfer = clientTransfer;
    }

    public Long getBalanceBenefactor() {
        return balanceBenefactor;
    }

    public void setBalanceBenefactor(Long balanceBenefactor) {
        this.balanceBenefactor = balanceBenefactor;
    }

    public Long getBalanceBeneficiary() {
        return balanceBeneficiary;
    }

    public void setBalanceBeneficiary(Long balanceBeneficiary) {
        this.balanceBeneficiary = balanceBeneficiary;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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
