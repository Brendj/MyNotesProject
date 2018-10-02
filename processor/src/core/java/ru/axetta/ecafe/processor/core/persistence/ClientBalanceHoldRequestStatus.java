/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by i.semenov on 26.09.2018.
 */
public enum ClientBalanceHoldRequestStatus {
    /*0*/ CREATED("Предварительный"),
    /*1*/ MOS_RU("Подано через портал"),
    /*2*/ ARM("Подано в школе"),
    /*3*/ ANNULLED("Аннулировано"),
    /*4*/ REFUNDED("Средства возвращены");

    private final String description;
    static Map<Integer,ClientBalanceHoldRequestStatus> map = new HashMap<Integer,ClientBalanceHoldRequestStatus>();
    static {
        for (ClientBalanceHoldRequestStatus questionaryStatus : ClientBalanceHoldRequestStatus.values()) {
            map.put(questionaryStatus.ordinal(), questionaryStatus);
        }
    }

    ClientBalanceHoldRequestStatus(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static ClientBalanceHoldRequestStatus fromInteger(Integer value){
        return map.get(value);
    }
}
