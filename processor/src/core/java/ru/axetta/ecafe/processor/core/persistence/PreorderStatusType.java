/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nuc on 13.02.2020.
 */
public enum PreorderStatusType {
    /*0*/ OK(0,"Нормальный"),
    /*1*/ BLOCKED(1,"Заблокирован");

    private final Integer code;
    private final String description;

    static Map<Integer,PreorderStatusType> map = new HashMap<Integer, PreorderStatusType>();
    static {
        for (PreorderStatusType status : PreorderStatusType.values()) {
            map.put(status.getCode(), status);
        }
    }

    private PreorderStatusType(int code, String description) {
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

    public static PreorderStatusType fromInteger(Integer value){
        return map.get(value);
    }
}
