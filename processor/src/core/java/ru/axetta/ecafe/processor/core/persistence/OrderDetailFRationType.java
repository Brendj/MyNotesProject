/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nuc on 05.02.2020.
 */
public enum OrderDetailFRationType {
    NOT_SPECIFIED(0,"Не указано"),
    BREAKFAST(1,"Завтрак"),
    DINNER(2, "Обед"),
    LUNCH(3, "Полдник"),
    SUPPER(4, "Ужин"),
    SECOND_BREAKFAST(5, "Второй завтрак"),
    WATER(6, "Вода"),
    SECOND_DINNER(7, "Второй ужин");


    private final Integer code;
    private final String description;

    static Map<Integer, OrderDetailFRationType> map = new HashMap<Integer, OrderDetailFRationType>();
    static {
        for (OrderDetailFRationType questionaryStatus : OrderDetailFRationType.values()) {
            map.put(questionaryStatus.getCode(), questionaryStatus);
        }
    }

    private OrderDetailFRationType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String toString() {
        return description;
    }

    public static OrderDetailFRationType fromInteger(Integer value){
        return map.get(value);
    }
}

