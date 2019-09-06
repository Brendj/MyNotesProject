/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingGroup;

import java.util.HashMap;
import java.util.Map;

public enum AutoPlanPaymentSettingType implements SettingType {

    AUTO_PAYMENT_ENABLED_FLAG(10401, "Булевое значение ввкл выкл", OrgSettingsDataTypes.BOOLEAN),
    AUTO_PAYMENT_PAY_TIME(10402, "Время оплаты", OrgSettingsDataTypes.STRING),
    AUTO_PAYMENT_TRIGGER_THRESHOLD(10403, "Порог срабатывания (от 0 до 100)", OrgSettingsDataTypes.INT32);

    AutoPlanPaymentSettingType(Integer globalId, String description, OrgSettingsDataTypes expectedClass){
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
        eCafeSettingIndexGlobalIdMap.put(0, AUTO_PAYMENT_ENABLED_FLAG.globalId);
        eCafeSettingIndexGlobalIdMap.put(1, AUTO_PAYMENT_PAY_TIME.globalId);
        eCafeSettingIndexGlobalIdMap.put(2, AUTO_PAYMENT_TRIGGER_THRESHOLD.globalId);
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
        return OrgSettingGroup.AutoPlanPaymentSetting.getId();
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
