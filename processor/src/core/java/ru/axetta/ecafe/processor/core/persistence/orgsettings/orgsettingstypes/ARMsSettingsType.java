/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes;

import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingGroup;

import java.util.HashMap;
import java.util.Map;

public enum ARMsSettingsType implements SettingType {
    CARD_DUPLICATE_ENABLED(0,"Дубликаты для основных карт", Boolean.class),
    REVERSE_MONTH_OF_SALE(1,"Оплата/сторнирование месяц продажи", Boolean.class);

    private Integer id;
    private String description;
    private Class expectedClass;

    private static Map<Integer, OrgSettingGroup> mapInt = new HashMap<Integer,OrgSettingGroup>();
    private static Map<String,OrgSettingGroup> mapStr = new HashMap<String,OrgSettingGroup>();
    static {
        for (OrgSettingGroup orgSettingGroup : OrgSettingGroup.values()) {
            mapInt.put(orgSettingGroup.getId(), orgSettingGroup);
            mapStr.put(orgSettingGroup.toString(), orgSettingGroup);
        }
    }

    ARMsSettingsType(Integer id, String description, Class expectedClass) {
        this.id = id;
        this.description = description;
        this.expectedClass = expectedClass;
    }

    @Override
    public Integer getId() {
        return id;
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
        return expectedClass;
    }

    @Override
    public Boolean validateSettingValue(Object value){
        return expectedClass.isInstance(value);
    }
}
