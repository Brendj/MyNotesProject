/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 28.03.16
 * Time: 12:22
 * To change this template use File | Settings | File Templates.
 */
public class CompositeIdOfZeroTransaction implements Serializable {
    private Long idOfOrg;
    private Date transactionDate;
    private ZeroTransactionCriteriaEnum idOfCriteria;

    protected CompositeIdOfZeroTransaction() {
        //for Hibernate only
    }

    public CompositeIdOfZeroTransaction(Long idOfOrg, Date transactionDate, ZeroTransactionCriteriaEnum idOfCriteria) {
        this.setIdOfOrg(idOfOrg);
        this.setTransactionDate(transactionDate);
        this.setIdOfCriteria(idOfCriteria);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompositeIdOfZeroTransaction)) {
            return false;
        }
        final CompositeIdOfZeroTransaction that = (CompositeIdOfZeroTransaction) o;
        return getIdOfOrg().equals(that.getIdOfOrg()) && transactionDate.equals(that.getTransactionDate()) && idOfCriteria.equals(that.getIdOfCriteria());
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public ZeroTransactionCriteriaEnum getIdOfCriteria() {
        return idOfCriteria;
    }

    public void setIdOfCriteria(ZeroTransactionCriteriaEnum idOfCriteria) {
        this.idOfCriteria = idOfCriteria;
    }
}
