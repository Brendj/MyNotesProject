/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 17.11.11
 * Time: 18:00
 * To change this template use File | Settings | File Templates.
 */
public class Settlement {
    private long idOfSettlement;
    private Contragent idOfContragentPayer;
    private Contragent idOfContragentReceiver;
    private Date createdDate;
    private Date paymentDate;
    private String paymentDoc;
    private long summa;

    public Settlement() {
        // For Hibernate only
    }

    public Settlement(long idOfSettlement, Contragent idOfContragentPayer, Contragent idOfContragentReceiver,
            Date createdDate, Date paymentDate, String paymentDoc, long summa) {
        this.idOfSettlement = idOfSettlement;
        this.idOfContragentPayer = idOfContragentPayer;
        this.idOfContragentReceiver = idOfContragentReceiver;
        this.createdDate = createdDate;
        this.paymentDate = paymentDate;
        this.paymentDoc = paymentDoc;
        this.summa = summa;
    }

    public long getIdOfSettlement() {
        return idOfSettlement;
    }

    public void setIdOfSettlement(long idOfSettlement) {
        this.idOfSettlement = idOfSettlement;
    }

    public Contragent getIdOfContragentPayer() {
        return idOfContragentPayer;
    }

    public void setIdOfContragentPayer(Contragent idOfContragentPayer) {
        this.idOfContragentPayer = idOfContragentPayer;
    }

    public Contragent getIdOfContragentReceiver() {
        return idOfContragentReceiver;
    }

    public void setIdOfContragentReceiver(Contragent idOfContragentReceiver) {
        this.idOfContragentReceiver = idOfContragentReceiver;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentDoc() {
        return paymentDoc;
    }

    public void setPaymentDoc(String paymentDoc) {
        this.paymentDoc = paymentDoc;
    }

    public long getSumma() {
        return summa;
    }

    public void setSumma(long summa) {
        this.summa = summa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Settlement that = (Settlement) o;

        if (idOfSettlement != that.idOfSettlement) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (idOfSettlement ^ (idOfSettlement >>> 32));
    }

    @Override
    public String toString() {
        return "Settlement{" + "idOfSettlement=" + idOfSettlement + ", idOfContragentPayer=" + idOfContragentPayer
                + ", idOfContragentReceiver=" + idOfContragentReceiver + ", createdDate=" + createdDate
                + ", paymentDate=" + paymentDate + ", paymentDoc='" + paymentDoc + '\'' + ", summa=" + summa + '}';
    }
}
