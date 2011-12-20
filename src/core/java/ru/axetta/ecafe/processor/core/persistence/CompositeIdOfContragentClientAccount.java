/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 27.07.2009
 * Time: 13:50:22
 * To change this template use File | Settings | File Templates.
 */
public class CompositeIdOfContragentClientAccount implements Serializable {

    private Long idOfContragent;
    private Long idOfAccount;

    CompositeIdOfContragentClientAccount() {
        // For Hibernate only
    }

    public CompositeIdOfContragentClientAccount(long idOfContragent, long idOfAccount) {
        this.idOfContragent = idOfContragent;
        this.idOfAccount = idOfAccount;
    }

    public Long getIdOfContragent() {
        return idOfContragent;
    }

    private void setIdOfContragent(Long idOfContragent) {
        // For Hibernate only
        this.idOfContragent = idOfContragent;
    }

    public Long getIdOfAccount() {
        return idOfAccount;
    }

    private void setIdOfAccount(Long idOfAccount) {
        // For Hibernate only
        this.idOfAccount = idOfAccount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompositeIdOfContragentClientAccount)) {
            return false;
        }
        final CompositeIdOfContragentClientAccount that = (CompositeIdOfContragentClientAccount) o;
        return idOfContragent.equals(that.getIdOfContragent()) && idOfAccount.equals(that.getIdOfAccount());
    }

    @Override
    public int hashCode() {
        int result = idOfContragent.hashCode();
        result = 31 * result + idOfAccount.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CompositeIdOfContragentClientAccount{" + "idOfContragent=" + idOfContragent + ", idOfAccount="
                + idOfAccount + '}';
    }
}