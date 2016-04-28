/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 26.04.16
 * Time: 11:28
 * To change this template use File | Settings | File Templates.
 */
public enum SJBalanceTypeEnum {
    /*0*/ SJBALANCE_TYPE_PAYMENT("Пополнение"),
    /*1*/ SJBALANCE_TYPE_ORDER("Списание");

    private final String description;
    static Map<Integer,SJBalanceTypeEnum> map = new HashMap<Integer,SJBalanceTypeEnum>();
    static {
        for (SJBalanceTypeEnum questionaryStatus : SJBalanceTypeEnum.values()) {
            map.put(questionaryStatus.ordinal(), questionaryStatus);
        }
    }
    private SJBalanceTypeEnum(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static SJBalanceTypeEnum fromInteger(Integer value){
        return map.get(value);
    }
}
