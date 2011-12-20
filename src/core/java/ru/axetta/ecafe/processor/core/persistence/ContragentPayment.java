/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 02.06.2009
 * Time: 10:39:31
 * To change this template use File | Settings | File Templates.
 */
public class ContragentPayment {

    private Long idOfContragentPayment;
    private Contragent contragent;
    private AccountTransaction transaction;
    private Long paySum;
    private Integer payType;
    private Integer state;
    private Date createTime;
    private Date paymentTime;

    ContragentPayment() {
        // For Hibernate only
    }

    ContragentPayment(Contragent contragent, AccountTransaction transaction, long paySum, int payType, int state,
            Date createTime) {
        this.contragent = contragent;
        this.transaction = transaction;
        this.paySum = paySum;
        this.payType = payType;
        this.state = state;
        this.createTime = createTime;
    }

    public Long getIdOfContragentPayment() {
        return idOfContragentPayment;
    }

    private void setIdOfContragentPayment(Long idOfContragentPayment) {
        // For Hibernate only
        this.idOfContragentPayment = idOfContragentPayment;
    }

    public Contragent getContragent() {
        return contragent;
    }

    private void setContragent(Contragent contragent) {
        // For Hibernate only
        this.contragent = contragent;
    }

    public AccountTransaction getTransaction() {
        return transaction;
    }

    private void setTransaction(AccountTransaction accountTransaction) {
        // For Hibernate only
        this.transaction = accountTransaction;
    }

    public Long getPaySum() {
        return paySum;
    }

    private void setPaySum(Long paySum) {
        // For Hibernate only
        this.paySum = paySum;
    }

    public Integer getPayType() {
        return payType;
    }

    private void setPayType(Integer payType) {
        // For Hibernate only
        this.payType = payType;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    private void setCreateTime(Date createTime) {
        // For Hibernate only
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContragentPayment)) {
            return false;
        }
        final ContragentPayment that = (ContragentPayment) o;
        return idOfContragentPayment.equals(that.getIdOfContragentPayment());
    }

    @Override
    public int hashCode() {
        return idOfContragentPayment.hashCode();
    }

    @Override
    public String toString() {
        return "ContragentPayment{" + "idOfContragentPayment=" + idOfContragentPayment + ", contragent=" + contragent
                + ", accountTransaction=" + transaction + ", paySum=" + paySum + ", payType=" + payType + ", state="
                + state + ", createTime=" + createTime + ", paymentTime=" + paymentTime + '}';
    }
}