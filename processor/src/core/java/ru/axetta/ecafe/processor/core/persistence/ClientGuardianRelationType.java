/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by i.semenov on 08.08.2016.
 */
public enum ClientGuardianRelationType {

    MOTHER(0, "Мать"),
    FATHER(1, "Отец"),
    UNCLE(8, "Дядя"),
    AUNT(9, "Тётя"),
    BROTHER(10, "Брат"),
    SISTER(11, "Сестра"),
    GRANDMOTHER(12, "Бабушка"),
    GRANDFATHER(13, "Дедушка"),
    UNDEFINED (16, "Не определено");

    //Старые типы представителей, значения конвертируем в новые
    private static final int ADOPTIVE_PARENT = 4;
    private static final int ADOPTIVE_FATHER = 5;
    private static final int FOSTER_PARENT = 6;
    private static final int OTHER = 7;

    private final String description;
    private final int code;
    static Map<Integer,ClientGuardianRelationType> map = new HashMap<Integer,ClientGuardianRelationType>();
    static {
        for (ClientGuardianRelationType questionaryStatus : ClientGuardianRelationType.values()) {
            map.put(questionaryStatus.code, questionaryStatus);
        }
    }

    private ClientGuardianRelationType(int code, String description){
        this.code = code;
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static ClientGuardianRelationType fromInteger(Integer value){
        if (value == null)
            return null;
        ClientGuardianRelationType relType = map.get(value);
        if (relType != null) return relType;
        if (value.equals(ADOPTIVE_PARENT)) return UNDEFINED;
        if (value.equals(ADOPTIVE_FATHER)) return UNDEFINED;
        if (value.equals(FOSTER_PARENT)) return UNDEFINED;
        if (value.equals(OTHER)) return UNDEFINED;
        return UNDEFINED;
    }

    //todo Использовался в сверке с реестрами, удалить.
    public static String getRelationshipExtended(String relation) {
        return "";
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

}
