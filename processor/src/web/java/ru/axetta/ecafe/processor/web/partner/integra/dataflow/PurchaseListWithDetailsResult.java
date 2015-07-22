/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

/**
 * Created with IntelliJ IDEA.
 * User: Алмаз
 * Date: 22.07.15
 * Time: 17:51
 * To change this template use File | Settings | File Templates.
 */
public class PurchaseListWithDetailsResult extends Result {

    public PurchaseListWithDetailsExt purchaseListWithDetailsExt;

    public PurchaseListWithDetailsExt getPurchaseListWithDetailsExt() {
        return purchaseListWithDetailsExt;
    }

    public void setPurchaseListWithDetailsExt(PurchaseListWithDetailsExt purchaseListWithDetailsExt) {
        this.purchaseListWithDetailsExt = purchaseListWithDetailsExt;
    }
}
