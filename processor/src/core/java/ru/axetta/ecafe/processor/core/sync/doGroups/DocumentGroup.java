/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.doGroups;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.*;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 14.08.13
 * Time: 12:49
 */

public class DocumentGroup extends AbstractGroup {

    @Override
    protected void fill() {
        doClassMap.put("ActOfInventarization", new DOSyncClass(ActOfInventarization.class, 0));
        doClassMap.put("ActOfWayBillDifference", new DOSyncClass(ActOfWayBillDifference.class, 0));
        doClassMap.put("ActOfWayBillDifferencePosition", new DOSyncClass(ActOfWayBillDifferencePosition.class, 0));
        doClassMap.put("GoodRequest", new DOSyncClass(GoodRequest.class, 0));
        doClassMap.put("GoodRequestPosition", new DOSyncClass(GoodRequestPosition.class, 3));
        doClassMap.put("InternalDisposingDocument", new DOSyncClass(InternalDisposingDocument.class, 1));
        doClassMap.put("InternalDisposingDocumentPosition", new DOSyncClass(InternalDisposingDocumentPosition.class, 4));
        doClassMap.put("InternalIncomingDocument", new DOSyncClass(InternalIncomingDocument.class, 2));
        doClassMap.put("InternalIncomingDocumentPosition", new DOSyncClass(InternalIncomingDocumentPosition.class, 4));
        doClassMap.put("StateChange", new DOSyncClass(StateChange.class, 3));
        doClassMap.put("WayBill", new DOSyncClass(WayBill.class, 1));
        doClassMap.put("WayBillPosition", new DOSyncClass(WayBillPosition.class, 3));
        doClassMap.put("Staff", new DOSyncClass(Staff.class, 0));
    }
}
