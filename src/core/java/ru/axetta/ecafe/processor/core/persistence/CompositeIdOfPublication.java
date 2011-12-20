/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 07.10.11
 * Time: 12:55
 * To change this template use File | Settings | File Templates.
 */
public class CompositeIdOfPublication implements Serializable {
    private long idOfPublication;
    private long idOfOrg;

    public CompositeIdOfPublication() {
        // For Hibernate only
    }

    public CompositeIdOfPublication(long idOfPublication, long idOfOrg) {
        this.idOfPublication = idOfPublication;
        this.idOfOrg = idOfOrg;
    }

    public long getIdOfPublication() {
        return idOfPublication;
    }

    public void setIdOfPublication(long idOfPublication) {
        // For Hibernate only
        this.idOfPublication = idOfPublication;
    }

    public long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(long idOfOrg) {
        // For Hibernate only
        this.idOfOrg = idOfOrg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CompositeIdOfPublication that = (CompositeIdOfPublication) o;

        if (idOfOrg != that.idOfOrg) {
            return false;
        }
        if (idOfPublication != that.idOfPublication) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idOfPublication ^ (idOfPublication >>> 32));
        result = 31 * result + (int) (idOfOrg ^ (idOfOrg >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "CompositeIdOfPublication{" + "idOfPublication=" + idOfPublication + ", idOfOrg=" + idOfOrg + '}';
    }
}
