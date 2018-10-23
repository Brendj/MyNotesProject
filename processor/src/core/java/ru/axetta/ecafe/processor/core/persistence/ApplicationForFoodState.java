/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

public enum ApplicationForFoodState {
    FILED(1010, "Подано"),
    TRY_TO_REGISTER(1040, "Проходит процедуру регистрации"),
    DELIVERY_ERROR(103099, "Ошибка доставки"),
    REGISTERED(1050, "Зарегистрировано"),
    PAUSED(1060, "Приостановлено"),
    RESUME(1160, "Возобновлено"),
    DENIED(1080, "Отказ"),
    OK(1075, "Решение положительное"),
    INFORMATION_REQUEST_SENDED(7704, "Направлен запрос сведений по МВ"),
    INFORMATION_REQUEST_RECEIVED(7705, "Получены сведения на запрос по МВ"),
    RESULT_PROCESSING(1052, "Формирование результата");

    private final Integer code;
    private final String description;
    private static Map<Integer,ApplicationForFoodState> mapInt = new HashMap<Integer,ApplicationForFoodState>();
    private static Map<String,ApplicationForFoodState> mapStr = new HashMap<String,ApplicationForFoodState>();
    static {
        for (ApplicationForFoodState value : ApplicationForFoodState.values()) {
            mapInt.put(value.getCode(), value);
            mapStr.put(value.toString(), value);
        }
    }

    ApplicationForFoodState(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ApplicationForFoodState fromCode(Integer id) {
        return mapInt.get(id);
    }

    public static ApplicationForFoodState fromDescription(String description) {
        return mapStr.get(description);
    }
}
