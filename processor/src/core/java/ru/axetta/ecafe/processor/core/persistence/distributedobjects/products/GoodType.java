/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import java.util.HashMap;
import java.util.Map;

public enum GoodType {
    /*0*/ UNSPECIFIED(0, "Не указано"),
    /*1*/ BREAKFAST(1, "Завтрак"),
    /*2*/ DINNER(2, "Обед"),
    /*3*/ AFTERNOON_SNACK(3, "Полдник"),
    /*4*/ EVENING_DINNER(4, "Ужин");

    private final Integer code;
    private final String description;

    static Map<Integer,GoodType> map = new HashMap<Integer,GoodType>();
    static {
        for (GoodType type : GoodType.values()) {
            map.put(type.getCode(), type);
        }
    }

    private GoodType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String toString() {
        return description;
    }

    public static GoodType fromInteger(Integer value){
        return map.get(value);
    }
}