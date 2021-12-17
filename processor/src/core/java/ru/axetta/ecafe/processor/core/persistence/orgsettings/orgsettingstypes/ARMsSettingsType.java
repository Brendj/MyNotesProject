/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes;

import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingGroup;

import java.util.HashMap;
import java.util.Map;

public enum ARMsSettingsType implements SettingType {
    CARD_DUPLICATE_ENABLED(1300,"Дубликаты для основных карт", OrgSettingsDataTypes.BOOLEAN),
    REVERSE_MONTH_OF_SALE(1301,"Оплата/сторнирование месяц продажи", OrgSettingsDataTypes.BOOLEAN),
    LP_DAYS_ON_WHICH_APPLICATIONS_ARE_MADE(11001, "ЛП. Количество дней, запрещенных к редактированию на сайте, начиная с текущего", OrgSettingsDataTypes.INT32),
    USE_MEAL_SCHEDULE(1080, "Использовать расписание питания", OrgSettingsDataTypes.BOOLEAN);

    private Integer globalId;
    private String description;
    private OrgSettingsDataTypes expectedClass;

    private static Map<Integer, SettingType> mapInt = new HashMap<Integer,SettingType>();

    static {
        for (SettingType orgSettingGroup : ARMsSettingsType.values()) {
            mapInt.put(orgSettingGroup.getId(), orgSettingGroup);
        }
    }

    ARMsSettingsType(Integer globalId, String description, OrgSettingsDataTypes expectedClass) {
        this.globalId = globalId;
        this.description = description;
        this.expectedClass = expectedClass;
    }

    @Override
    public Integer getId() {
        return globalId;
    }

    @Override
    public Integer getSettingGroupId(){
        return OrgSettingGroup.ARMsSetting.getId();
    }

    @Override
    public String toString(){
        return description;
    }

    @Override
    public Class getExpectedClass() {
        return expectedClass.getDataType();
    }

    @Override
    public Boolean validateSettingValue(Object value){
        return expectedClass.validateSettingValue(value);
    }

    @Override
    public Integer getSyncDataTypeId() {
        return expectedClass.ordinal();
    }

    public static Integer getGlobalIdByECafeSettingValueIndex(Integer index) {
        return index;
    }

    public static Integer getECafeSettingValueIndexByGlobalId(Integer globalId){
        return globalId;
    }

    static public Map<Integer, SettingType> getSettingTypeAsMap() {
        return mapInt;
    }


}
