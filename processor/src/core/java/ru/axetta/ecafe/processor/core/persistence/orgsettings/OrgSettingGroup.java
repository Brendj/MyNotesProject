/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings;

import ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes.*;

import java.util.HashMap;
import java.util.Map;

/* TODO:
   Часть классов возращают NULL и emptyCollection. По мере появлении новых настроек необходимо расширять ENUM'ы
*/
public enum OrgSettingGroup {
    UnknownSetting(0, "Неизвестно", UnknownSettingsType.getSettingTypeAsMap()),
    CashierCheckPrinter(1,"Настройки принтера кассового чека", CashierCheckPrinterType.getSettingTypeAsMap()),
    SalesReportPrinter(2,"Настройка принтера отчета по продажам", SalesReportPrinterType.getSettingTypeAsMap()),
    CardBalanceReportPrinter(3,"Настройка принтера отчета по балансам карт", CardBalanceReportPrinterType.getSettingTypeAsMap()),
    AutoPlanPaymentSetting(4,"Настройка автооплаты льготного питания", AutoPlanPaymentSettingType.getSettingTypeAsMap()),
    SubscriberFeeding(5,"Настройки персонализированного питания", SubscriberFeedingType.getSettingTypeAsMap()),
    ReplacingMissingBeneficiaries(6,"Режим замены отсутствующих льготников", ReplacingMissingBeneficiariesType.getSettingTypeAsMap()),
    PreOrderFeeding(7,"Настройки питания по предзаказу", PreOrderFeedingType.getSettingTypeAsMap()),
    PreOrderAutoPay(8,"Настройка автооплаты предзаказа", PreOrderAutoPayType.getSettingTypeAsMap()),
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
