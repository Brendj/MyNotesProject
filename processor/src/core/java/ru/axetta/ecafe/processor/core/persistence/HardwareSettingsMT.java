/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class HardwareSettingsMT implements Serializable {

    private Integer moduleType;
    private Integer installStatus;
    private Date lastUpdate;
    private HardwareSettings hardwareSettings;
    private Long idOfModuleType;
    private Set<HardwareSettingsMT> orgsInternal = new HashSet<HardwareSettingsMT>();

    public HardwareSettingsMT() {
    }

    public HardwareSettingsMT(Integer moduleType, Integer installStatus, Date lastUpdate,
            HardwareSettings hardwareSettings, Long idOfModuleType) {
        this.moduleType = moduleType;
        this.installStatus = installStatus;
        this.lastUpdate = lastUpdate;
        this.hardwareSettings = hardwareSettings;
        this.idOfModuleType = idOfModuleType;
    }

    public Integer getModuleType() {
        return moduleType;
    }

    public void setModuleType(Integer moduleType) {
        this.moduleType = moduleType;
    }

    public Integer getInstallStatus() {
        return installStatus;
    }

    public void setInstallStatus(Integer installStatus) {
        this.installStatus = installStatus;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public HardwareSettings getHardwareSettings() {
        return hardwareSettings;
    }

    public void setHardwareSettings(HardwareSettings hardwareSettings) {
        this.hardwareSettings = hardwareSettings;
    }

    public Long getIdOfModuleType() {
        return idOfModuleType;
    }

    public void setIdOfModuleType(Long idOfModuleType) {
        this.idOfModuleType = idOfModuleType;
    }

    public Set<HardwareSettingsMT> getOrgsInternal() {
        return orgsInternal;
    }

    public void setOrgsInternal(Set<HardwareSettingsMT> orgsInternal) {
        this.orgsInternal = orgsInternal;
    }
}
