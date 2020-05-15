/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class HardwareSettings implements Serializable {

    private Set<HardwareSettingsMT> moduleTypes = new HashSet<HardwareSettingsMT>();
    private Set<HardwareSettingsReaders> readers = new HashSet<HardwareSettingsReaders>();
    private Org org;
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
    private Long version;
    private CompositeIdOfHardwareSettings compositeIdOfHardwareSettings;

    public HardwareSettings() {
    }

    //@Override
    //public String toString() {
    //    return "HardwareSettingsRequest{" + "idOfHardwareSetting=" + idOfHardwareSetting + ", ipHost" + ipHost
    //            + ", lastUpdateForIpHost" + lastUpdateForIPHost + ", dotNetVer" + dotNetVer + ", lastUpdateForDotNetVer"
    //            + ", oSVer" + oSVer + ", lastUpdateForOSVer" + lastUpdateForOSVer + ", ramSize" + ramSize
    //            + ", lastUpdateForRamSize" + lastUpdateForRAMSize + ", cpuHost" + cpuHost + ", lastUpdateForCPUHost+ "
    //            + lastUpdateForCPUHost + ",version" + version + ", idOfOrg='" + org.getIdOfOrg() + '}';
    //}

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Set<HardwareSettingsMT> getModuleTypes() {
        return moduleTypes;
    }

    public void setModuleTypes(Set<HardwareSettingsMT> moduleTypes) {
        this.moduleTypes = moduleTypes;
    }

    public Set<HardwareSettingsReaders> getReaders() {
        return readers;
    }

    public void setReaders(Set<HardwareSettingsReaders> readers) {
        this.readers = readers;
    }

    public CompositeIdOfHardwareSettings getCompositeIdOfHardwareSettings() {
        return compositeIdOfHardwareSettings;
    }

    public void setCompositeIdOfHardwareSettings(CompositeIdOfHardwareSettings compositeIdOfHardwareSettings) {
        this.compositeIdOfHardwareSettings = compositeIdOfHardwareSettings;
    }
}
