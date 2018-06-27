/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import java.util.HashMap;
import java.util.Map;

public enum GoodAgeGroupType {
    /*0*/ UNSPECIFIED(0, "Не указано"),
    /*1*/ G_1_5_3(1, "1.5 - 3"),
    /*2*/ G_3_7(2, "3 - 7"),
    /*3*/ G_1_4(3, "1 - 4"),
    /*4*/ G_5_11(4, "5 - 11");

    private final Integer code;
    private final String description;

    static Map<Integer,GoodAgeGroupType> map = new HashMap<Integer,GoodAgeGroupType>();
    static {
        for (GoodAgeGroupType type : GoodAgeGroupType.values()) {
            map.put(type.getCode(), type);
        }
    }

    private GoodAgeGroupType(int code, String description) {
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

    public static GoodAgeGroupType fromInteger(Integer value){
        return map.get(value);
    }
}