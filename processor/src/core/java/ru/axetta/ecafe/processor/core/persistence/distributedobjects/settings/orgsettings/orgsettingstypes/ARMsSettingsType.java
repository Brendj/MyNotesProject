/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.orgsettings.orgsettingstypes;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.orgsettings.OrgSettingGroup;

import java.util.HashMap;
import java.util.Map;

public enum ARMsSettingsType implements SettingType {
    CARD_DUPLICATE_ENABLED(0,"Дубликаты для основных карт"), // Bool
    REVERSE_MONTH_OF_SALE(1,"Оплата/сторнирование месяц продажи"); // Bool

    private Integer id;
    private String description;

    private static Map<Integer, OrgSettingGroup> mapInt = new HashMap<Integer,OrgSettingGroup>();
    private static Map<String,OrgSettingGroup> mapStr = new HashMap<String,OrgSettingGroup>();
    static {
        for (OrgSettingGroup questionaryStatus : OrgSettingGroup.values()) {
            mapInt.put(questionaryStatus.getId(), questionaryStatus);
            mapStr.put(questionaryStatus.toString(), questionaryStatus);
        }
    }

    ARMsSettingsType(Integer id, String description) {
        this.id = id;
        this.description = description;
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
}
