/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingGroup;

import java.util.HashMap;
import java.util.Map;

public enum CardBalanceReportPrinterType implements SettingType {

    CARD_BALANCE_PRINTER_NAME(10701, "Название принтера", OrgSettingsDataTypes.STRING), // Microsoft XPS DocumentItem Writer
    CARD_BALANCE_TOTAL_TAPE_WIDTH(10702, "Общая ширина ленты принтера", OrgSettingsDataTypes.INT32), // возможные значения 42,48, по умолчанию 42
    CARD_BALANCE_COLUMN_SEPARATOR_WIDTH(10703, "Ширина разделителя между колонками", OrgSettingsDataTypes.INT32), // возможные значения 1,2,3, по умолчанию 1
    CARD_BALANCE_COLUMN_WIDTH_NAME(10704, "Ширина колонки наименование", OrgSettingsDataTypes.INT32), // определяется по формуле TOTAL_TAPE_WIDTH - COLUMN_SEPARATOR_WIDTH * 2 - COLUMN_WIDTH_CARD_NUMBER - COLUMN_WIDTH_BALANCE
    CARD_BALANCE_COLUMN_WIDTH_CARD_NUMBER(10705, "Ширина колонки номер карты", OrgSettingsDataTypes.INT32), // возможные значения 8,10,12,14, по умолчанию 12
    CARD_BALANCE_COLUMN_WIDTH_BALANCE(10706, "Ширина колонки баланс", OrgSettingsDataTypes.INT32), // возможные значения 7,8,9,10,11,12, по умолчанию 12
    CARD_BALANCE_TEXT_AREA(10707, "Текстовое поле выводимое на принтере", OrgSettingsDataTypes.INT32);

    CardBalanceReportPrinterType(Integer globalId, String description, OrgSettingsDataTypes expectedClass){
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
        eCafeSettingIndexGlobalIdMap.put(0, CARD_BALANCE_PRINTER_NAME.globalId);
        eCafeSettingIndexGlobalIdMap.put(1, CARD_BALANCE_TOTAL_TAPE_WIDTH.globalId);
        eCafeSettingIndexGlobalIdMap.put(2, CARD_BALANCE_COLUMN_SEPARATOR_WIDTH.globalId);
        eCafeSettingIndexGlobalIdMap.put(3, CARD_BALANCE_COLUMN_WIDTH_NAME.globalId);
        eCafeSettingIndexGlobalIdMap.put(4, CARD_BALANCE_COLUMN_WIDTH_CARD_NUMBER.globalId);
        eCafeSettingIndexGlobalIdMap.put(5, CARD_BALANCE_COLUMN_WIDTH_BALANCE.globalId);
        eCafeSettingIndexGlobalIdMap.put(6, CARD_BALANCE_TEXT_AREA.globalId);
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
        return OrgSettingGroup.CardBalanceReportPrinter.getId();
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
