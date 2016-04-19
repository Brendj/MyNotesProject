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
public enum OrganizationSecurityLevel {

    /*0*/ STANDARD(0,"Стандартный"),
    /*1*/ EXTENDED(1,"Расширенный");

    private final Integer code;
    private final String description;

    static Map<Integer,OrganizationSecurityLevel> map = new HashMap<Integer,OrganizationSecurityLevel>();
    static {
        for (OrganizationSecurityLevel questionaryStatus : OrganizationSecurityLevel.values()) {
            map.put(questionaryStatus.getCode(), questionaryStatus);
        }
    }

    private OrganizationSecurityLevel(int code, String description) {
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

    public static OrganizationSecurityLevel fromInteger(Integer value){
        return map.get(value);
    }
}
