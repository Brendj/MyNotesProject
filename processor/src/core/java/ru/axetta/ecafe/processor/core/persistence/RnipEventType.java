/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by i.semenov on 30.05.2019.
 */
public enum RnipEventType {
    CONTRAGENT_CREATE(0, "Создание каталога"),
    CONTRAGENT_EDIT(1, "Изменение каталога"),
    PAYMENT(2, "Экспорта платежей"),
    PAYMENT_MODIFIED(3, "Экспорт корректировок");

    private final Integer code;
    private final String description;
    private static Map<Integer,RnipEventType> mapInt = new HashMap<Integer,RnipEventType>();
    private static Map<String,RnipEventType> mapStr = new HashMap<String,RnipEventType>();
    static {
        for (RnipEventType value : RnipEventType.values()) {
            mapInt.put(value.getCode(), value);
            mapStr.put(value.toString(), value);
        }
    }

    RnipEventType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static RnipEventType fromInteger(Integer id) {
        return mapInt.get(id);
    }

    public static RnipEventType fromString(String description) {
        return mapStr.get(description);
    }
}
