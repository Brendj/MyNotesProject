/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 16.07.12
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class DOConfirm {

    private long idOfDOConfirm;
    private String distributedObjectClassName;
    private String guid;
    private long orgOwner;

    public DOConfirm() {}

    public DOConfirm(String distributedObjectClassName, String guid, long orgOwner) {
        this.distributedObjectClassName = distributedObjectClassName;
        this.guid = guid;
        this.orgOwner = orgOwner;
    }

    public long getOrgOwner() {
        return orgOwner;
    }

    public void setOrgOwner(long orgOwner) {
        this.orgOwner = orgOwner;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getDistributedObjectClassName() {
        return distributedObjectClassName;
    }

    public void setDistributedObjectClassName(String distributedObjectClassName) {
        this.distributedObjectClassName = distributedObjectClassName;
    }

    public long getIdOfDOConfirm() {
        return idOfDOConfirm;
    }

    public void setIdOfDOConfirm(long idOfDOConfirm) {
        this.idOfDOConfirm = idOfDOConfirm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DOConfirm doConfirm = (DOConfirm) o;

        if (orgOwner != doConfirm.orgOwner) {
            return false;
        }
        if (!distributedObjectClassName.equals(doConfirm.distributedObjectClassName)) {
            return false;
        }
        if (!guid.equals(doConfirm.guid)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = distributedObjectClassName.hashCode();
        result = 31 * result + guid.hashCode();
        result = 31 * result + (int) (orgOwner ^ (orgOwner >>> 32));
        return result;
    }
}
