/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 16.02.16
 * Time: 14:11
 * To change this template use File | Settings | File Templates.
 */
public enum MigrantInitiatorEnum {

    /*0*/ INITIATOR_ORG("ОУ"),
    /*1*/ INITIATOR_ESZ("ЕСЗ"),
    /*2*/ INITIATOR_NSI("НСИ");

    private final String description;
    static Map<Integer,MigrantInitiatorEnum> map = new HashMap<Integer,MigrantInitiatorEnum>();
    static {
        for (MigrantInitiatorEnum questionaryStatus : MigrantInitiatorEnum.values()) {
            map.put(questionaryStatus.ordinal(), questionaryStatus);
        }
    }
    private MigrantInitiatorEnum(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static MigrantInitiatorEnum fromInteger(Integer value){
        return map.get(value);
    }

}
