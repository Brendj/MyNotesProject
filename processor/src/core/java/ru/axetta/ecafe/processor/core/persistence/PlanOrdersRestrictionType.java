/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by i.semenov on 27.01.2020.
 */
public enum PlanOrdersRestrictionType {
    /*0*/ UNKNOWN("Неизвестно"),
    /*1*/ LP("ЛП"),
    /*2*/ PP("ПП");

    private final String description;
    static Map<Integer,PlanOrdersRestrictionType> map = new HashMap<Integer,PlanOrdersRestrictionType>();
    static {
        for (PlanOrdersRestrictionType questionaryStatus : PlanOrdersRestrictionType.values()) {
            map.put(questionaryStatus.ordinal(), questionaryStatus);
        }
    }

    PlanOrdersRestrictionType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static PlanOrdersRestrictionType fromInteger(Integer value){
        return map.get(value);
    }
}
