/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch.dto;

import java.util.Date;

public class JsonTransaction {
    private Date orderDate;
    private Integer transactionType;
    private Long transactionSum;
    private Long idOfTransaction;

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Integer getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(Integer transactionType) {
        this.transactionType = transactionType;
    }

    public Long getTransactionSum() {
        return transactionSum;
    }

    public void setTransactionSum(Long transactionSum) {
        this.transactionSum = transactionSum;
    }

    public Long getIdOfTransaction() {
        return idOfTransaction;
    }

    public void setIdOfTransaction(Long idOfTransaction) {
        this.idOfTransaction = idOfTransaction;
    }
}
