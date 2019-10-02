/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class OrgSetting {
    private Long idOfOrgSetting;
    private Long idOfOrg;
    private Date createdDate;
    private Date lastUpdate;
    private OrgSettingGroup settingGroup;
    private Set<OrgSettingItem> orgSettingItems = new HashSet<>();
    private Long version;

    public Long getIdOfOrgSetting() {
        return idOfOrgSetting;
    }

    public void setIdOfOrgSetting(Long idOfOrgSetting) {
        this.idOfOrgSetting = idOfOrgSetting;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public OrgSettingGroup getSettingGroup() {
        return settingGroup;
    }

    public void setSettingGroup(OrgSettingGroup settingGroup) {
        this.settingGroup = settingGroup;
    }

    public Set<OrgSettingItem> getOrgSettingItems() {
        return orgSettingItems;
    }

    public void setOrgSettingItems(Set<OrgSettingItem> orgSettingItems) {
        this.orgSettingItems = orgSettingItems;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrgSetting)){
            return false;
        }
        OrgSetting setting = (OrgSetting) o;
        if(idOfOrgSetting != null) {
            return this.idOfOrgSetting.equals(setting.idOfOrgSetting);
        } else {
            return idOfOrg.equals(setting.idOfOrg) && settingGroup.getId().equals(setting.settingGroup.getId());
        }
    }

    @Override
    public int hashCode(){
        return idOfOrg.hashCode() + settingGroup.getId().hashCode();
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
