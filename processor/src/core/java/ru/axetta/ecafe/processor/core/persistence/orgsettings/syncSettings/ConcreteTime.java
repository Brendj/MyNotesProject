/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings;

public class ConcreteTime {
    private Long idOfSyncSettingsConcreteTime;
    private SyncSettings syncSettings;
    private String concreteTime;

    public Long getIdOfSyncSettingsConcreteTime() {
        return idOfSyncSettingsConcreteTime;
    }

    public void setIdOfSyncSettingsConcreteTime(Long idOfSyncSettingsConcreteTime) {
        this.idOfSyncSettingsConcreteTime = idOfSyncSettingsConcreteTime;
    }

    public SyncSettings getSyncSettings() {
        return syncSettings;
    }

    public void setSyncSettings(SyncSettings syncSettings) {
        this.syncSettings = syncSettings;
    }

    public String getConcreteTime() {
        return concreteTime;
    }

    public void setConcreteTime(String concreteTime) {
        this.concreteTime = concreteTime;
    }
}
