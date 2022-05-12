/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.meals;

import java.util.HashMap;
import java.util.Map;

public enum ResponseCodes {
    RC_OK(0L, "ОК"),
    RC_INTERNAL_ERROR(100L, "Внутренняя ошибка сервера, сбой"),
    RC_WRONG_KEY(120L, "Для доступа к запрашиваемому ресурсу требуется аутентификация"),
    RC_WRONG_REQUST(130L, "Не хватает обязательных параметров, неправильное значение параметров или другая ошибка, связанная с формулировкой запроса"),
    RC_WRONG_DATE(210L, "Неверная дата и время операции"),
    RC_NOT_FOUND_AVAILABLE_CLIENT (140L, "Клиент не уполномочен совершать операции с запрошенным ресурсом"),
    RC_NOT_FOUND_CLIENT(150L, "Клиент не найден"),
    RC_NOT_FOUND_ORG(160L, "Клиент не уполномочен совершать операции с запрошенным ресурсом"),
    RC_NOT_FOUND_FOODBOX(170L, "Заявка не найдена"),
    RC_FOUND_FOODBOX(180L, "Заявка с данных идентификатором уже зарегистрирована"),
    RC_NOT_FOUND_AVAILABLE_PARALLEL (190L, "Клиент не уполномочен совершать операции с запрошенным ресурсом");

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
