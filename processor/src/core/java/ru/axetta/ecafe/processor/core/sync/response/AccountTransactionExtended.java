/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 05.04.16
 * Time: 18:49
 * To change this template use File | Settings | File Templates.
 */
public class AccountTransactionExtended {
    private Long idoftransaction;
    private String source;
    private Date transactiondate;
    private Integer sourcetype;
    private Long transactionsum;
    private Long transactionsubbalance1sum;
    private Long complexsum;
    private Long discountsum;
    private Integer ordertype;
    private Long idofclient;

    public Long getIdoftransaction() {
        return idoftransaction;
    }

    public void setIdoftransaction(BigInteger idoftransaction) {
        this.idoftransaction = idoftransaction.longValue();
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getTransactiondate() {
        return transactiondate;
    }

    public void setTransactiondate(BigInteger transactiondate) {
        this.transactiondate = new Date(transactiondate.longValue());
    }

    public Integer getSourcetype() {
        return sourcetype;
    }

    public void setSourcetype(Integer sourcetype) {
        this.sourcetype = sourcetype;
    }

    public Long getTransactionsum() {
        return transactionsum;
    }

    public void setTransactionsum(BigInteger transactionsum) {
        this.transactionsum = transactionsum.longValue();
    }

    public Long getTransactionsubbalance1sum() {
        return transactionsubbalance1sum;
    }

    public void setTransactionsubbalance1sum(BigInteger transactionsubbalance1sum) {
        this.transactionsubbalance1sum = transactionsubbalance1sum.longValue();
    }

    public Long getComplexsum() {
        return complexsum;
    }

    public void setComplexsum(BigDecimal complexsum) {
        this.complexsum = complexsum.longValue();
    }

    public Long getDiscountsum() {
        return discountsum;
    }

    public void setDiscountsum(BigDecimal discountsum) {
        this.discountsum = discountsum.longValue();
    }

    public Integer getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(Integer ordertype) {
        this.ordertype = ordertype;
    }

    public Long getIdofclient() {
        return idofclient;
    }

    public void setIdofclient(BigInteger idofclient) {
        this.idofclient = idofclient.longValue();
    }
}
