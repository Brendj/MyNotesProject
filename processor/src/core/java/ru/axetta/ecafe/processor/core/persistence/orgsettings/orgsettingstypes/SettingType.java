/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes;

import com.sun.org.apache.xpath.internal.operations.Bool;

public interface SettingType {

    public Integer getSettingGroupId();

    public Integer getId();

    public Class getExpectedClass();

    public Boolean validateSettingValue(Object value);
}
