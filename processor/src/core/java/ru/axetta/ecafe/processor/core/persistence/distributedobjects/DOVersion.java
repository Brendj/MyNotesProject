/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 15.06.12
 * Time: 16:37
 * To change this template use File | Settings | File Templates.
 */
public class DOVersion {

    private long idOfDOObject;
    private String distributedObjectClassName;
    private long currentVersion;

    public long getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(long currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getDistributedObjectClassName() {
        return distributedObjectClassName;
    }

    public void setDistributedObjectClassName(String distributedObjectClassName) {
        this.distributedObjectClassName = distributedObjectClassName;
    }

    public long getIdOfDOObject() {
        return idOfDOObject;
    }

    public void setIdOfDOObject(long idOfDOObject) {
        this.idOfDOObject = idOfDOObject;
    }
}
