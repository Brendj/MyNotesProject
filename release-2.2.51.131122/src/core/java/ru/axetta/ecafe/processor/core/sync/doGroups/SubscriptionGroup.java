/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.doGroups;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.CycleDiagram;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.SubscriptionFeeding;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.*;

/**
 * Created with IntelliJ IDEA.
 * User: developer
 * Date: 14.08.13
 * Time: 13:41
 */

public class SubscriptionGroup extends AbstractGroup {

    @Override
    protected void fill() {
        doClassMap.put("CycleDiagram", new DOSyncClass(CycleDiagram.class, 0));
        doClassMap.put("SubscriptionFeeding", new DOSyncClass(SubscriptionFeeding.class, 0));
    }
}
