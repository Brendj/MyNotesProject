/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.requests.supplier;

import java.util.HashMap;
import java.util.Map;

public enum RequestsSupplierTypeEnum {

    /*0*/ REQUEST_TYPE_DEFAULT("По умолчанию"),
    /*1*/ REQUEST_TYPE_SUBSCRIPTION("Абонементное питание"),
    /*2*/ REQUEST_TYPE_VARIATION("Вариативное питание"),
    /*3*/ REQUEST_TYPE_PREORDER("Предзаказы");

    private final String description;
    static Map<Integer, RequestsSupplierTypeEnum> map = new HashMap<>();
    static {
        for (RequestsSupplierTypeEnum requestStatus : RequestsSupplierTypeEnum.values()) {
            map.put(requestStatus.ordinal(), requestStatus);
        }
    }

    RequestsSupplierTypeEnum(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static RequestsSupplierTypeEnum fromInteger(Integer value) {
        return map.get(value);
    }
}
