/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model.enums;

public enum ClientNotificationSettingType {
    SMS_SETTING_CHANGED(1L, "Настройки были изменены"),
    SMS_NOTIFY_REFILLS(1000000000L, "Оповещать о пополнениях"),
    SMS_NOTIFY_EVENTS(1100000000L, "Оповещать о проходах"),
    SMS_NOTIFY_ORDERS(1200000000L, "Оповещать о покупках в буфете"),
    SMS_NOTIFY_ORDERS_PAY(1220000000L, "Оповещать о покупках обедов"),
    SMS_NOTIFY_ORDERS_FREE(1230000000L, "Оповещать о покупках льготного питания"),
    SMS_NOTIFY_SUMMARY_DAY(1300000000L, "Оповещать по итогам дня"),
    SMS_NOTIFY_SUMMARY_WEEK(1400000000L, "Оповещать по итогам недели"),
    SMS_NOTIFY_LOW_BALANCE(1500000000L, "Оповещать о снижении баланса"),
    SMS_NOTIFY_MUSEUM(1600000000L, "Оповещение о получении и аннулировании билета в музей"),
    SMS_NOTIFY_CULTURE(1700000000L, "Оповещение о посещении зданий учреждений Минкультуры (управляется флагом \"Оповещать о проходах\")"),
    SMS_NOTIFY_SPECIAL(1800000000L, "Служебные оповещения");

    private final Long code;
    private final String description;

    ClientNotificationSettingType(Long code, String description) {
        this.code = code;
        this.description = description;
    }

    public Long getCode() {
        return code;
    }

    @Override
    public String toString(){
        return description;
    }
}
