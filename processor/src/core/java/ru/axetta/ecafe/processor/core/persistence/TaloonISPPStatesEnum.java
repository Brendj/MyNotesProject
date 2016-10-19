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
public enum TaloonISPPStatesEnum {

    /*0*/ TALOON_ISPP_STATE_NOT_SELECTED("Не указано"),
    /*1*/ TALOON_ISPP_STATE_CONFIRMED("Согласовано");

    private final String description;
    static Map<Integer,TaloonISPPStatesEnum> map = new HashMap<Integer,TaloonISPPStatesEnum>();
    static {
        for (TaloonISPPStatesEnum questionaryStatus : TaloonISPPStatesEnum.values()) {
            map.put(questionaryStatus.ordinal(), questionaryStatus);
        }
    }
    private TaloonISPPStatesEnum(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static TaloonISPPStatesEnum fromInteger(Integer value){
        return map.get(value);
    }

}
