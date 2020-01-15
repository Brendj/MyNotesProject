/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by i.semenov on 26.06.2017.
 */
public enum ExternalEventStatus {

    /*0*/ TICKET_GIVEN("Выдан билет"),
    /*1*/ TICKET_BACK("Возврат билета"),
    /*2*/ START_SICK("Рекомендация об освобождении"),
    /*3*/ CANCEL_START_SICK("Отмена рекомендации об освобождении"),
    /*4*/ END_SICK("Рекомендация о возможности посещать ОО"),
    /*5*/ CANCEL_END_SICK("Отмена рекомендации о возможности посещать ОО");

    private final String description;
    static Map<Integer,ExternalEventStatus> map = new HashMap<Integer,ExternalEventStatus>();
    static {
        for (ExternalEventStatus questionaryStatus : ExternalEventStatus.values()) {
            map.put(questionaryStatus.ordinal(), questionaryStatus);
        }
    }
    private ExternalEventStatus(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static ExternalEventStatus fromInteger(Integer value){
        return map.get(value);
    }

}
