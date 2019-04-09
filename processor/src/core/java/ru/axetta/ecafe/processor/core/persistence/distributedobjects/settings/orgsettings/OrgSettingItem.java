/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.orgsettings;

import java.util.Date;

/*
 *  CREATE TABLE CF_OrgSettingsItems(
 *  idOfOrgSettingItems BIGSERIAL NOT NULL,
 *  idOfOrgSetting BIGINT NOT NULL,
 *  createdDate BIGINT NOT NULL,
 *  lastUpdate BIGINT NOT NULL,
 *  settingType INTEGER NOT NULL,
 *  settingValue character varying(128),
 *  CONSTRAINT cf_OrgSettingsItems_pk PRIMARY KEY(idOfOrgSettingItem),
 *  CONSTRAINT cf_OrgSettingsItems_uk UNIQUE(idOfOrgSetting, settingType), -- для одной группы одна настройка
 *  CONSTRAINT cf_OrgSettingsItems_org_fk FOREIGN KEY(idOfOrgSetting),
 *  REFERENCES CF_OrgSettings (idOfOrgSetting) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE CASCADE
 *  );
 *
 *  CREATE INDEX CF_OrgSettingsItems_TYPE_GROUP_IDX ON CF_OrgSettingsItems USING btree (idOfOrgSetting, settingType);
 * */

public class OrgSettingItem {
    private Long idOfOrgSettingItem;
    private OrgSetting orgSetting;
    private Date createdDate;
    private Date lastUpdate;
    private Integer settingType;
    private String settingValue;

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
        return this.idOfOrgSettingItem.equals(setting.getIdOfOrgSettingItem());
    }

    @Override
    public int hashCode(){
        return idOfOrgSettingItem.hashCode();
    }
}
