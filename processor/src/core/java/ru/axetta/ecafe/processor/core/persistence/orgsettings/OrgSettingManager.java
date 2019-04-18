/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes.SettingType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class OrgSettingManager {
    private Logger logger = LoggerFactory.getLogger(OrgSettingManager.class);

    private static Long DEFAULT_VERSION = 1L;

    public String getSettingValueFromOrg(Org org, SettingType settingType) {
        Integer settingGroup = settingType.getSettingGroupId();
        Integer settingTypeId = settingType.getId();

        OrgSetting targetSetting = getTargetSetting(org, settingGroup);

        if(targetSetting != null && targetSetting.getOrgSettingItems() != null) {
            for (OrgSettingItem item : targetSetting.getOrgSettingItems()) {
                if (item.getSettingType().equals(settingTypeId)) {
                    return item.getSettingValue();
                }
            }
        }
        return "";
    }

    public void createOrUpdateOrgSettingValue(Org org, SettingType settingType, String value, Session session)throws Exception{
        Integer settingGroup = settingType.getSettingGroupId();
        Integer settingTypeId = settingType.getId();
        try {
            Long lastVersionOfOrgSetting = DAOUtils.getLastVersionOfOrgSettings(session);
            Long lastVersionOfOrgSettingItem = DAOUtils.getLastVersionOfOrgSettingsItem(session);

            OrgSetting targetGroup = getTargetSetting(org, settingGroup);

            if (targetGroup == null) {
                targetGroup = new OrgSetting();
                targetGroup.setCreatedDate(new Date());
                targetGroup.setIdOfOrg(org.getIdOfOrg());
                targetGroup.setLastUpdate(new Date());
                targetGroup.setVersion(lastVersionOfOrgSetting == null ? DEFAULT_VERSION : lastVersionOfOrgSetting + 1L);
                targetGroup.setSettingGroup(OrgSettingGroup.getGroupById(settingGroup));
                session.save(targetGroup);
                org.getOrgSettings().add(targetGroup);
            } else {
                targetGroup.setLastUpdate(new Date());
                targetGroup.setVersion(lastVersionOfOrgSetting == null ? DEFAULT_VERSION : lastVersionOfOrgSetting + 1L);
                session.update(targetGroup);
            }

            OrgSettingItem targetItem = null;
            if(targetGroup.getOrgSettingItems() != null) {
                for (OrgSettingItem item : targetGroup.getOrgSettingItems()) {
                    if (item.getSettingType().equals(settingTypeId)) {
                        targetItem = item;
                    }
                }
            }

            if (targetItem == null) {
                targetItem = new OrgSettingItem();
                targetItem.setCreatedDate(new Date());
                targetItem.setLastUpdate(new Date());
                targetItem.setOrgSetting(targetGroup);
                targetItem.setSettingType(settingTypeId);
                targetItem.setVersion(lastVersionOfOrgSettingItem == null ? DEFAULT_VERSION : lastVersionOfOrgSettingItem + 1L);
                targetItem.setSettingValue(value);
                session.save(targetItem);
                targetGroup.getOrgSettingItems().add(targetItem);
            } else {
                targetItem.setLastUpdate(new Date());
                targetItem.setVersion(lastVersionOfOrgSettingItem == null ? DEFAULT_VERSION : lastVersionOfOrgSettingItem + 1L);
                targetItem.setSettingValue(value);
                session.update(targetItem);
            }
        } catch (Exception e){
            logger.error("Exception when try set value \"" + value + "\" for setting Group: " + settingGroup + ",setting item type: " + settingTypeId, e);
            throw e;
        }
    }

    private OrgSetting getTargetSetting(Org org, Integer settingId){
        for(OrgSetting setting : org.getOrgSettings()){
            if(setting.getSettingGroup().getId().equals(settingId)){
                return setting;
            }
        }
        return null;
    }
}
