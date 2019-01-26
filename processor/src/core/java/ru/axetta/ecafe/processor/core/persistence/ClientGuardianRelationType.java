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

    /*0*/ MOTHER("Мать"),
    /*1*/ FATHER("Отец"),
    /*2*/ REPRESENTATIVE("Представитель"),
    /*3*/ GUARDIAN("Опекун/попечитель"),
    /*4*/ ADOPTIVE_PARENT("Приемный родитель"),
    /*5*/ ADOPTIVE_FATHER("Усыновитель"),
    /*6*/ FOSTER_PARENT("Патронатный воспитатель"),
    /*7*/ OTHER("Иное"),
    /*8*/ TRUSTED_REPRESENTATIVE("Доверенный представитель");

    private final String description;
    static Map<Integer,ClientGuardianRelationType> map = new HashMap<Integer,ClientGuardianRelationType>();
    static {
        for (ClientGuardianRelationType questionaryStatus : ClientGuardianRelationType.values()) {
            map.put(questionaryStatus.ordinal(), questionaryStatus);
        }
    }
    private ClientGuardianRelationType(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static ClientGuardianRelationType fromInteger(Integer value){
        return map.get(value);
    }
}
