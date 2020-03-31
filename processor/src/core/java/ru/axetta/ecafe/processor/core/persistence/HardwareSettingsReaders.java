/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class HardwareSettingsReaders implements Serializable {

    private Long idOfReader;
    private Integer usedByModule;
    private String readerName;
    private String firmwareVer;
    private Date lastUpdateForReader;
    private HardwareSettings hardwareSettings;
    private Set<Org> orgsInternal = new HashSet<>();

    public HardwareSettingsReaders() {
    }

    public HardwareSettingsReaders(Integer usedByModule, String readerName, String firmwareVer,
            Date lastUpdateForReader, HardwareSettings hardwareSettings, Long idOfReader) {
        this.usedByModule = usedByModule;
        this.readerName = readerName;
        this.firmwareVer = firmwareVer;
        this.lastUpdateForReader = lastUpdateForReader;
        this.idOfReader = idOfReader;
        this.hardwareSettings = hardwareSettings;
    }

    public HardwareSettings getHardwareSettings() {
        return hardwareSettings;
    }

    public void setHardwareSettings(HardwareSettings hardwareSettings) {
        this.hardwareSettings = hardwareSettings;
    }

    public Long getIdOfReader() {
        return idOfReader;
    }

    public void setIdOfReader(Long idOfReader) {
        this.idOfReader = idOfReader;
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

    public Set<Org> getOrgsInternal() {
        return orgsInternal;
    }

    public void setOrgsInternal(Set<Org> orgsInternal) {
        this.orgsInternal = orgsInternal;
    }
}
