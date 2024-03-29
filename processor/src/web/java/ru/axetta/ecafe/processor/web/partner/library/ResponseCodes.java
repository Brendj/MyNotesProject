/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.library;

import java.util.HashMap;
import java.util.Map;

public enum ResponseCodes {
    RC_OK(0L, "ОК"),
    RC_INTERNAL_ERROR(100L, "ошибка сервера"),
    RC_WRONG_KEY(120L, "Неверный ключ доступа"),
    RC_WRONG_REQUST(130L, "Не все обязательные поля заполнены"),
    RC_WRONG_DATE(210L, "Неверная дата и время операции"),
    RC_NOT_FOUND_CLIENT(150L, "Клиент не найден"),
    RC_NOT_FOUND_ORG(160L, "Организация не найдена"),
    RC_NOT_FOUND_ESP(170L, "Заявка не найдена");

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
