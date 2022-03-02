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

    /*0*/ UNKNOWN("Неизвестный"),
    /*1*/ DEFAULT("По-умолчанию"),
    /*2*/ VENDING("Вендинг"),
    /*3*/ PAY_PLAN("План платного питания"),
    /*4*/ REDUCED_PRICE_PLAN("План льготного питания"),
    /*5*/ DAILY_SAMPLE("Суточная проба"),
    /*6*/ REDUCED_PRICE_PLAN_RESERVE("План льготного питания, резерв"),
    /*7*/ SUBSCRIPTION_FEEDING("Абонементное питание"),
    /*8*/ CORRECTION_TYPE("Корректировочный тип"),
    /*9*/ TEST_EMULATOR("Тестовый тип"),
    /*10*/ WATER_ACCOUNTING("Бутилированная вода"),
    /*11*/ DISCOUNT_PLAN_CHANGE("Льготный план замена"),
    /*12*/ RECYCLING_RETIONS("Утилизация"),
    /*13*/ FOODBOX("Фудбокс");

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
