/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 16.09.11
 * Time: 13:05
 * To change this template use File | Settings | File Templates.
 */
public class Circul {
    private long idOfCircul;
    private Publ publ;
    private Org org;
    private Client client;
    private Date issuanceDate;
    private Date refundDate;
    private Date realRefundDate;
    private int quantity;

    public Circul() {
        // For Hibernate
    }

    public Circul(long idOfCircul, Client client,
            Publ publ, Org org, Date issuanceDate, Date refundDate, int quantity) {
        this.idOfCircul = idOfCircul;
        this.client = client;
        this.publ = publ;
        this.org = org;
        this.issuanceDate = issuanceDate;
        this.refundDate = refundDate;
        this.quantity = quantity;
    }

    public long getIdOfCircul() {
        return idOfCircul;
    }

    public void setIdOfCircul(long idOfCircul) {
        this.idOfCircul = idOfCircul;
    }

    public Publ getPubl() {
        return publ;
    }

    public void setPubl(Publ publ) {
        this.publ = publ;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Date getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(Date issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    public Date getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(Date refundDate) {
        this.refundDate = refundDate;
    }

    public Date getRealRefundDate() {
        return realRefundDate;
    }

    public void setRealRefundDate(Date realRefundDate) {
        this.realRefundDate = realRefundDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Circul circul = (Circul) o;

        if (idOfCircul != circul.idOfCircul) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (idOfCircul ^ (idOfCircul >>> 32));
    }

    @Override
    public String toString() {
        return "Circul{" +
                "idOfCircul=" + idOfCircul +
                ", publ=" + publ +
                ", org=" + org +
                ", client=" + client +
                ", issuanceDate=" + issuanceDate +
                ", refundDate=" + refundDate +
                ", realRefundDate=" + realRefundDate +
                ", quantity=" + quantity +
                '}';
    }
}
