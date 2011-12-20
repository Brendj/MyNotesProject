/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
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
public class SochiClientPayment {

    private Long paymentId;
    private SochiClient client;
    private Long paymentSum;
    private Long paymentSumF;
    private Date paymentTime;
    private Long terminalId;
    private Date createTime;

    SochiClientPayment() {
        // For Hibernate only
    }

    public SochiClientPayment(Long paymentId, SochiClient client, Long paymentSum, Long paymentSumF, Date paymentTime,
            Long terminalId) {
        this.paymentId = paymentId;
        this.client = client;
        this.paymentSum = paymentSum;
        this.paymentSumF = paymentSumF;
        this.paymentTime = paymentTime;
        this.terminalId = terminalId;
        this.createTime = new Date();
    }

    public Long getPaymentId() {
        return paymentId;
    }

    private void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public SochiClient getClient() {
        return client;
    }

    private void setClient(SochiClient client) {
        this.client = client;
    }

    public Long getPaymentSum() {
        return paymentSum;
    }

    private void setPaymentSum(Long paymentSum) {
        this.paymentSum = paymentSum;
    }

    public Date getCreateTime() {
        return createTime;
    }

    private void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getPaymentSumF() {
        return paymentSumF;
    }

    private void setPaymentSumF(Long paymentSumF) {
        this.paymentSumF = paymentSumF;
    }

    public Date getPaymentTime() {
        return paymentTime;
    }

    private void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }

    public Long getTerminalId() {
        return terminalId;
    }

    private void setTerminalId(Long terminalId) {
        this.terminalId = terminalId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SochiClientPayment)) {
            return false;
        }
        final SochiClientPayment that = (SochiClientPayment) o;
        return paymentId.equals(that.getPaymentId());
    }

    @Override
    public int hashCode() {
        return paymentId.hashCode();
    }

    @Override
    public String toString() {
        return "SochiClientPayment{" + "paymentId=" + paymentId + ", client=" + client + ", paymentSum=" + paymentSum
                + ", createTime=" + createTime + '}';
    }
}