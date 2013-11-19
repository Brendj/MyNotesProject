/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.doGroups;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.CycleDiagram;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.SubscriptionFeeding;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.*;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 14.08.13
 * Time: 13:41
 */

public class ProductsGroup extends AbstractGroup {

    @Override
    protected void fill() {
        doClassMap.put("ProductGroup", new DOSyncClass(ProductGroup.class, 0));
        doClassMap.put("TechnologicalMapGroup", new DOSyncClass(TechnologicalMapGroup.class, 0));
        doClassMap.put("Product", new DOSyncClass(Product.class, 1));
        doClassMap.put("GoodGroup", new DOSyncClass(GoodGroup.class, 0));
        doClassMap.put("Good", new DOSyncClass(Good.class, 2));
        doClassMap.put("GoodComplaintBook", new DOSyncClass(GoodComplaintBook.class, 3));
        doClassMap.put("GoodComplaintIterations", new DOSyncClass(GoodComplaintIterations.class, 4));
        doClassMap.put("GoodComplaintCauses", new DOSyncClass(GoodComplaintCauses.class, 5));
        doClassMap.put("GoodComplaintOrders", new DOSyncClass(GoodComplaintOrders.class, 5));
        doClassMap.put("TechnologicalMap", new DOSyncClass(TechnologicalMap.class, 1));
        doClassMap.put("TechnologicalMapProduct", new DOSyncClass(TechnologicalMapProduct.class, 2));
        doClassMap.put("TradeMaterialGood", new DOSyncClass(TradeMaterialGood.class, 3));
        doClassMap.put("Prohibition", new DOSyncClass(Prohibition.class, 3));
        doClassMap.put("ProhibitionExclusion", new DOSyncClass(ProhibitionExclusion.class, 4));
        doClassMap.put("GoodBasicBasketPrice", new DOSyncClass(GoodBasicBasketPrice.class, 3));
        doClassMap.put("CycleDiagram", new DOSyncClass(CycleDiagram.class, 0));
        doClassMap.put("SubscriptionFeeding", new DOSyncClass(SubscriptionFeeding.class, 0));
    }
}
