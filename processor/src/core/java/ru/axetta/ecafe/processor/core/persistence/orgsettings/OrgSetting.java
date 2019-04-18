/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings;

import java.util.Date;
import java.util.Set;

/*
*  CREATE TABLE CF_OrgSettings(
*  idOfOrgSetting BIGSERIAL NOT NULL,
*  idOfOrg BIGINT NOT NULL,
*  createdDate BIGINT NOT NULL,
*  lastUpdate BIGINT NOT NULL,
*  settingGroup INTEGER NOT NULL,
*  version BIGINT NOT NULL,
*  CONSTRAINT cf_OrgSettings_pk PRIMARY KEY(idOfOrgSetting),
*  CONSTRAINT cf_OrgSettings_uk UNIQUE(idOfOrg, settingGroup), -- для одной ОО одна группа
*  CONSTRAINT cf_OrgSettings_org_fk FOREIGN KEY(idOfOrg)
*  REFERENCES cf_Orgs (idOfOrg) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE CASCADE
*  );
*
*  CREATE INDEX CF_OrgSettings_GROUP_ORG_IDX ON CF_OrgSettings USING btree (idOfOrg, settingGroup);
* */

public class OrgSetting {
    private Long idOfOrgSetting;
    private Long idOfOrg;
    private Date createdDate;
    private Date lastUpdate;
    private OrgSettingGroup settingGroup;
    private Set<OrgSettingItem> orgSettingItems;
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
        return this.idOfOrgSetting.equals(setting.idOfOrgSetting);
    }

    @Override
    public int hashCode(){
        return idOfOrgSetting.hashCode();
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
