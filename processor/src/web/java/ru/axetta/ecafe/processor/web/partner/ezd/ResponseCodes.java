/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.ezd;

import java.util.HashMap;
import java.util.Map;

public enum ResponseCodes {
    RC_OK(0L, "ОК"),
    RC_BAD_ARGUMENTS_ERROR(100L, "ошибка сервера/переданы некорректные параметры"),
    RC_INTERNAL_ERROR(110L, "организация не найдена"),
    RC_WRONG_DATA(210L, "не верная дата"),
    ////////////////
    RC_PARAM_NOT_FOUND(220L, "Отсутствуют обязательные поля"),
    RC_WRONG_KEY(220L, "Неверный ключ доступа"),
    RC_CLIENT_NOT_FOUND(230L, "Клиент не найден"),
    RC_NO_CONFIG(240L, "Севрис временно не доступен. Попробуйте позже"),
    RC_BAD_CATEGORY(250L, "Для клиентов вашей категории пользование данным сервисом не предусмотрено"),
    RC_NO_CARD(260L, "На текущий момент доступ в здание невозможен по причине отсутствия активного электронного идентификатора"),
    RC_NO_ACTIVE_CARD(270L, "На текущий момент доступ в здание невозможен по причине заблокированного электронного идентификатора"),
    RC_SERVER_ERROR(280L, "Внутренняя ошибка сервера");

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
