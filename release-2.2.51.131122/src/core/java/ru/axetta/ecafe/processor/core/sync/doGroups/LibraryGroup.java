/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.doGroups;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary.*;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 14.08.13
 * Time: 13:22
 */

public class LibraryGroup extends AbstractGroup {

    @Override
    protected void fill() {
        doClassMap.put("Publication", new DOSyncClass(Publication.class, 0));
        doClassMap.put("Source", new DOSyncClass(Source.class, 0));
        doClassMap.put("TypeOfAccompanyingDocument", new DOSyncClass(TypeOfAccompanyingDocument.class, 0));
        doClassMap.put("InventoryBook", new DOSyncClass(InventoryBook.class, 0));
        doClassMap.put("Fund", new DOSyncClass(Fund.class, 0)); // клиент не передает и не обрабатывает
        doClassMap.put("RetirementReason", new DOSyncClass(RetirementReason.class, 0));
        doClassMap.put("AccompanyingDocument", new DOSyncClass(AccompanyingDocument.class, 1));
        doClassMap.put("Journal", new DOSyncClass(Journal.class, 1));
        doClassMap.put("Ksu2Record", new DOSyncClass(Ksu2Record.class, 1));
        doClassMap.put("Ksu1Record", new DOSyncClass(Ksu1Record.class, 2));
        doClassMap.put("Instance", new DOSyncClass(Instance.class, 3));
        doClassMap.put("JournalItem", new DOSyncClass(JournalItem.class, 3));
        doClassMap.put("Issuable", new DOSyncClass(Issuable.class, 4));
        doClassMap.put("Circulation", new DOSyncClass(Circulation.class, 5));
    }
}
