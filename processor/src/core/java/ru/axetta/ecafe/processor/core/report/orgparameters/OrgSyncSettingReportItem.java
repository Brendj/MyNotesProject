/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.orgparameters;

public class OrgSyncSettingReportItem {
    private String orgName;
    private Long idOfOrg;
    private String shortAddress;
    private SyncInfo fullSync;
    private SyncInfo accIncSync;
    private SyncInfo orgSettingSync;
    private SyncInfo menuSync;
    private SyncInfo photoSync;
    private SyncInfo helpRequestsSync;
    private SyncInfo libSync;

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getShortAddress() {
        return shortAddress;
    }

    public void setShortAddress(String shortAddress) {
        this.shortAddress = shortAddress;
    }

    public SyncInfo getFullSync() {
        return fullSync;
    }

    public void setFullSync(SyncInfo fullSync) {
        this.fullSync = fullSync;
    }

    public SyncInfo getAccIncSync() {
        return accIncSync;
    }

    public void setAccIncSync(SyncInfo accIncSync) {
        this.accIncSync = accIncSync;
    }

    public SyncInfo getOrgSettingSync() {
        return orgSettingSync;
    }

    public void setOrgSettingSync(SyncInfo orgSettingSync) {
        this.orgSettingSync = orgSettingSync;
    }

    public SyncInfo getMenuSync() {
        return menuSync;
    }

    public void setMenuSync(SyncInfo menuSync) {
        this.menuSync = menuSync;
    }

    public SyncInfo getPhotoSync() {
        return photoSync;
    }

    public void setPhotoSync(SyncInfo photoSync) {
        this.photoSync = photoSync;
    }

    public SyncInfo getHelpRequestsSync() {
        return helpRequestsSync;
    }

    public void setHelpRequestsSync(SyncInfo helpRequestsSync) {
        this.helpRequestsSync = helpRequestsSync;
    }

    public SyncInfo getLibSync() {
        return libSync;
    }

    public void setLibSync(SyncInfo libSync) {
        this.libSync = libSync;
    }

    private class SyncInfo {
        private String times;
        private String days;

        public String getTimes() {
            return times;
        }

        public void setTimes(String times) {
            this.times = times;
        }

        public String getDays() {
            return days;
        }

        public void setDays(String days) {
            this.days = days;
        }
    }

}
