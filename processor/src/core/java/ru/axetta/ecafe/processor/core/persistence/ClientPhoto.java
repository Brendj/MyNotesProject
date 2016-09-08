/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 18.07.16
 * Time: 10:37
 */
public class ClientPhoto {
    private Long idOfClientPhoto;
    private Long idOfClient;
    private String name;
    private boolean isNew;
    private boolean isCanceled;
    private boolean isApproved;
    private Client guardian;
    private String lastProceedError;
    private Long version;

    public ClientPhoto() {
    }

    public ClientPhoto(Long idOfClient, String name) {
        this.idOfClient = idOfClient;
        this.name = name;
    }

    public ClientPhoto(Long idOfClient, Client guardian, String name, boolean isNew) {
        this.idOfClient = idOfClient;
        this.guardian = guardian;
        this.name = name;
        this.isNew = isNew;
    }

    public Long getIdOfClientPhoto() {
        return idOfClientPhoto;
    }

    public void setIdOfClientPhoto(Long idOfClientPhoto) {
        this.idOfClientPhoto = idOfClientPhoto;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public boolean getIsCanceled() {
        return isCanceled;
    }

    public void setIsCanceled(boolean isCanceled) {
        this.isCanceled = isCanceled;
    }

    public boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(boolean approved) {
        isApproved = approved;
    }

    public Client getGuardian() {
        return guardian;
    }

    public void setGuardian(Client guardian) {
        this.guardian = guardian;
    }

    public String getLastProceedError() {
        return lastProceedError;
    }

    public void setLastProceedError(String lastProceedError) {
        this.lastProceedError = lastProceedError;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
