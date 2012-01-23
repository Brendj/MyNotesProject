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
public class Circulation {
    private CompositeIdOfCirculation compositeIdOfCirculation;
    private long idOfPublication;
    private long idOfOrg;
    private Client client;
    private Publication publication;
    private Date issuanceDate;
    private Date refundDate;
    private Date realRefundDate;
    private int status;
    private long version;

    public Circulation() {
        // For Hibernate
    }

    public Circulation(CompositeIdOfCirculation compositeIdOfCirculation, Client client, Publication publication,
            long idOfPublication, long idOfOrg, Date issuanceDate, Date refundDate, int status, long version) {
        this.compositeIdOfCirculation = compositeIdOfCirculation;
        this.client = client;
        this.idOfPublication = idOfPublication;
        this.idOfOrg = idOfOrg;
        this.publication = publication;
        this.issuanceDate = issuanceDate;
        this.refundDate = refundDate;
        this.status = status;
        this.version = version;
    }

    public CompositeIdOfCirculation getCompositeIdOfCirculation() {
        return compositeIdOfCirculation;
    }

    public void setCompositeIdOfCirculation(CompositeIdOfCirculation compositeIdOfCirculation) {
        this.compositeIdOfCirculation = compositeIdOfCirculation;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    public long getIdOfPublication() {
        return idOfPublication;
    }

    public void setIdOfPublication(long idOfPublication) {
        this.idOfPublication = idOfPublication;
    }

    public long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(long idOfOrg) {
        this.idOfOrg = idOfOrg;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Circulation that = (Circulation) o;

        if (!compositeIdOfCirculation.equals(that.compositeIdOfCirculation)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return compositeIdOfCirculation.hashCode();
    }

    @Override
    public String toString() {
        return "Circulation{" + "compositeIdOfCirculation=" + compositeIdOfCirculation
                + ", Client=" + client + ", Publication=" + publication
                + ", issuanceDate=" + issuanceDate + ", refundDate=" + refundDate
                + ", realRefundDate=" + realRefundDate + ", status=" + status + ", version=" + version + '}';
    }
}
