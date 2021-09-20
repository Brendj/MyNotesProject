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
@Deprecated
public class DOVersion {

    private Long idOfDOObject;
    private String distributedObjectClassName;
    private Long currentVersion;

    public Long getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(Long currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getDistributedObjectClassName() {
        return distributedObjectClassName;
    }

    public void setDistributedObjectClassName(String distributedObjectClassName) {
        this.distributedObjectClassName = distributedObjectClassName;
    }

    public Long getIdOfDOObject() {
        return idOfDOObject;
    }

    public void setIdOfDOObject(Long idOfDOObject) {
        this.idOfDOObject = idOfDOObject;
    }
}
