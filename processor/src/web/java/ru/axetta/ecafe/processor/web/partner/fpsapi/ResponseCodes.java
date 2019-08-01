/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.fpsapi;

import java.util.HashMap;
import java.util.Map;

public enum ResponseCodes {
    RC_OK(0L, "OK"),
    RC_INTERNAL_ERROR(5L, "неверный regID"),
    RC_BAD_ARGUMENTS_ERROR(2L, "ошибка сервера");


    private final Long code;
    private final String description;

    static Map<Long, ResponseCodes> map = new HashMap<Long, ResponseCodes>();
    static {
        for (ResponseCodes type : ResponseCodes
                .values()) {
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
}
