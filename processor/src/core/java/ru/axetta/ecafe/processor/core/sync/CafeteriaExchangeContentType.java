/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync;

import java.util.HashMap;
import java.util.Map;

public enum CafeteriaExchangeContentType {
    BALANCES_AND_ENTEREVENTS(0, "Балансы и проходы"),
    MENU(1, "Меню"),
    MIGRANTS(2, "Мигранты"),
    PAYMENTS_AND_CARDS(3, "Платежи и карты"),
    ELECTRONIC_RECONCILIATION(4, "Электронная сверка"),
    FILE_STORAGE(5, "Хранение файлов"),
    ZERO_TRANSACTIONS(6, "Нулевые транзакции"),
    CARDS(7, "Карты"),
    PHOTOS(8, "Фотографии"),
    CLIENTS_DATA(9, "Данные по клиентам"),
    FOOD_APPLICATIONS(10, "Заявки на питание"),
    ENTEREVENTS(11, "Проходы"),
    GROUPS(12, "Группы"),
    SUPPORT_SERVICE(13, "Служба помощи"),
    PREORDERS(14, "Предзаказы"),
    BLOCK_OR_REFUND_APPLICATIONS(15, "Заявления на блокировку/возврат средств"),
    DISCOUNT_FOOD_APPLICATION(16, "Заявление ЛП"),
    ORGSETTINGS(17, "Настройки организаций"),
    LIBRARY(18, "Библиотека"),
    EZD_REQUEST(19, "Заявки ЭЖД"),
    PAID_MEALS(20, "Электронная сверка по платному питанию"),
    FULL_SYNC(50, "Полная синхронизация");

    private Integer typeCode;
    private String description;

    private static final Map<Integer, CafeteriaExchangeContentType> intMap = new HashMap<>();

    static {
        for(CafeteriaExchangeContentType type : CafeteriaExchangeContentType.values()){
            intMap.put(type.getTypeCode(), type);
        }
    }

    CafeteriaExchangeContentType(Integer typeCode, String description){
        this.typeCode = typeCode;
        this.description = description;
    }

    @Override
    public String toString(){
        return description;
    }

    public Integer getTypeCode() {
        return typeCode;
    }

    public static CafeteriaExchangeContentType getContentTypeByCode(Integer code){
        return intMap.get(code);
    }
}
