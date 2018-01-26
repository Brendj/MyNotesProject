/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

public enum HelpRequestStatusEnumType {

    /*0*/ OPEN("Открыта"),
    /*1*/ ANNULLED("Аннулирована (закрыта)"),
    /*2*/ WORKED_OUT("Отработана (закрыта)");

    private final String description;
    static Map<Integer,HelpRequestStatusEnumType> map = new HashMap<Integer,HelpRequestStatusEnumType>();
    static {
        for (HelpRequestStatusEnumType statusEnumType : HelpRequestStatusEnumType.values()) {
            map.put(statusEnumType.ordinal(), statusEnumType);
        }
    }
    private HelpRequestStatusEnumType(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static HelpRequestStatusEnumType fromInteger(Integer value){
        return map.get(value);
    }

}

