/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes;

public interface SettingType {

    Integer getSettingGroupId();

    Integer getId();

    Class getExpectedClass();

    Boolean validateSettingValue(Object value);
}
