/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;
import java.util.Date;

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
    private Double timeCoefficient;

    public TurnstileSettings() {

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

    public Double getTimeCoefficient() {
        return timeCoefficient;
    }

    public void setTimeCoefficient(Double timeCoefficient) {
        this.timeCoefficient = timeCoefficient;
    }
}
