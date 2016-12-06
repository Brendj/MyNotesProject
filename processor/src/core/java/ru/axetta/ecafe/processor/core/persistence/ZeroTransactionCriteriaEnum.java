/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 28.03.16
 * Time: 12:35
 * To change this template use File | Settings | File Templates.
 */
public enum ZeroTransactionCriteriaEnum {
    /*0*/ ZT_TYPE_INOUT("Входы-выходы"),
    /*1*/ ZT_TYPE_DISCOUNTPLAN("Льготный план"),
    /*2*/ ZT_TYPE_PAYDABLEPLAN("Платный план"),
    /*3*/ ZT_TYPE_BUFFET("Буфет"),
    /*4*/ ZT_TYPE_DISCOUNTPLANLOWGRADE("Льготный план начальные классы"),
    /*5*/ ZT_TYPE_DISCOUNTPLANMIDDLEHIGHTGRADE("Льготный план старшие классы"),
    /*6*/ ZT_TYPE_PAYDABLEPLANCHILDREN("Платный план обучающиеся"),
    /*7*/ ZT_TYPE_PAYDABLEPLANNOTCHILDREN("Платный план не обучающиеся");


    private final String description;
    static Map<Integer,ZeroTransactionCriteriaEnum> map = new HashMap<Integer,ZeroTransactionCriteriaEnum>();
    static {
        for (ZeroTransactionCriteriaEnum questionaryStatus : ZeroTransactionCriteriaEnum.values()) {
            map.put(questionaryStatus.ordinal(), questionaryStatus);
        }
    }
    private ZeroTransactionCriteriaEnum(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static ZeroTransactionCriteriaEnum fromInteger(Integer value) throws IllegalArgumentException {
        if (map.get(value) == null) {
            throw new IllegalArgumentException("Element not found by value");
        }
        return map.get(value);
    }
}
