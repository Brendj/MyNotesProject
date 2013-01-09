/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.manager;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 21.06.12
 * Time: 15:51
 * To change this template use File | Settings | File Templates.
 */
public enum DistributedObjectsEnum {
    /* Накладная */
    ActOfInventorization(ActOfInventarization.class,0),
    ActOfWayBillDifference(ActOfWayBillDifference.class, 0),
    ActOfWayBillDifferencePosition(ActOfWayBillDifferencePosition.class, 3),
    GoodRequest(GoodRequest.class, 0),
    GoodRequestPosition(GoodRequestPosition.class,3),
    InternalDisposingDocument(InternalDisposingDocument.class,1),
    InternalDisposingDocumentPosition(InternalDisposingDocumentPosition.class, 4),
    InternalIncomingDocument(InternalIncomingDocument.class,2),
    InternalIncomingDocumentPosition(InternalIncomingDocumentPosition.class, 4),
    StateChange(StateChange.class,3),
    WayBill(WayBill.class,1),
    WayBillPosition(WayBillPosition.class,3),
    Staff(Staff.class, 0),
     // товары продукты
    ProductGroup(ProductGroup.class,0),
    TechnologicalMapGroup(TechnologicalMapGroup.class,0),
    Product(Product.class, 1),
    GoodGroup(GoodGroup.class,0),
    Good(Good.class,2),
    GoodComplaintBook(GoodComplaintBook.class, 3),
    GoodComplaintIterations(GoodComplaintIterations.class, 4),
    GoodComplaintCauses(GoodComplaintCauses.class, 5),
    GoodComplaintOrders(GoodComplaintOrders.class, 5),
    TechnologicalMap(TechnologicalMap.class, 1),
    TechnologicalMapProduct(TechnologicalMapProduct.class, 2),
    TradeMaterialGood(TradeMaterialGood.class,3),
    Prohibition(Prohibition.class, 3),
    ProhibitionExclusion(ProhibitionExclusion.class, 4),
    // настойки
    ECafeSettings(ECafeSettings.class,0),
    // библиоткека
    Publication(Publication.class, 0),
    Source(Source.class,0),
    TypeOfAccompanyingDocument(TypeOfAccompanyingDocument.class,0),
    InventoryBook(InventoryBook.class,0),
    Fund(Fund.class,0),
    RetirementReason(RetirementReason.class,0),
    AccompanyingDocument(AccompanyingDocument.class, 1),
    Journal(Journal.class, 1),
    Ksu2Record(Ksu2Record.class,1),
    Ksu1Record(Ksu1Record.class, 2),
    Instance(Instance.class,3),
    JournalItem(JournalItem.class,3),
    Issuable(Issuable.class, 4),
    Circulation(Circulation.class, 5);

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
