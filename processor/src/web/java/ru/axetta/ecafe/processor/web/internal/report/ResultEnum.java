/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.report;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 29.01.13
 * Time: 13:10
 * To change this template use File | Settings | File Templates.
 */
public enum ResultEnum {

    OK(0),
    FILE_NOT_FOUND(1);

    private int value;

    static Map<Integer,ResultEnum> map = new HashMap<Integer,ResultEnum>();
    static {
        for (ResultEnum questionaryStatus : ResultEnum.values()) {
            map.put(questionaryStatus.getValue(), questionaryStatus);
        }
    }

    private ResultEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ResultEnum fromInteger(Integer value){
        return map.get(value);
    }

    @Override
    public String toString() {
        return name();
    }
}
