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
public enum TaloonCreatedTypeEnum {

    /*0*/ TALOON_CREATED_TYPE_AUTO("Авто (ОО в ИС ПП)"),
    /*1*/ TALOON_CREATED_TYPE_MANUAL("Ручной (ОО нет в ИС ПП)");

    private final String description;
    static Map<Integer,TaloonCreatedTypeEnum> map = new HashMap<Integer,TaloonCreatedTypeEnum>();
    static {
        for (TaloonCreatedTypeEnum questionaryStatus : TaloonCreatedTypeEnum.values()) {
            map.put(questionaryStatus.ordinal(), questionaryStatus);
        }
    }
    private TaloonCreatedTypeEnum(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static TaloonCreatedTypeEnum fromInteger(Integer value){
        return map.get(value);
    }

}
