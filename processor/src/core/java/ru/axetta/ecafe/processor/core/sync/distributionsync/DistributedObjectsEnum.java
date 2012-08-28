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
    /* Накладная */
    ActOfInventorization(ActOfInventarization.class,0),
    ActOfWayBillDifference(ActOfWaybillDifference.class, 0),
    ActOfWayBillDifferencePosition(ActOfWaybillDifferencePosition.class, 3),
    GoodsRequest(GoodRequest.class, 0),
    GoodsRequestPosition(GoodRequestPosition.class,3),
    InternalDisposingDocument(InternalDisposingDocument.class,1),
    InternalDisposingDocumentPosition(InternalDisposingDocumentPosition.class, 4),
    InternalIncomingDocument(InternalIncomingDocument.class,2),
    InternalIncomingDocumentPosition(InternalIncomingDocumentPosition.class, 4),
    StateChanges(StateChange.class,3),
    WayBill(ru.axetta.ecafe.processor.core.persistence.distributedobjects.WayBill.class,1),
    WayBillPosition(WayBillPosition.class,3),
    Staff(Staff.class, 0),
    /* товары продукты */
    ProductGroup(ProductGroup.class,0),
    TechnologicalMapGroup(TechnologicalMapGroup.class,0),
    Product(Product.class, 1),
    GoodsGroups(GoodGroup.class,0),
    Goods(Good.class,2),
    TechnologicalMap(TechnologicalMap.class, 1),
    TechnologicalMapProduct(TechnologicalMapProduct.class, 2),
    TradeMaterialGoods(TradeMaterialGood.class,3),
    // библиоткека
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
