/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.service.card;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by i.semenov on 29.09.2017.
 */
public enum CardSignVerifyType {
    /*0*/ VERIFY_SUCCESS("Карта прошла проверку"),
    /*1*/ NOT_PROCESSED("Проверка не выполнялась"),
    /*2*/ VERIFY_FAIL("Карта не прошла проверку");

    private final String description;
    static Map<Integer,CardSignVerifyType> map = new HashMap<Integer,CardSignVerifyType>();
    static {
        for (CardSignVerifyType questionaryStatus : CardSignVerifyType.values()) {
            map.put(questionaryStatus.ordinal(), questionaryStatus);
        }
    }
    private CardSignVerifyType(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static CardSignVerifyType fromInteger(Integer value){
        return map.get(value);
    }
}
