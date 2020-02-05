/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by nuc on 16.12.2019.
 */
public class PreorderFlag {
    private Long idOfPreorderFlag;
    private Client client;
    private Boolean informedSpecialMenu;
    private Client guardianInformedSpecialMenu;
    private Boolean allowedPreorder;
    private Client guardianAllowedPreorder;
    private Date createdDate;
    private Date lastUpdate;

    public PreorderFlag() {

    }

    public PreorderFlag(Client client) {
        this.client = client;
        this.createdDate = new Date();
    }

    public Long getIdOfPreorderFlag() {
        return idOfPreorderFlag;
    }

    public void setIdOfPreorderFlag(Long idOfPreorderFlag) {
        this.idOfPreorderFlag = idOfPreorderFlag;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Boolean getInformedSpecialMenu() {
        return informedSpecialMenu;
    }

    public void setInformedSpecialMenu(Boolean informedSpecialMenu) {
        this.informedSpecialMenu = informedSpecialMenu;
    }

    public Client getGuardianInformedSpecialMenu() {
        return guardianInformedSpecialMenu;
    }

    public void setGuardianInformedSpecialMenu(Client guardianInformedSpecialMenu) {
        this.guardianInformedSpecialMenu = guardianInformedSpecialMenu;
    }

    public Boolean getAllowedPreorder() {
        return allowedPreorder;
    }

    public void setAllowedPreorder(Boolean allowedPreorder) {
        this.allowedPreorder = allowedPreorder;
    }

    public Client getGuardianAllowedPreorder() {
        return guardianAllowedPreorder;
    }

    public void setGuardianAllowedPreorder(Client guardianAllowedPreorder) {
        this.guardianAllowedPreorder = guardianAllowedPreorder;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
