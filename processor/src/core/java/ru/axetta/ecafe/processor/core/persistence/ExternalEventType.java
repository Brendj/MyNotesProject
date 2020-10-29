/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by i.semenov on 26.06.2017.
 */
public enum ExternalEventType {

    /*0*/ MUSEUM("Проход в музей"),
    /*1*/ CULTURE("Посещении зданий Мункультуры РФ"),
    /*2*/ SPECIAL("Служебное оповещение"),
    /*3*/ LIBRARY("Проход в библиотеку");

    private final String description;
    static Map<Integer,ExternalEventType> map = new HashMap<Integer,ExternalEventType>();
    static {
        for (ExternalEventType questionaryStatus : ExternalEventType.values()) {
            map.put(questionaryStatus.ordinal(), questionaryStatus);
        }
    }
    private ExternalEventType(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static ExternalEventType fromInteger(Integer value){
        return map.get(value);
    }

}
