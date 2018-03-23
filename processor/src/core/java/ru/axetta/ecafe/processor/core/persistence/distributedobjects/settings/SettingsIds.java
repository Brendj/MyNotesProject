/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 05.04.13
 * Time: 11:41
 * To change this template use File | Settings | File Templates.
 */
public enum SettingsIds {
    CashierCheckPrinter(0,"Настройки принтера кассового чека"),
    SalesReportPrinter(1,"Настройка принтера отчета по продажам"),
    CardBalanceReportPrinter(2,"Настройка принтера отчета по балансам карт"),
    AutoPlanPaymentSetting(3,"Настройка автооплаты льготного питания"),
    SubscriberFeeding(4,"Настройки персонализированного питания"),
    ReplacingMissingBeneficiaries(5,"Режим замены отсутствующих льготников"),
    PreOrderFeeding(6,"Настройки питания по предзаказу");

    private Integer id;
    private String description;

    private static Map<Integer,SettingsIds> mapInt = new HashMap<Integer,SettingsIds>();
    private static Map<String,SettingsIds> mapStr = new HashMap<String,SettingsIds>();
    static {
        for (SettingsIds questionaryStatus : SettingsIds.values()) {
            mapInt.put(questionaryStatus.getId(), questionaryStatus);
            mapStr.put(questionaryStatus.toString(), questionaryStatus);
        }
    }

    private SettingsIds(Integer id, String description) {
        this.id = id;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public static SettingsIds fromInteger(Integer id){
        return mapInt.get(id);
    }

    public static SettingsIds fromString(String description){
        return mapStr.get(description);
    }

    @Override
    public String toString() {
        return  description;
    }
}
