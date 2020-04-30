/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nuc on 07.04.2020.
 */
public enum ClientGuardianRepresentType {
    UNKNOWN(-1, "Не определено"),
    NOT_IN_LAW(0, "Не является законным представителем"),
    IN_LAW(1, "Законный представитель"),
    GUARDIAN(2, "Расширенные полномочия");

    private final String description;
    private final int code;
    static Map<Integer,ClientGuardianRepresentType> map = new HashMap<Integer,ClientGuardianRepresentType>();
    static {
        for (ClientGuardianRepresentType questionaryStatus : ClientGuardianRepresentType.values()) {
            map.put(questionaryStatus.code, questionaryStatus);
        }
    }

    private ClientGuardianRepresentType(int code, String description){
        this.code = code;
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static ClientGuardianRepresentType fromInteger(Integer value){
        if (value == null)
            return map.get(-1);
        return map.get(value);
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }


}
