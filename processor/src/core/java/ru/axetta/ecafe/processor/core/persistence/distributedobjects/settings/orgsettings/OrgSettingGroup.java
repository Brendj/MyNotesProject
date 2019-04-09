/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.orgsettings;

import java.util.HashMap;
import java.util.Map;

public enum OrgSettingGroup {
    CashierCheckPrinter(0,"Настройки принтера кассового чека"),
    SalesReportPrinter(1,"Настройка принтера отчета по продажам"),
    CardBalanceReportPrinter(2,"Настройка принтера отчета по балансам карт"),
    AutoPlanPaymentSetting(3,"Настройка автооплаты льготного питания"),
    SubscriberFeeding(4,"Настройки персонализированного питания"),
    ReplacingMissingBeneficiaries(5,"Режим замены отсутствующих льготников"),
    PreOrderFeeding(6,"Настройки питания по предзаказу"),
    PreOrderAutopay(7,"Настройка автооплаты предзаказа"),
    ARMsSetting(8,"Настройки АРМа");

    private Integer id;
    private String description;

    private static Map<Integer,OrgSettingGroup> mapInt = new HashMap<Integer,OrgSettingGroup>();
    private static Map<String,OrgSettingGroup> mapStr = new HashMap<String,OrgSettingGroup>();
    static {
        for (OrgSettingGroup questionaryStatus : OrgSettingGroup.values()) {
            mapInt.put(questionaryStatus.getId(), questionaryStatus);
            mapStr.put(questionaryStatus.toString(), questionaryStatus);
        }
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
