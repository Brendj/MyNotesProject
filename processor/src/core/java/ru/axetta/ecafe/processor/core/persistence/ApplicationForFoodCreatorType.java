/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

public enum ApplicationForFoodCreatorType {
    OU(0, "ОУ"),
    PORTAL(1, "портал"),
    ENROLLMENT_IS(2, "ИС Зачисление ОУ");

    private final Integer code;
    private final String description;
    private static Map<Integer,ApplicationForFoodCreatorType> mapInt = new HashMap<Integer,ApplicationForFoodCreatorType>();
    private static Map<String,ApplicationForFoodCreatorType> mapStr = new HashMap<String,ApplicationForFoodCreatorType>();
    static {
        for (ApplicationForFoodCreatorType value : ApplicationForFoodCreatorType.values()) {
            mapInt.put(value.getCode(), value);
            mapStr.put(value.toString(), value);
        }
    }

    ApplicationForFoodCreatorType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ApplicationForFoodCreatorType fromCode(Integer id) {
        return mapInt.get(id);
    }

    public static ApplicationForFoodCreatorType fromDescription(String description) {
        return mapStr.get(description);
    }
}
