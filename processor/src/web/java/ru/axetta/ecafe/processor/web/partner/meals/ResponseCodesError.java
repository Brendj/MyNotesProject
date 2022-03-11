/*
 * Copyright (c) 2022. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.meals;

import java.util.HashMap;
import java.util.Map;

public enum ResponseCodesError {
    RC_ERROR_TIME(10L, "Заказ не попадает в рамки времени работы буфета"),
    RC_ERROR_LIMIT(11L, "Заказ не может быть оформлен из-за превышения дневного лимита"),
    RC_ERROR_HAVE_PREORDER(12L, "Заказ не может быть оформлен из-за наличия незавершенного заказа"),
    RC_ERROR_NOMONEY(13L, "Заказ не может быть оформлен из-за отсутствия денежных средств"),
    RC_ERROR_CELL(14L, "Заказ не может быть оформлен из-за отсутствия свободной ячейки"),
    RC_ERROR_DISH_COUNT(15L, "Количество блюд в заказе превышает допустимый лимит"),
    RC_ERROR_NO_DISH(16L, "Все заказанные позиции недоступны для выдачи");

    private final Long code;
    private final String description;

    static Map<Long, ResponseCodesError> map = new HashMap<Long, ResponseCodesError>();
    static {
        for (ResponseCodesError type : ResponseCodesError
                .values()) {
            map.put(type.getCode(), type);
        }
    }

    private ResponseCodesError(long code, String description) {
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
