/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

public enum ApplicationForFoodDeclineReason {
    NO_DOCS(1, "Отказано в предоставлении услуги. Основание для отказа: «Заявитель не представил документы, подтверждающие право на льготное питание»."),
    NO_APPROVAL(2, "Отказано в предоставлении услуги. Основание для отказа: «Право на получение питания за счет бюджета города Москвы не подтверждено по результатам решения Управляющего совета школы»."),
    INFORMATION_CONFLICT(3, "Отказано в предоставлении услуги. Основание для отказа: «По результатам межведомственного взаимодействия с Департаментом труда и социальной защиты населения города "
            + "Москвы льготный статус ребенка не подтвержден».");

    private final Integer code;
    private final String description;
    private static Map<Integer,ApplicationForFoodDeclineReason> mapInt = new HashMap<Integer,ApplicationForFoodDeclineReason>();
    private static Map<String,ApplicationForFoodDeclineReason> mapStr = new HashMap<String,ApplicationForFoodDeclineReason>();
    static {
        for (ApplicationForFoodDeclineReason value : ApplicationForFoodDeclineReason.values()) {
            mapInt.put(value.getCode(), value);
            mapStr.put(value.toString(), value);
        }
    }

    ApplicationForFoodDeclineReason(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ApplicationForFoodDeclineReason fromInteger(Integer id) {
        return mapInt.get(id);
    }

    public static ApplicationForFoodDeclineReason fromString(String description) {
        return mapStr.get(description);
    }
}
