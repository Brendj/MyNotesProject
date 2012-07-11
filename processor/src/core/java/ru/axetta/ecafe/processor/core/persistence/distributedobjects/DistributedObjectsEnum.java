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
/* TODO: надежнее будет сделать поле value типа Class */
public enum DistributedObjectsEnum {
    ProductGroup("ProductGroup",0),
    TechnologicalMapGroup("TechnologicalMapGroup",0),
    Product("Product", 1),
    TechnologicalMap("TechnologicalMap", 1),
    TechnologicalMapProduct("TechnologicalMapProduct", 2),
    Circulation("Circulation", 1),
    Publication("Publication", 0);

    private final String value;
    /* приоритет обработки объектов при синхронизации */
    private final int priority;

    private DistributedObjectsEnum(String value, int priority) {
        this.value = value;
        this.priority = priority;
    }
    public String getValue() {
        return value;
    }

    public int getPriority() {
        return priority;
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
        DistributedObjectsEnum distributedObjectsEnum = null; // Default
        String className = clazz.getSimpleName();
        for (DistributedObjectsEnum item : DistributedObjectsEnum.values()) {
            if (item.name().equalsIgnoreCase(className)) {
                distributedObjectsEnum = item;
                break;
            }
        }
        return distributedObjectsEnum;
    }
}
