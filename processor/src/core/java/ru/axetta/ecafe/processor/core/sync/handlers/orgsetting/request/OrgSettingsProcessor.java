/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request;

import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSetting;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingDAOUtils;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingGroup;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingItem;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes.SettingType;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.hibernate.Session;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrgSettingsProcessor extends AbstractProcessor<OrgSettingSection> {

    private final OrgSettingsRequest orgSettingsRequest;

    public OrgSettingsProcessor(Session session, OrgSettingsRequest orgSettingsRequest) {
        super(session);
        this.orgSettingsRequest = orgSettingsRequest;
    }

    @Override
    public OrgSettingSection process() throws Exception {
        Long maxVersionFromARM = orgSettingsRequest.getMaxVersion();
        Long idOfOrg = orgSettingsRequest.getIdOfOrgSource();
        Date syncData = new Date();
        Long nextVersionOfOrgSetting = OrgSettingDAOUtils.getNextVersionOfOrgSettings(session);
        Long nextVersionOfOrgSettingItem = OrgSettingDAOUtils.getNextVersionOfOrgSettingItem(session);

        //Apply change from ARM
        for (OrgSettingSyncPOJO pojo : orgSettingsRequest.getItems()) {
            OrgSetting setting = OrgSettingDAOUtils
                    .getOrgSettingByGroupIdAndOrg(session, pojo.getGroupID(), pojo.getIdOfOrg());
            if (setting == null) {
                setting = new OrgSetting();
                setting.setVersion(nextVersionOfOrgSetting);
                setting.setLastUpdate(syncData);
                setting.setCreatedDate(syncData);
                setting.setSettingGroup(OrgSettingGroup.getGroupById(pojo.getGroupID()));
                setting.setIdOfOrg(pojo.getIdOfOrg().longValue());
                for (OrgSettingItemSyncPOJO itemSyncPOJO : pojo.getItems()) {
                    OrgSettingItem item = buildFromPOJO(nextVersionOfOrgSettingItem, itemSyncPOJO, syncData, setting);
                    setting.getOrgSettingItems().add(item);
                }
            } else if (setting.getVersion() <= maxVersionFromARM) {
                Map<Integer, OrgSettingItem> orgSettingItemMap = buildMap(setting);
                for (OrgSettingItemSyncPOJO itemSyncPOJO : pojo.getItems()) {
                    OrgSettingItem settingItem = orgSettingItemMap.get(itemSyncPOJO.getGlobalID());
                    if (settingItem == null) {
                        settingItem = buildFromPOJO(nextVersionOfOrgSettingItem, itemSyncPOJO, syncData, setting);
                        setting.getOrgSettingItems().add(settingItem);
                    } else if (settingItem.getVersion() <= itemSyncPOJO.getVersion()) {
                        settingItem.setLastUpdate(syncData);
                        settingItem.setSettingValue(itemSyncPOJO.getValue());
                        settingItem.setVersion(nextVersionOfOrgSettingItem);
                    }
                }
                setting.setLastUpdate(syncData);
                setting.setVersion(nextVersionOfOrgSetting);
            }
            session.saveOrUpdate(setting);
        }

        //Build section for response
        OrgSettingSection section = new OrgSettingSection();
        Long maxVersionFromDB = OrgSettingDAOUtils.getMaxVersionOfOrgSettingForFriendlyOrgGroup(idOfOrg, session);
        List<OrgSetting> settingsFromDB = OrgSettingDAOUtils
                .getOrgSettingsForAllFriendlyOrgByIdOfOrgAndMaxVersion(idOfOrg, maxVersionFromARM, session);

        section.setMaxVersion(maxVersionFromDB);
        for (OrgSetting settingFromDB : settingsFromDB) {
            OrgSettingSyncPOJO settingPojo = new OrgSettingSyncPOJO();
            settingPojo.setIdOfOrg(settingFromDB.getIdOfOrg().intValue());
            settingPojo.setGroupID(settingFromDB.getSettingGroup().getId());
            for (OrgSettingItem settingItem : settingFromDB.getOrgSettingItems()) {
                SettingType typeForCurrentItem = OrgSettingGroup
                        .getSettingTypeByGroupIdAndGlobalId(settingFromDB.getSettingGroup().getId(),
                                settingItem.getSettingType());

                OrgSettingItemSyncPOJO itemSyncPOJO = new OrgSettingItemSyncPOJO();
                itemSyncPOJO.setVersion(settingItem.getVersion());
                itemSyncPOJO.setValue(settingItem.getSettingValue());
                itemSyncPOJO.setGlobalID(settingItem.getSettingType());

                if (typeForCurrentItem != null) {
                    itemSyncPOJO.setType(typeForCurrentItem.getSyncDataTypeId());
                } else {
                    itemSyncPOJO.setType(settingItem.getSettingType());
                }
                settingPojo.getItems().add(itemSyncPOJO);
            }
            section.getItems().add(settingPojo);
        }
        return section;
    }

    private OrgSettingItem buildFromPOJO(Long nextVersionOfOrgSettingItem, OrgSettingItemSyncPOJO itemSyncPOJO,
            Date syncData, OrgSetting setting) {
        OrgSettingItem settingItem = new OrgSettingItem();
        settingItem.setCreatedDate(syncData);
        settingItem.setLastUpdate(syncData);
        settingItem.setVersion(nextVersionOfOrgSettingItem);
        settingItem.setOrgSetting(setting);
        settingItem.setSettingType(itemSyncPOJO.getGlobalID());
        settingItem.setSettingValue(itemSyncPOJO.getValue());
        return settingItem;
    }

    private Map<Integer, OrgSettingItem> buildMap(OrgSetting setting) {
        Map<Integer, OrgSettingItem> result = new HashMap<>();
        for (OrgSettingItem item : setting.getOrgSettingItems()) {
            result.put(item.getSettingType(), item);
        }
        return result;
    }
}
