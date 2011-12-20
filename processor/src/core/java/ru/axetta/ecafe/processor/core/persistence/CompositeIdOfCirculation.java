/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 07.10.11
 * Time: 12:44
 * To change this template use File | Settings | File Templates.
 */
public class CompositeIdOfCirculation implements Serializable {
    private long idOfCirculation;
    private long idOfOrg;

    CompositeIdOfCirculation() {
        // For Hibernate only
    }

    public CompositeIdOfCirculation(long idOfCirculation, long idOfOrg) {
        this.idOfCirculation = idOfCirculation;
        this.idOfOrg = idOfOrg;
    }

    public long getIdOfCirculation() {
        return idOfCirculation;
    }

    public void setIdOfCirculation(long idOfCirculation) {
        // For Hibernate only
        this.idOfCirculation = idOfCirculation;
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

        CompositeIdOfCirculation that = (CompositeIdOfCirculation) o;

        if (idOfCirculation != that.idOfCirculation) {
            return false;
        }
        if (idOfOrg != that.idOfOrg) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idOfCirculation ^ (idOfCirculation >>> 32));
        result = 31 * result + (int) (idOfOrg ^ (idOfOrg >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "CompositeIdOfCirculation{" + "idOfCirculation=" + idOfCirculation + ", idOfOrg=" + idOfOrg + '}';
    }
}
