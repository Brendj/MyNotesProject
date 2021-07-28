/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

public enum InfoMessageType {
    /*0*/ TO_SCHOOL_ARM("Объявление"),
    /*1*/ TO_WEB_ARM("Новое в сборке");

    private final String description;
    static Map<Integer,InfoMessageType> map = new HashMap<Integer, InfoMessageType>();
    static {
        for (InfoMessageType statusEnumType : InfoMessageType.values()) {
            map.put(statusEnumType.ordinal(), statusEnumType);
        }
    }
    private InfoMessageType(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static InfoMessageType fromInteger(Integer value){
        return map.get(value);
    }
}
