/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingGroup;

import java.util.HashMap;
import java.util.Map;

public enum SalesReportPrinterType implements SettingType {

    SALES_REPORT_PRINTER_NAME(10730, "Название принтера", OrgSettingsDataTypes.STRING), // Microsoft XPS Document Writer
    SALES_REPORT_TOTAL_TAPE_WIDTH(10731, "Общая ширина ленты принтера", OrgSettingsDataTypes.INT32), // возможные значения 42,48, по умолчанию 42
    SALES_REPORT_COLUMN_SEPARATOR_WIDTH(10732, "Ширина разделителя между колонками", OrgSettingsDataTypes.INT32), // возможные значения 1,2,3, по умолчанию 1
    SALES_REPORT_COLUMN_WIDTH_NAME(10733, "Ширина колонки наименование", OrgSettingsDataTypes.INT32), // определяется по формуле TOTAL_TAPE_WIDTH - COLUMN_SEPARATOR_WIDTH * 2 - COLUMN_WIDTH_QTY - COLUMN_WIDTH_COST
    SALES_REPORT_COLUMN_WIDTH_QTY(10734, "Ширина колонки количество", OrgSettingsDataTypes.INT32), // возможные значения 6,7,8, по умолчанию 6
    SALES_REPORT_COLUMN_WIDTH_COST(10735, "Ширина колонки баланс", OrgSettingsDataTypes.INT32), // возможные значения 7,8,9,10,11,12, по умолчанию 12
    SALES_REPORT_TEXT_AREA(10736, "Текстовое поле выводимое на принтере", OrgSettingsDataTypes.INT32);

    SalesReportPrinterType(Integer globalId, String description, OrgSettingsDataTypes expectedClass){
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
        eCafeSettingIndexGlobalIdMap.put(0, SALES_REPORT_PRINTER_NAME.globalId);
        eCafeSettingIndexGlobalIdMap.put(1, SALES_REPORT_TOTAL_TAPE_WIDTH.globalId);
        eCafeSettingIndexGlobalIdMap.put(2, SALES_REPORT_COLUMN_SEPARATOR_WIDTH.globalId);
        eCafeSettingIndexGlobalIdMap.put(3, SALES_REPORT_COLUMN_WIDTH_NAME.globalId);
        eCafeSettingIndexGlobalIdMap.put(4, SALES_REPORT_COLUMN_WIDTH_QTY.globalId);
        eCafeSettingIndexGlobalIdMap.put(5, SALES_REPORT_COLUMN_WIDTH_COST.globalId);
        eCafeSettingIndexGlobalIdMap.put(6, SALES_REPORT_TEXT_AREA.globalId);
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
        return OrgSettingGroup.SalesReportPrinter.getId();
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
