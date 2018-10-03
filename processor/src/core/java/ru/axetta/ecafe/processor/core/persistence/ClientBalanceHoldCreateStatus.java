/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by i.semenov on 26.09.2018.
 */
public enum ClientBalanceHoldCreateStatus {
    /*0*/ CHANGE_SUPPLIER("Процессинг"),
    /*1*/ MOS_RU("Арм администратора ОУ"),
    /*2*/ ARM("Портал");

    private final String description;
    static Map<Integer,ClientBalanceHoldCreateStatus> map = new HashMap<Integer,ClientBalanceHoldCreateStatus>();
    static {
        for (ClientBalanceHoldCreateStatus questionaryStatus : ClientBalanceHoldCreateStatus.values()) {
            map.put(questionaryStatus.ordinal(), questionaryStatus);
        }
    }

    ClientBalanceHoldCreateStatus(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static ClientBalanceHoldCreateStatus fromInteger(Integer value){
        return map.get(value);
    }
}
