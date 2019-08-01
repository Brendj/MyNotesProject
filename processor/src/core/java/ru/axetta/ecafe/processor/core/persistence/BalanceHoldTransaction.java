/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

public class BalanceHoldTransaction {
    private Long idOfTransaction;
    private ClientBalanceHold clientBalanceHold;
    private Long transactionSum;
    private Date transactionDate;
    private Long balanceBefore;

    public BalanceHoldTransaction() {

    }

    public BalanceHoldTransaction(ClientBalanceHold clientBalanceHold, long transactionSum, Date transactionDate) throws Exception {
        this.clientBalanceHold = clientBalanceHold;
        this.balanceBefore = clientBalanceHold.getHoldSum();
        this.transactionSum = transactionSum;
        this.transactionDate = transactionDate;
    }

    public Long getIdOfTransaction() {
        return idOfTransaction;
    }

    public void setIdOfTransaction(Long idOfTransaction) {
        this.idOfTransaction = idOfTransaction;
    }

    public ClientBalanceHold getClientBalanceHold() {
        return clientBalanceHold;
    }

    public void setClientBalanceHold(ClientBalanceHold clientBalanceHold) {
        this.clientBalanceHold = clientBalanceHold;
    }

    public Long getTransactionSum() {
        return transactionSum;
    }

    public void setTransactionSum(Long transactionSum) {
        this.transactionSum = transactionSum;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Long getBalanceBefore() {
        return balanceBefore;
    }

    public void setBalanceBefore(Long balanceBefore) {
        this.balanceBefore = balanceBefore;
    }

}
