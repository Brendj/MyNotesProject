/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.requests.supplier;

import java.util.HashMap;
import java.util.Map;

public enum RequestsSupplierDetailTypeEnum {

    /*0*/ REQUEST_TYPE_GENERAL("Общий тип"),
    /*1*/ REQUEST_TYPE_DISCOUNT("Льготное питание"),
    /*2*/ REQUEST_TYPE_PAID("Платное питание"),
    /*3*/ REQUEST_TYPE_SUBSCRIPTION("Абонементное питание"),
    /*4*/ REQUEST_TYPE_VARIATION("Вариативное питание"),
    /*5*/ REQUEST_TYPE_PREORDER("Предзаказы");

    private final String description;
    static Map<Integer, RequestsSupplierDetailTypeEnum> map = new HashMap<>();
    static {
        for (RequestsSupplierDetailTypeEnum requestStatus : RequestsSupplierDetailTypeEnum.values()) {
            map.put(requestStatus.ordinal(), requestStatus);
        }
    }

    RequestsSupplierDetailTypeEnum(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static RequestsSupplierDetailTypeEnum fromInteger(Integer value) {
        return map.get(value);
    }
}
