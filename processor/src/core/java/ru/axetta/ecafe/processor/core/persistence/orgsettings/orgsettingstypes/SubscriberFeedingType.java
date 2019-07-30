/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes;

import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingGroup;

import java.util.HashMap;
import java.util.Map;

public enum SubscriberFeedingType implements SettingType {

    AP_TIME_OF_CREATION_CANNOT_BE_EDITED(10104, "АП.Время после создания заявки, в которое нельзя редактировать заяку, в часах", OrgSettingsDataTypes.INT32),
    AP_DAYS_WHEN_BLOCKED_CASHBOX(10106,"АП.Кол-во раб. дней, на которое блокируются средства на кассах", OrgSettingsDataTypes.INT32),
    VP_DAYS_ON_WHICH_APPLICATIONS_ARE_MADE(10201, "ВП.Количество раб. дней, на которые оформляются заявки", OrgSettingsDataTypes.INT32),
    VP_TIME_OF_CREATION_CANNOT_BE_EDITED(10202, "ВП.Время после создания заявки, в которое нельзя редактировать заяку, в часах", OrgSettingsDataTypes.INT32);

    private Integer globalId;
    private String description;
    private OrgSettingsDataTypes expectedClass;

    private static Map<Integer, SettingType> mapInt = new HashMap<Integer,SettingType>();

    static {
        for (SettingType orgSettingGroup : SubscriberFeedingType.values()) {
            mapInt.put(orgSettingGroup.getId(), orgSettingGroup);
        }
    }

    SubscriberFeedingType(Integer globalId, String description, OrgSettingsDataTypes expectedClass) {
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
