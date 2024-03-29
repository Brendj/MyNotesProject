/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 05.09.13
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public enum PhotoRegistryDirective {

    /*0*/ DISALLOWED(0,"Запрещено"),
    /*1*/ ALLOWED(1,"Разрешено");

    private final Integer code;
    private final String description;

    static Map<Integer,PhotoRegistryDirective> map = new HashMap<Integer,PhotoRegistryDirective>();
    static {
        for (PhotoRegistryDirective status : PhotoRegistryDirective.values()) {
            map.put(status.getCode(), status);
        }
    }

    private PhotoRegistryDirective(int code, String description) {
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

    public static PhotoRegistryDirective fromInteger(Integer value){
        return map.get(value);
    }
}
