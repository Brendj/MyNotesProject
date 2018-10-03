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
    /*0*/ CREATED("Создано"),
    /*1*/ MOS_RU("Подписано"),
    /*2*/ ARM("Аннулировано"),
    /*3*/ ANNULLED("Средства возвращены"),
    /*4*/ REFUNDED("Отказано в возврате средств");

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
