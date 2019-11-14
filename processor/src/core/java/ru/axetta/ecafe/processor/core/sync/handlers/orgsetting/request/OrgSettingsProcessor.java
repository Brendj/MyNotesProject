/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.daoservices.org.SettingService;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SettingsIds;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSetting;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingDAOUtils;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingGroup;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingItem;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes.SettingType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrgSettingsProcessor extends AbstractProcessor<OrgSettingSection> {
    private final OrgSettingsRequest orgSettingsRequest;
    private static final Logger logger = LoggerFactory.getLogger(OrgSettingsProcessor.class);

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
            boolean isChanged = false;
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
                isChanged = true;
            } else if (setting.getVersion() <= maxVersionFromARM) {
                Map<Integer, OrgSettingItem> orgSettingItemMap = buildMap(setting);
                for (OrgSettingItemSyncPOJO itemSyncPOJO : pojo.getItems()) {
                    OrgSettingItem settingItem = orgSettingItemMap.get(itemSyncPOJO.getGlobalID());
                    if (settingItem == null) {
                        settingItem = buildFromPOJO(nextVersionOfOrgSettingItem, itemSyncPOJO, syncData, setting);
                        setting.getOrgSettingItems().add(settingItem);
                        isChanged = true;
                    } else if (settingItem.getVersion() <= itemSyncPOJO.getVersion()
                            && !settingItem.getSettingValue().equals(itemSyncPOJO.getValue())) {
                        if (!setting.isPreOrderFeeding()) {
                            //Не принимаем изменения от арма для типа PreOrderFeeding
                            settingItem.setLastUpdate(syncData);
                            settingItem.setSettingValue(itemSyncPOJO.getValue());
                        }
                        settingItem.setVersion(nextVersionOfOrgSettingItem);
                        isChanged = true;
                    }
                }
            }

            if(isChanged) {
                setting.setVersion(nextVersionOfOrgSetting);
                if (!setting.isPreOrderFeeding()) {
                    //Не принимаем изменения от арма для типа PreOrderFeeding (issue_314)
                    setting.setLastUpdate(syncData);
                    updateECafeSettingByOrgSetting(setting);
                }
                session.persist(setting);
            }
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
                    itemSyncPOJO.setType(-1);
                }
                settingPojo.getItems().add(itemSyncPOJO);
            }
            section.getItems().add(settingPojo);
        }
        return section;
    }

    private void updateECafeSettingByOrgSetting(OrgSetting setting) {
        if(setting.getSettingGroup().getId() > 8 || setting.getSettingGroup().getId() == 0){
            // unsupported types
            return;
        }
        SettingService settingService = RuntimeContext.getAppContext().getBean(SettingService.class);
        Long DOVersion = settingService.updateAndGetDOVersion();

        ECafeSettings eCafeSettings = DAOUtils.getECafeSettingByIdOfOrgAndSettingId(session, setting.getIdOfOrg(),
                SettingsIds.fromInteger(
                        setting.getSettingGroup().getId() - OrgSettingGroup.OFFSET_IN_RELATION_TO_ECAFESETTING));
        if (eCafeSettings == null) {
            eCafeSettings = new ECafeSettings();
            eCafeSettings.setOrgOwner(setting.getIdOfOrg());
            eCafeSettings.setSettingsId(SettingsIds.fromInteger(
                    setting.getSettingGroup().getId() - OrgSettingGroup.OFFSET_IN_RELATION_TO_ECAFESETTING));
            setDefaultValue(eCafeSettings);
            eCafeSettings.setDeletedState(false);
            eCafeSettings.setCreatedDate(new Date());
            eCafeSettings.setLastUpdate(new Date());
            eCafeSettings.setGlobalVersionOnCreate(DOVersion);
            eCafeSettings.setSendAll(SendToAssociatedOrgs.SendToSelf);
        }
        try {
            String[] newValue = eCafeSettings.getSplitSettingValue().buildChangedValueByOrgSetting(setting);
            eCafeSettings.setSettingValue(buildECafeSettingValue(newValue));
            eCafeSettings.setGlobalVersion(DOVersion);

            session.persist(eCafeSettings);
        } catch (Exception e) {
            logger.error(String.format("Can't update or create ECafeSetting with SettingID %d for Org ID %d :",
                    eCafeSettings.getSettingsId().getId(), setting.getIdOfOrg()), e);
        }
    }

    private void setDefaultValue(ECafeSettings eCafeSettings) {
        switch (eCafeSettings.getSettingsId().getId()){
            case 0: eCafeSettings.setSettingValue("Microsoft XPS Document Writer;42;1;19;3;10;10;Спасибо;"); break;
            case 1: eCafeSettings.setSettingValue("Microsoft XPS Document Writer;42;1;22;6;12;Спасибо;"); break;
            case 2: eCafeSettings.setSettingValue("Microsoft XPS Document Writer;42;1;16;12;12;Спасибо;"); break;
            case 3: eCafeSettings.setSettingValue("0;0:00;100;");break;
            case 4: eCafeSettings.setSettingValue("5;2;0;2;");break;
            case 5: eCafeSettings.setSettingValue("Резерв;2;");break;
            case 6: eCafeSettings.setSettingValue("1;1;");break;
            case 7: eCafeSettings.setSettingValue("0;0:00;"); break;
        }
    }

    private String buildECafeSettingValue(String[] splitValue){
        if(splitValue == null || splitValue.length == 0){
            throw new RuntimeException("Get empty ECafeSetting value array");
        }
        // array.length * 2: параметры в основном булевые + символ-сепаратор
        StringBuilder sb = new StringBuilder(splitValue.length * 2);
        for(String item : splitValue){
            if(item != null) {
                sb.append(item);
            }
            sb.append(';');
        }
        return sb.toString();
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
