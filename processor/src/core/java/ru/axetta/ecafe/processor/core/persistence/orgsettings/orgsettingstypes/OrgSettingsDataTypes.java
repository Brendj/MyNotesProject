/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes;

public enum OrgSettingsDataTypes {
    STRING(String.class),
    INT32(Integer.class),
    BOOLEAN(Boolean.class);

    private Class dataType;

    OrgSettingsDataTypes(Class c){
        dataType = c;
    }

    public Boolean validateSettingValue(Object o){
        return dataType.isInstance(o);
    }

    public Class getDataType() {
        return dataType;
    }
}
