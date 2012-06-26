/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 21.06.12
 * Time: 15:51
 * To change this template use File | Settings | File Templates.
 */
public enum DistributedObjectsEnum {
    ProductGuide("Products"),
    TechnologicalMap("TechnologicalMap"),
    TechnologicalMapProduct("TechnologicalMapProduct");

    private final String value;
    private DistributedObjectsEnum(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }

    public static DistributedObjectsEnum parse(String ids) {
        DistributedObjectsEnum distributedObjectsEnum = null; // Default
        for (DistributedObjectsEnum item : DistributedObjectsEnum.values()) {
            if (item.getValue().equalsIgnoreCase(ids)) {
                distributedObjectsEnum = item;
                break;
            }
        }
        return distributedObjectsEnum;
    }

    public static DistributedObjectsEnum parse(Class clazz) {
        DistributedObjectsEnum event = null; // Default
        String className = clazz.getSimpleName();
        for (DistributedObjectsEnum item : DistributedObjectsEnum.values()) {
            if (item.name().equalsIgnoreCase(className)) {
                event = item;
                break;
            }
        }
        return event;
    }
}
