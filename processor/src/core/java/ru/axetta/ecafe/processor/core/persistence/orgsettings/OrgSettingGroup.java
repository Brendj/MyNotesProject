/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings;

import java.util.HashMap;
import java.util.Map;

/*
UPDATE cf_orgsettings
SET settingGroup = settingGroup + 1;
*/

public enum OrgSettingGroup {
    UnknownSetting(0, "Неизвестно"),
    CashierCheckPrinter(1,"Настройки принтера кассового чека"),
    SalesReportPrinter(2,"Настройка принтера отчета по продажам"),
    CardBalanceReportPrinter(3,"Настройка принтера отчета по балансам карт"),
    AutoPlanPaymentSetting(4,"Настройка автооплаты льготного питания"),
    SubscriberFeeding(5,"Настройки персонализированного питания"),
    ReplacingMissingBeneficiaries(6,"Режим замены отсутствующих льготников"),
    PreOrderFeeding(7,"Настройки питания по предзаказу"),
    PreOrderAutopay(8,"Настройка автооплаты предзаказа"),
    ARMsSetting(9,"Настройки АРМа");

    private Integer id;
    private String description;

    private static Map<Integer,OrgSettingGroup> mapInt = new HashMap<Integer,OrgSettingGroup>();
    private static Map<String,OrgSettingGroup> mapStr = new HashMap<String,OrgSettingGroup>();
    static {
        for (OrgSettingGroup orgSettingGroup : OrgSettingGroup.values()) {
            mapInt.put(orgSettingGroup.getId(), orgSettingGroup);
            mapStr.put(orgSettingGroup.toString(), orgSettingGroup);
        }
    }

    public static OrgSettingGroup getGroupById(Integer id){
        return mapInt.get(id);
    }

    OrgSettingGroup(Integer id, String description) {
        this.id = id;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString(){
        return description;
    }
}
