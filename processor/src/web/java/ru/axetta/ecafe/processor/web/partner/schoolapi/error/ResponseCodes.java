/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.error;

import java.util.HashMap;
import java.util.Map;

public enum ResponseCodes {
    OK(0, "ОК"),
    SERVER_ERROR(502, "Внутренняя ошибка сервера"),
    BAD_REQUEST_ERROR(400, "Ошибка данных"),
    CLIENT_NOT_FOUND(101, "Клиент не найден"),
    CLIENT_GROUP_NOT_FOUND(102, "Группа клиентов не найден"),
    ORG_NOT_FOUND(103, "Организация не найдена"),
    ORG_GROUP_IS_NOT_FRIENDLY(104, "Организация не является дружественной"),
    GROUP_MANAGER_NOT_FOUND(105, "Руководитель группы не найден"),
    ORDER_REGISTER_ERROR(110, "Ошибка при регистрации заказа");



    private final int code;
    private final String description;

    static Map<Integer, ResponseCodes> map = new HashMap<>();
    static {
        for (ResponseCodes type : ResponseCodes.values()) {
            map.put(type.getCode(), type);
        }
    }

    ResponseCodes(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public ResponseCodes fromCode(int code) {
        return map.get(code);
    }

    @Override
    public String toString() {
        return description;
    }
}
