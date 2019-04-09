/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.orgsettings;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.orgsettings.orgsettingstypes.SettingType;

public class OrgSettingManager {

    public static String getSettingValueFromOrg(Org org, SettingType settingType) {
        Integer settingGroup = settingType.getSettingGroupId();
        Integer settingTypeId = settingType.getId();

        OrgSetting targetSetting = null;
        for(OrgSetting setting : org.getOrgSettings()){
            if(setting.getSettingGroup().getId().equals(settingGroup)){
                targetSetting = setting;
                break;
            }
        }

        if(targetSetting != null) {
            for (OrgSettingItem item : targetSetting.getOrgSettingItems()) {
                if (item.getSettingType().equals(settingTypeId)) {
                    return item.getSettingValue();
                }
            }
        }
        return "";
    }
}
