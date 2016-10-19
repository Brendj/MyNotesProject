/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
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
public enum TaloonPPStatesEnum {

    /*0*/ TALOON_PP_STATE_NOT_SELECTED("Не указано"),
    /*1*/ TALOON_PP_STATE_CONFIRMED("Согласовано"),
    /*2*/ TALOON_PP_STATE_CANCELED("Отказ");

    private final String description;
    static Map<Integer,TaloonPPStatesEnum> map = new HashMap<Integer,TaloonPPStatesEnum>();
    static {
        for (TaloonPPStatesEnum questionaryStatus : TaloonPPStatesEnum.values()) {
            map.put(questionaryStatus.ordinal(), questionaryStatus);
        }
    }
    private TaloonPPStatesEnum(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static TaloonPPStatesEnum fromInteger(Integer value){
        return map.get(value);
    }

}
