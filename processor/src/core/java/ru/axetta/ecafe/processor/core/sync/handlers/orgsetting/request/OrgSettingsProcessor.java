/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request;

import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSetting;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingDAOUtils;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingItem;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.hibernate.Session;

import java.util.List;

public class OrgSettingsProcessor extends AbstractProcessor<OrgSettingSection> {
    private final OrgSettingsRequest orgSettingsRequest;

    public OrgSettingsProcessor(Session session, OrgSettingsRequest orgSettingsRequest) {
        super(session);
        this.orgSettingsRequest = orgSettingsRequest;
    }

    @Override
    public OrgSettingSection process() throws Exception {
        OrgSettingSection section = new OrgSettingSection();
        Long maxVersionFromARM = orgSettingsRequest.getMaxVersion();
        Long idOfOrg = orgSettingsRequest.getIdOfOrgSource();

        Long maxVersionFromDB = OrgSettingDAOUtils.getMaxVersionOfOrgSettingForFriendlyOrgGroup(idOfOrg, session);
        List<OrgSetting> settingsFromDB = OrgSettingDAOUtils.getOrgSettingsForAllFriendlyOrgByIdOfOrgAndMaxVersion(idOfOrg, maxVersionFromARM, session);

        section.setMaxVersion(maxVersionFromDB);
        for(OrgSetting settingFromDB : settingsFromDB){
            OrgSettingSyncPOJO settingPojo = new OrgSettingSyncPOJO();
            settingPojo.setIdOfOrg(settingFromDB.getIdOfOrg().intValue());
            settingPojo.setGroupID(settingFromDB.getSettingGroup().getId());
            for(OrgSettingItem settingItem : settingFromDB.getOrgSettingItems()){
                OrgSettingItemSyncPOJO itemSyncPOJO = new OrgSettingItemSyncPOJO();
                itemSyncPOJO.setVersion(settingItem.getVersion());
                itemSyncPOJO.setValue(settingItem.getSettingValue());
                itemSyncPOJO.setGlobalID(settingItem.getSettingType());
                // TODO ADD TYPE
                settingPojo.getItems().add(itemSyncPOJO);
            }
            section.getItems().add(settingPojo);
        }
        return section;
    }
}
