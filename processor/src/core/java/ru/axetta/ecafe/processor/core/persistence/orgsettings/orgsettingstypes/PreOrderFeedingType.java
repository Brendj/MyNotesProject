/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingGroup;

import java.util.HashMap;
import java.util.Map;

public enum PreOrderFeedingType implements SettingType {

    DAYS_ON_WHICH_APPLICATIONS_ARE_MADE(10301, "Предзаказ. Кол-во раб. дней, на которые оформляются заявки", OrgSettingsDataTypes.INT32),
    DAYS_WHEN_BLOCKED_EDITION_ON_WEBSITE(10302,"Предзаказ. Кол-во дней, запрещенных к редактированию на сайте", OrgSettingsDataTypes.INT32);

    private Integer globalId;
    private String description;
    private OrgSettingsDataTypes expectedClass;

    private static Map<Integer, SettingType> mapInt = new HashMap<Integer,SettingType>();
    private static BiMap<Integer, Integer> eCafeSettingIndexGlobalIdMap = HashBiMap.create();

    static {
        for (SettingType orgSettingGroup : PreOrderFeedingType.values()) {
            mapInt.put(orgSettingGroup.getId(), orgSettingGroup);
        }
        /* Индекс настройки из ECafeSetting сопоставляется с GlobalId  OrgSettingsItem */
        eCafeSettingIndexGlobalIdMap.put(0, DAYS_ON_WHICH_APPLICATIONS_ARE_MADE.globalId);
        eCafeSettingIndexGlobalIdMap.put(1, DAYS_WHEN_BLOCKED_EDITION_ON_WEBSITE.globalId);
    }

    PreOrderFeedingType(Integer globalId, String description, OrgSettingsDataTypes expectedClass) {
        this.globalId = globalId;
        this.description = description;
        this.expectedClass = expectedClass;
    }

    @Override
    public Integer getSettingGroupId() {
        return OrgSettingGroup.SubscriberFeeding.getId();
    }

    @Override
    public Integer getId() {
        return globalId;
    }

    @Override
    public Class getExpectedClass() {
        return expectedClass.getDataType();
    }

    @Override
    public Boolean validateSettingValue(Object value) {
        return expectedClass.validateSettingValue(value);
    }

    @Override
    public Integer getSyncDataTypeId() {
        return expectedClass.ordinal();
    }

    public static Integer getGlobalIdByECafeSettingValueIndex(Integer index) {
        return eCafeSettingIndexGlobalIdMap.containsKey(index) ? eCafeSettingIndexGlobalIdMap.get(index) : index;
    }

    public static Integer getECafeSettingValueIndexByGlobalId(Integer globalId){
        BiMap<Integer, Integer> inverse =  eCafeSettingIndexGlobalIdMap.inverse();
        return inverse.containsKey(globalId) ?inverse.get(globalId) : globalId;
    }

    static public Map<Integer, SettingType> getSettingTypeAsMap() {
        return mapInt;
    }

    @Override
    public String toString(){
        return description;
    }
}
