/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class TurnstileSettings implements Serializable {

    private Long idOfTurnstileSetting;
    private Org org;
    private Long version;
    private Integer numOfEntries;
    private String turnstileId;
    private String controllerModel;
    private String controllerFirmwareVersion;
    private Integer isReadsLongIdsIncorrectly;
    private Date lastUpdateForTurnstile;
    private Set<Org> orgsInternal = new HashSet<>();

    public TurnstileSettings() {

    }

    public TurnstileSettings(Long idOfTurnstileSetting, Org org, Integer numOfEntries, String turnstileId,
            String controllerModel, String controllerFirmwareVersion, Integer isReadsLongIdsIncorrectly,
            Date lastUpdateForTurnstile, Long version) {
        this.idOfTurnstileSetting = idOfTurnstileSetting;
        this.org = org;
        this.numOfEntries = numOfEntries;
        this.turnstileId = turnstileId;
        this.controllerModel = controllerModel;
        this.controllerFirmwareVersion = controllerFirmwareVersion;
        this.isReadsLongIdsIncorrectly = isReadsLongIdsIncorrectly;
        this.lastUpdateForTurnstile = lastUpdateForTurnstile;
        this.version = version;
    }

    public Long getIdOfTurnstileSetting() {
        return idOfTurnstileSetting;
    }

    public void setIdOfTurnstileSetting(Long idOfTurnstileSetting) {
        this.idOfTurnstileSetting = idOfTurnstileSetting;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public String getTurnstileId() {
        return turnstileId;
    }

    public void setTurnstileId(String turnstileId) {
        this.turnstileId = turnstileId;
    }

    public String getControllerModel() {
        return controllerModel;
    }

    public void setControllerModel(String controllerModel) {
        this.controllerModel = controllerModel;
    }

    public String getControllerFirmwareVersion() {
        return controllerFirmwareVersion;
    }

    public void setControllerFirmwareVersion(String controllerFirmwareVersion) {
        this.controllerFirmwareVersion = controllerFirmwareVersion;
    }

    public Integer getIsReadsLongIdsIncorrectly() {
        return isReadsLongIdsIncorrectly;
    }

    public void setIsReadsLongIdsIncorrectly(Integer isReadsLongIdsIncorrectly) {
        this.isReadsLongIdsIncorrectly = isReadsLongIdsIncorrectly;
    }

    public Date getLastUpdateForTurnstile() {
        return lastUpdateForTurnstile;
    }

    public void setLastUpdateForTurnstile(Date lastUpdateForTurnstile) {
        this.lastUpdateForTurnstile = lastUpdateForTurnstile;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Integer getNumOfEntries() {
        return numOfEntries;
    }

    public void setNumOfEntries(Integer numOfEntries) {
        this.numOfEntries = numOfEntries;
    }

    public Set<Org> getOrgsInternal() {
        return orgsInternal;
    }

    public void setOrgsInternal(Set<Org> orgsInternal) {
        this.orgsInternal = orgsInternal;
    }
}
