/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch;


import java.util.HashMap;
import java.util.Map;

public enum ResponseCodes {
    RC_OK(0L, "OK"),
    RC_INTERNAL_ERROR(100L, "Внутренняя ошибка"),
    RC_BAD_ARGUMENTS_ERROR(110L, "Не корректные параметры запроса");


    private final Long code;
    private final String description;

    static Map<Long, ResponseCodes> map = new HashMap<Long, ResponseCodes>();
    static {
        for (ResponseCodes type : ResponseCodes.values()) {
            map.put(type.getCode(), type);
        }
    }

    private ResponseCodes(long code, String description) {
        this.code = code;
        this.description = description;
    }

    public Long getCode() {
        return code;
    }

    @Override
    public String toString() {
        return description;
    }

    public static ResponseCodes fromInteger(Integer value){
        return map.get(value);
    }
}
