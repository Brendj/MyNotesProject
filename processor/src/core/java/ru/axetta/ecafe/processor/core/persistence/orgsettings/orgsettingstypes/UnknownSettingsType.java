/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes;

import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingGroup;

import java.util.Collections;
import java.util.Map;

public class UnknownSettingsType implements SettingType {

    @Override
    public Integer getSettingGroupId() {
        return OrgSettingGroup.UnknownSetting.getId();
    }

    @Override
    public Integer getId() {
        return null;
    }

    @Override
    public Class getExpectedClass() {
        return null;
    }

    @Override
    public Boolean validateSettingValue(Object value) {
        return null;
    }

    @Override
    public Integer getSyncDataTypeId() {
        return null;
    }

    static public Map<Integer, SettingType> getSettingTypeAsMap() {
        return Collections.emptyMap();
    }
}
