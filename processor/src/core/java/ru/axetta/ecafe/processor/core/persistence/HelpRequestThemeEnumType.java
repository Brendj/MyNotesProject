/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

public enum HelpRequestThemeEnumType {

    /*0*/ REPAIR_OF_EQUIPMENT("Ремонт оборудования"),
    /*1*/ ELECTRIC_ISSUE("Не работает электрика"),
    /*2*/ SANITARY_ENGINEERING_ISSUE("Вопрос связанный с сантехникой"),
    /*3*/ MINOR_REPAIR("Мелко-срочный ремонт"),
    /*4*/ OTHER("Другое");

    private final String description;
    static Map<Integer,HelpRequestThemeEnumType> map = new HashMap<Integer,HelpRequestThemeEnumType>();
    static {
        for (HelpRequestThemeEnumType topicEnumType : HelpRequestThemeEnumType.values()) {
            map.put(topicEnumType.ordinal(), topicEnumType);
        }
    }
    private HelpRequestThemeEnumType(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static HelpRequestThemeEnumType fromInteger(Integer value){
        return map.get(value);
    }

    public static HelpRequestThemeEnumType fromString(String description) {
        for (HelpRequestThemeEnumType e : map.values()) {
            if (e.description.equals(description))
                return e;
        }
        return null;
    }
}

