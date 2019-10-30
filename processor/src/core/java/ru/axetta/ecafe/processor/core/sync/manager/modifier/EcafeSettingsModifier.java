/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.manager.modifier;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SettingValueParser;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSetting;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingDAOUtils;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingGroup;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingItem;

import org.hibernate.Session;
import org.w3c.dom.Document;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by i.semenov on 29.10.2019.
 */
public class EcafeSettingsModifier extends CommonModifier {

    public void modifyDO(Session persistenceSession, DistributedObject distributedObject,
            Long currentMaxVersion, DistributedObject currentDO, Long idOfOrg, Document conflictDocument) throws Exception {

        if (((ECafeSettings) distributedObject).isPreOrderFeeding()) {
            //Не принимаем изменения от арма для типа PreOrderFeeding (issue_314)
            currentDO.setGlobalVersion(currentMaxVersion);
            currentDO.setTagName("M");
            currentDO.preProcess(persistenceSession, idOfOrg);
            currentDO.updateVersionFromParent(persistenceSession);
            persistenceSession.update(currentDO);
            distributedObject.setGlobalVersion(currentMaxVersion);
            distributedObject.setTagName("M");
        } else {
            //для остальных типов ECafeSettings стандартная логика + доп по OrgSetting
            super.modifyDO(persistenceSession, distributedObject, currentMaxVersion, currentDO, idOfOrg, conflictDocument);
            updateOrgSettingByECafeSetting(persistenceSession, (ECafeSettings) distributedObject);
        }

    }

    private void updateOrgSettingByECafeSetting(Session persistenceSession, ECafeSettings eCafeSettings) throws Exception {
        Date now = new Date();
        Long lastVersionOfOrgSetting = OrgSettingDAOUtils.getLastVersionOfOrgSettings(persistenceSession);
        Long lastVersionOfOrgSettingItem = OrgSettingDAOUtils.getLastVersionOfOrgSettingsItem(persistenceSession);

        Long nextVersionOfOrgSetting = (lastVersionOfOrgSetting == null ? 0L : lastVersionOfOrgSetting) + 1L;
        Long nextVersionOfOrgSettingItem = (lastVersionOfOrgSettingItem == null ? 0L : lastVersionOfOrgSettingItem) + 1L;

        OrgSetting setting = OrgSettingDAOUtils.getOrgSettingByGroupIdAndOrg(persistenceSession,
                eCafeSettings.getSettingsId().getId() + OrgSettingGroup.OFFSET_IN_RELATION_TO_ECAFESETTING, eCafeSettings.getOrgOwner().intValue());
        if(setting == null){
            setting = new OrgSetting();
            setting.setCreatedDate(now);
            setting.setIdOfOrg(eCafeSettings.getOrgOwner());
            setting.setSettingGroup(OrgSettingGroup.getGroupById(eCafeSettings.getSettingsId().getId() + OrgSettingGroup.OFFSET_IN_RELATION_TO_ECAFESETTING));
            setting.setLastUpdate(now);
            setting.setVersion(nextVersionOfOrgSetting);
            persistenceSession.save(setting);
        } else {
            setting.setLastUpdate(now);
            setting.setVersion(nextVersionOfOrgSetting);
        }

        SettingValueParser valueParser = new SettingValueParser(eCafeSettings.getSettingValue(), eCafeSettings.getSettingsId());
        Set<OrgSettingItem> itemsFromECafeSetting = valueParser.getParserBySettingValue().buildSetOfOrgSettingItem(setting, nextVersionOfOrgSetting);
        Map<Integer, OrgSettingItem> itemsFromOrgSetting = buildHashMap(setting.getOrgSettingItems());

        for(OrgSettingItem item : itemsFromECafeSetting){
            if(!itemsFromOrgSetting.containsKey(item.getSettingType())){
                setting.getOrgSettingItems().add(item);
                persistenceSession.persist(item);
            } else {
                OrgSettingItem orgSettingItem = itemsFromOrgSetting.get(item.getSettingType());
                orgSettingItem.setLastUpdate(now);
                orgSettingItem.setVersion(nextVersionOfOrgSettingItem);
                orgSettingItem.setSettingValue(item.getSettingValue());
                persistenceSession.persist(orgSettingItem);
            }
        }
        persistenceSession.persist(setting);
    }

    private Map<Integer, OrgSettingItem> buildHashMap(Set<OrgSettingItem> orgSettingItems) {
        Map<Integer, OrgSettingItem> map = new HashMap<>();
        for(OrgSettingItem item : orgSettingItems){
            map.put(item.getSettingType(), item);
        }
        return map;
    }
}
