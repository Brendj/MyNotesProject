/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings;

import ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes.ARMsSettingsType;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes.SettingType;

import java.util.HashMap;
import java.util.Map;

/*
UPDATE cf_orgsettings
SET settingGroup = settingGroup + 1;
*/

public enum OrgSettingGroup {
    UnknownSetting(0, "Неизвестно", null),
    CashierCheckPrinter(1,"Настройки принтера кассового чека", null),
    SalesReportPrinter(2,"Настройка принтера отчета по продажам", null),
    CardBalanceReportPrinter(3,"Настройка принтера отчета по балансам карт", null),
    AutoPlanPaymentSetting(4,"Настройка автооплаты льготного питания", null),
    SubscriberFeeding(5,"Настройки персонализированного питания", null),
    ReplacingMissingBeneficiaries(6,"Режим замены отсутствующих льготников", null),
    PreOrderFeeding(7,"Настройки питания по предзаказу", null),
    PreOrderAutoPay(8,"Настройка автооплаты предзаказа", null),
    ARMsSetting(9,"Настройки АРМа", ARMsSettingsType.getSettingTypeAsMap());

    private Integer id;
    private String description;
    private Map<Integer, SettingType> nestedEnumMap;

    private static Map<Integer,OrgSettingGroup> mapInt = new HashMap<Integer,OrgSettingGroup>();
    static {
        for (OrgSettingGroup orgSettingGroup : OrgSettingGroup.values()) {
            mapInt.put(orgSettingGroup.getId(), orgSettingGroup);
        }
    }

    public static OrgSettingGroup getGroupById(Integer id){
        return mapInt.get(id);
    }

    public static SettingType getSettingTypeByGroupIdAndGlobalId(Integer groupId, Integer globalId){
        OrgSettingGroup group = mapInt.get(groupId);
        if(group != null){
            return group.getSettingTypeByGlobalId(globalId);
        }
        return null;
    }

    OrgSettingGroup(Integer id, String description, Map<Integer, SettingType> nestedEnumMap) {
        this.id = id;
        this.description = description;
        this.nestedEnumMap = nestedEnumMap;
    }

    public Integer getId() {
        return id;
    }

    public SettingType getSettingTypeByGlobalId(Integer globalId){
        return nestedEnumMap.get(globalId);
    }

    @Override
    public String toString(){
        return description;
    }
}
