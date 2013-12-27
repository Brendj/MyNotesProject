/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 27.12.13
 * Time: 11:35
 * To change this template use File | Settings | File Templates.
 */
public class ClientGuardian {

    private long idOfClientGuardian;
    private long idOfChildren;
    private long idOfGuardian;

    protected ClientGuardian() {}

    public ClientGuardian(long idOfChildren, long idOfGuardian) {
        this.idOfChildren = idOfChildren;
        this.idOfGuardian = idOfGuardian;
    }

    public long getIdOfClientGuardian() {
        return idOfClientGuardian;
    }

    public void setIdOfClientGuardian(long idOfClientGuardian) {
        this.idOfClientGuardian = idOfClientGuardian;
    }

    public long getIdOfChildren() {
        return idOfChildren;
    }

    public void setIdOfChildren(long idOfChildren) {
        this.idOfChildren = idOfChildren;
    }

    public long getIdOfGuardian() {
        return idOfGuardian;
    }

    public void setIdOfGuardian(long idOfGuardian) {
        this.idOfGuardian = idOfGuardian;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ClientGuardian that = (ClientGuardian) o;

        if (idOfChildren != that.idOfChildren) {
            return false;
        }
        if (idOfClientGuardian != that.idOfClientGuardian) {
            return false;
        }
        if (idOfGuardian != that.idOfGuardian) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idOfClientGuardian ^ (idOfClientGuardian >>> 32));
        result = 31 * result + (int) (idOfChildren ^ (idOfChildren >>> 32));
        result = 31 * result + (int) (idOfGuardian ^ (idOfGuardian >>> 32));
        return result;
    }
}
