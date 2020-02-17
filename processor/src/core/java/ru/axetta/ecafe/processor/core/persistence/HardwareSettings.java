/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;
import java.util.Date;

public class HardwareSettings implements Serializable {

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
    private Integer usedByModule;
    private String readerName;
    private String firmwareVer;
    private Date lastUpdateForReader;
    private Long version;

    public HardwareSettings() {

    }

    public HardwareSettings(Long idOfHardwareSetting, Org org, Integer moduleType, Integer installStatus,
            Date lastUpdateForModuleType, String ipHost, Date lastUpdateForIPHost, String dotNetVer,
            Date lastUpdateForDotNetVer, String oSVer, Date lastUpdateForOSVer, String ramSize,
            Date lastUpdateForRAMSize, String cpuHost, Date lastUpdateForCPUHost, Integer usedByModule,
            String readerName, String firmwareVer, Date lastUpdateForReader, Long version) {
        this.idOfHardwareSetting = idOfHardwareSetting;
        this.moduleType = moduleType;
        this.installStatus = installStatus;
        this.lastUpdateForModuleType = lastUpdateForModuleType;
        this.ipHost = ipHost;
        this.lastUpdateForIPHost = lastUpdateForIPHost;
        this.dotNetVer = dotNetVer;
        this.lastUpdateForDotNetVer = lastUpdateForDotNetVer;
        this.oSVer = oSVer;
        this.lastUpdateForOSVer = lastUpdateForOSVer;
        this.ramSize = ramSize;
        this.lastUpdateForRAMSize = lastUpdateForRAMSize;
        this.cpuHost = cpuHost;
        this.lastUpdateForCPUHost = lastUpdateForCPUHost;
        this.usedByModule = usedByModule;
        this.readerName = readerName;
        this.firmwareVer = firmwareVer;
        this.lastUpdateForReader = lastUpdateForReader;
        this.version = version;
    }

    @Override
    public String toString() {
        return "HardwareSettingsRequest{" + "idOfHardwareSetting=" + idOfHardwareSetting + ", moduleType" + moduleType
                + ", installStatus" + installStatus + ", lastUpdateForModuleType" + lastUpdateForModuleType + ", ipHost"
                + ipHost + ", lastUpdateForIpHost" + lastUpdateForIPHost + ", dotNetVer" + dotNetVer
                + ", lastUpdateForDotNetVer" + ", oSVer" + oSVer + ", lastUpdateForOSVer" + lastUpdateForOSVer
                + ", ramSize" + ramSize + ", lastUpdateForRamSize" + lastUpdateForRAMSize + ", cpuHost" + cpuHost
                + ", lastUpdateForCPUHost+ " + lastUpdateForCPUHost + ", usedByModule" + usedByModule + ", readerName"
                + readerName + ", firmwareVer" + firmwareVer + ", lastUpdateForReader" + lastUpdateForReader
                + ",version" + version + ", idOfOrg='" + org.getIdOfOrg() + '}';
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

    public Integer getUsedByModule() {
        return usedByModule;
    }

    public void setUsedByModule(Integer usedByModule) {
        this.usedByModule = usedByModule;
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

    public Date getLastUpdateForReader() {
        return lastUpdateForReader;
    }

    public void setLastUpdateForReader(Date lastUpdateForReader) {
        this.lastUpdateForReader = lastUpdateForReader;
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
