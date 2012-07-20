/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.distributionsync;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 21.06.12
 * Time: 15:51
 * To change this template use File | Settings | File Templates.
 */
/* TODO: надежнее будет сделать поле value типа Class */
public enum DistributedObjectsEnum {
    ProductGroup(ru.axetta.ecafe.processor.core.persistence.distributedobjects.ProductGroup.class,0),
    TechnologicalMapGroup(ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMapGroup.class,0),
    Product(ru.axetta.ecafe.processor.core.persistence.distributedobjects.Product.class, 1),
    TechnologicalMap(ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMap.class, 1),
    TechnologicalMapProduct(ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMapProduct.class, 2),
    Publication(Publication.class, 0),
    Issuable(Issuable.class, 1),
    Circulation(Circulation.class, 2);

    private final Class<? extends DistributedObject> value;
    /* приоритет обработки объектов при синхронизации */
    private final int priority;

    private DistributedObjectsEnum(Class<? extends DistributedObject> value, int priority) {
        this.priority = priority;
        this.value = value;
    }

    public Class<? extends DistributedObject> getValue() {
        return value;
    }

    public int getPriority() {
        return priority;
    }

    public static DistributedObjectsEnum parse(String ids) {
        DistributedObjectsEnum distributedObjectsEnum = null; // Default
        for (DistributedObjectsEnum item : DistributedObjectsEnum.values()) {
            if (item.name().equalsIgnoreCase(ids)) {
                distributedObjectsEnum = item;
                break;
            }
        }
        return distributedObjectsEnum;
    }

    public static DistributedObjectsEnum parse(Class clazz) {
        DistributedObjectsEnum distributedObjectsEnum = null;
        for (DistributedObjectsEnum item : DistributedObjectsEnum.values()) {
            if (item.getValue().equals(clazz)) {
                distributedObjectsEnum = item;
                break;
            }
        }
        return distributedObjectsEnum;
    }
}
