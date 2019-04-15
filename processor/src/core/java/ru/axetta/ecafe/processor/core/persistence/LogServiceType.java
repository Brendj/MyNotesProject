/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nuc on 04.04.2019.
 */
public enum LogServiceType {
    /*0*/ CLIENT_ROOM_CONTROLLER("Сервис информирования");

    private final String description;
    static Map<Integer,LogServiceType> map = new HashMap<Integer,LogServiceType>();
    static {
        for (LogServiceType topicEnumType : LogServiceType.values()) {
            map.put(topicEnumType.ordinal(), topicEnumType);
        }
    }
    private LogServiceType(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static LogServiceType fromInteger(Integer value){
        return map.get(value);
    }

    public static LogServiceType fromString(String description) {
        for (LogServiceType e : map.values()) {
            if (e.description.equals(description))
                return e;
        }
        return null;
    }

}
