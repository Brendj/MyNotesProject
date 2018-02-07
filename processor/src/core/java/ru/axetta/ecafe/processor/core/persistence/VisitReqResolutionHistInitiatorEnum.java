/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

public enum VisitReqResolutionHistInitiatorEnum {

    /*0*/ INITIATOR_CLIENT("Клиент"),
    /*1*/ INITIATOR_ESZ("ЕСЗ"),
    /*2*/ INITIATOR_REESTR("Реестры"),
    /*3*/ INITIATOR_ISPP("Процессинг");

    private final String description;
    static Map<Integer,VisitReqResolutionHistInitiatorEnum> map = new HashMap<Integer,VisitReqResolutionHistInitiatorEnum>();
    static {
        for (VisitReqResolutionHistInitiatorEnum questionaryStatus : VisitReqResolutionHistInitiatorEnum.values()) {
            map.put(questionaryStatus.ordinal(), questionaryStatus);
        }
    }
    private VisitReqResolutionHistInitiatorEnum(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static VisitReqResolutionHistInitiatorEnum fromInteger(Integer value){
        return map.get(value);
    }

}