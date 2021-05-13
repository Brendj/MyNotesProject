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
    private String readerName;
    private String firmwareVer;
    private Set<HardwareSettingsMT> orgsInternal = new HashSet<HardwareSettingsMT>();
    private Long idOfHardwareSetting;
    private Long idOfOrg;
    private String hostIp;

    public HardwareSettingsMT() {
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

    public Long getIdOfHardwareSetting() {
        return idOfHardwareSetting;
    }

    public void setIdOfHardwareSetting(Long idOfHardwareSetting) {
        this.idOfHardwareSetting = idOfHardwareSetting;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getReaderName() {
        return readerName;
    }

    public void setReaderName(String readerName) {
        this.readerName = readerName;
    }

    public String getFirmwareVer() {
        return firmwareVer;
    }

    public void setFirmwareVer(String firmwareVer) {
        this.firmwareVer = firmwareVer;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }
}
