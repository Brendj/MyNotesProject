/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;
import java.util.Date;

public class OrgEquipment implements Serializable {
    private Long idOfHardwareSetting;
    private Org org;
    private Integer moduleType;
    private Integer installStatus;
    private Date lastUpdateForModuleType;
    private String ipHost;
    private Date lastUpdateForIPHost;
    private String dotNetVer;
    private Date lastUpdateForDotNetVer;
    private String oSVer;
    private Date lastUpdateForOSVer;
    private String ramSize;
    private Date lastUpdateForRAMSize;
    private String cpuHost;
    private Date lastUpdateForCPUHost;
    private Integer readerUsedByModule;
    private String readerName;
    private String firmwareVer;
    private Date lastUpdateReader;
    private String turnstileId;
    private String controllerModel;
    private String controllerFirmwareVersion;
    private Boolean isWorkWithLongIds;
    private Date lastUpdateForTurnstile;
    private Long version;

    public OrgEquipment() {

    }

    public OrgEquipment(Integer moduleType, Integer installStatus, Date lastUpdateForModuleType) {
        this.moduleType = moduleType;
        this.installStatus = installStatus;
        this.lastUpdateForModuleType = lastUpdateForModuleType;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
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

    public Date getLastUpdateForModuleType() {
        return lastUpdateForModuleType;
    }

    public void setLastUpdateForModuleType(Date lastUpdateForModuleType) {
        this.lastUpdateForModuleType = lastUpdateForModuleType;
    }

    public String getIpHost() {
        return ipHost;
    }

    public void setIpHost(String ipHost) {
        this.ipHost = ipHost;
    }

    public Date getLastUpdateForIPHost() {
        return lastUpdateForIPHost;
    }

    public void setLastUpdateForIPHost(Date lastUpdateForIPHost) {
        this.lastUpdateForIPHost = lastUpdateForIPHost;
    }

    public String getDotNetVer() {
        return dotNetVer;
    }

    public void setDotNetVer(String dotNetVer) {
        this.dotNetVer = dotNetVer;
    }

    public Date getLastUpdateForDotNetVer() {
        return lastUpdateForDotNetVer;
    }

    public void setLastUpdateForDotNetVer(Date lastUpdateForDotNetVer) {
        this.lastUpdateForDotNetVer = lastUpdateForDotNetVer;
    }

    public String getoSVer() {
        return oSVer;
    }

    public void setoSVer(String oSVer) {
        this.oSVer = oSVer;
    }

    public Date getLastUpdateForOSVer() {
        return lastUpdateForOSVer;
    }

    public void setLastUpdateForOSVer(Date lastUpdateForOSVer) {
        this.lastUpdateForOSVer = lastUpdateForOSVer;
    }

    public String getRamSize() {
        return ramSize;
    }

    public void setRamSize(String ramSize) {
        this.ramSize = ramSize;
    }

    public Date getLastUpdateForRAMSize() {
        return lastUpdateForRAMSize;
    }

    public void setLastUpdateForRAMSize(Date lastUpdateForRAMSize) {
        this.lastUpdateForRAMSize = lastUpdateForRAMSize;
    }

    public String getCpuHost() {
        return cpuHost;
    }

    public void setCpuHost(String cpuHost) {
        this.cpuHost = cpuHost;
    }

    public Date getLastUpdateForCPUHost() {
        return lastUpdateForCPUHost;
    }

    public void setLastUpdateForCPUHost(Date lastUpdateForCPUHost) {
        this.lastUpdateForCPUHost = lastUpdateForCPUHost;
    }

    public Integer getReaderUsedByModule() {
        return readerUsedByModule;
    }

    public void setReaderUsedByModule(Integer readerUsedByModule) {
        this.readerUsedByModule = readerUsedByModule;
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

    public Date getLastUpdateReader() {
        return lastUpdateReader;
    }

    public void setLastUpdateReader(Date lastUpdateReader) {
        this.lastUpdateReader = lastUpdateReader;
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

    public Boolean getWorkWithLongIds() {
        return isWorkWithLongIds;
    }

    public void setWorkWithLongIds(Boolean workWithLongIds) {
        isWorkWithLongIds = workWithLongIds;
    }

    public Date getLastUpdateForTurnstile() {
        return lastUpdateForTurnstile;
    }

    public void setLastUpdateForTurnstile(Date lastUpdateForTurnstile) {
        this.lastUpdateForTurnstile = lastUpdateForTurnstile;
    }

    public Long getIdOfHardwareSetting() {
        return idOfHardwareSetting;
    }

    public void setIdOfHardwareSetting(Long idOfHardwareSetting) {
        this.idOfHardwareSetting = idOfHardwareSetting;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
