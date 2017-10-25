/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Created by i.semenov on 23.10.2017.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionInfo")
public class TransactionInfo {
    @XmlAttribute(name = "idOfTransaction")
    protected Long idOfTransaction;

    @XmlAttribute(name = "transactionSum")
    protected Long transactionSum;

    @XmlAttribute(name = "sourceType")
    protected Integer sourceType;

    @XmlAttribute(name = "transactionDate")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar transactionDate;

    @XmlAttribute(name = "balanceBefore")
    protected Long balanceBefore;

    @XmlAttribute(name = "balanceAfter")
    protected Long balanceAfter;

    @XmlAttribute(name = "idOfOrder")
    protected Long idOfOrder;

    @XmlAttribute(name = "contractId")
    protected Long contractId;

    public TransactionInfo() {

    }

    public TransactionInfo(Long idOfTransaction, Long transactionSum, Integer sourceType, XMLGregorianCalendar transactionDate,
                Long balanceBefore, Long balanceAfter, Long idOfOrder, Long contractId) {
        this.idOfTransaction = idOfTransaction;
        this.transactionSum = transactionSum;
        this.sourceType = sourceType;
        this.transactionDate = transactionDate;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.idOfOrder = idOfOrder;
        this.contractId = contractId;
    }

    public Long getIdOfTransaction() {
        return idOfTransaction;
    }

    public void setIdOfTransaction(Long idOfTransaction) {
        this.idOfTransaction = idOfTransaction;
    }

    public Long getTransactionSum() {
        return transactionSum;
    }

    public void setTransactionSum(Long transactionSum) {
        this.transactionSum = transactionSum;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public XMLGregorianCalendar getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(XMLGregorianCalendar transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Long getBalanceBefore() {
        return balanceBefore;
    }

    public void setBalanceBefore(Long balanceBefore) {
        this.balanceBefore = balanceBefore;
    }

    public Long getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(Long balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public Long getIdOfOrder() {
        return idOfOrder;
    }

    public void setIdOfOrder(Long idOfOrder) {
        this.idOfOrder = idOfOrder;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }
}
