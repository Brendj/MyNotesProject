/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingGroup;

import java.util.HashMap;
import java.util.Map;

public enum PreOrderAutoPayType implements SettingType {

    PREORDER_AUTO_PAYMENT_ENABLED_FLAG(10303, "Булевое значение ввкл выкл", OrgSettingsDataTypes.BOOLEAN),
    PREORDER_AUTO_PAYMENT_RESPONSE_TIME(10304, "Время срабатывания автооплаты", OrgSettingsDataTypes.STRING);

    PreOrderAutoPayType(Integer globalId, String description, OrgSettingsDataTypes expectedClass){
        this.globalId = globalId;
        this.description = description;
        this.expectedClass = expectedClass;
    }

    private Integer globalId;
    private String description;
    private OrgSettingsDataTypes expectedClass;

    private static final Map<Integer, SettingType> mapInt = new HashMap<>();
    private static final BiMap<Integer, Integer> eCafeSettingIndexGlobalIdMap = HashBiMap.create();

    static {
        for (SettingType orgSettingGroup : SubscriberFeedingType.values()) {
            mapInt.put(orgSettingGroup.getId(), orgSettingGroup);
        }
        /* Индекс настройки из ECafeSetting сопоставляется с GlobalId  OrgSettingsItem */
        eCafeSettingIndexGlobalIdMap.put(0, PREORDER_AUTO_PAYMENT_ENABLED_FLAG.globalId);
        eCafeSettingIndexGlobalIdMap.put(1, PREORDER_AUTO_PAYMENT_RESPONSE_TIME.globalId);
    }

    public static Integer getGlobalIdByECafeSettingValueIndex(Integer index) {
        return eCafeSettingIndexGlobalIdMap.containsKey(index) ? eCafeSettingIndexGlobalIdMap.get(index) : index;
    }

    public static Integer getECafeSettingValueIndexByGlobalId(Integer globalId){
        BiMap<Integer, Integer> inverse =  eCafeSettingIndexGlobalIdMap.inverse();
        return inverse.containsKey(globalId) ? inverse.get(globalId) : globalId;
    }

    @Override
    public Integer getSettingGroupId() {
        return OrgSettingGroup.PreOrderAutoPay.getId();
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

    static public Map<Integer, SettingType> getSettingTypeAsMap() {
        return mapInt;
    }

    @Override
    public String toString(){
        return description;
    }
}
