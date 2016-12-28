/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 28.12.16
 * Time: 10:37
 */

public enum TradeAccountConfigChange {

    // Флаг смены производственной конфигурации
    /*0*/ NOT_CHANGED(0,"Конфигурация не изменена"),
    /*1*/ CHANGED(1,"Конфигурация изменена");

    private final Integer code;
    private final String description;

    static Map<Integer,TradeAccountConfigChange> map = new HashMap<Integer,TradeAccountConfigChange>();
    static {
        for (TradeAccountConfigChange status : TradeAccountConfigChange.values()) {
            map.put(status.getCode(), status);
        }
    }

    private TradeAccountConfigChange(int code, String description) {
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

    public static TradeAccountConfigChange fromInteger(Integer value){
        return map.get(value);
    }
}
