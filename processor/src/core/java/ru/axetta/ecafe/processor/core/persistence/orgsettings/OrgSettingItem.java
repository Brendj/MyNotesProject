/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings;

import java.util.Date;

public class OrgSettingItem {
    private Long idOfOrgSettingItem;
    private OrgSetting orgSetting;
    private Date createdDate;
    private Date lastUpdate;
    private Integer settingType;
    private String settingValue;
    private Long version;

    public Long getIdOfOrgSettingItem() {
        return idOfOrgSettingItem;
    }

    public void setIdOfOrgSettingItem(Long idOfOrgSettingItem) {
        this.idOfOrgSettingItem = idOfOrgSettingItem;
    }

    public OrgSetting getOrgSetting() {
        return orgSetting;
    }

    public void setOrgSetting(OrgSetting orgSetting) {
        this.orgSetting = orgSetting;
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

    public Integer getSettingType() {
        return settingType;
    }

    public void setSettingType(Integer settingType) {
        this.settingType = settingType;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrgSettingItem)){
            return false;
        }
        OrgSettingItem setting = (OrgSettingItem) o;
        return this.settingType.equals(setting.settingType);
    }

    @Override
    public int hashCode(){
        return settingType.hashCode();
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
