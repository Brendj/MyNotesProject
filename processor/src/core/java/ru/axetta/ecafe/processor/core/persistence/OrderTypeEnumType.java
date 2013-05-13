/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 25.12.12
 * Time: 15:58
 * To change this template use File | Settings | File Templates.
 */
public enum OrderTypeEnumType {

    UNKNOWN("Неизвестный"),
    DEFAULT("По-умолчанию"),
    VENDING("Вендинг"),
    PAY_PLAN("План платного питания"),
    REDUCED_PRICE_PLAN("План льготного питания"),
    DAILY_SAMPLE("суточная проба");

    private final String description;
    static Map<Integer,OrderTypeEnumType> map = new HashMap<Integer,OrderTypeEnumType>();
    static {
        for (OrderTypeEnumType questionaryStatus : OrderTypeEnumType.values()) {
            map.put(questionaryStatus.ordinal(), questionaryStatus);
        }
    }
    private OrderTypeEnumType(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static OrderTypeEnumType fromInteger(Integer value){
        return map.get(value);
    }

}
