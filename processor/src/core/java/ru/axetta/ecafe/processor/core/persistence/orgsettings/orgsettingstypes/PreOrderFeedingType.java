/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes;

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

    static {
        for (SettingType orgSettingGroup : PreOrderFeedingType.values()) {
            mapInt.put(orgSettingGroup.getId(), orgSettingGroup);
        }
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

    static public Map<Integer, SettingType> getSettingTypeAsMap() {
        return mapInt;
    }

    @Override
    public String toString(){
        return description;
    }
}
