/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal;

import java.util.ArrayList;
import java.util.List;

public class PayPlanBalanceListResponse extends ResponseItem {
    private List<PayPlanBalanceItem> items;

    public PayPlanBalanceListResponse() {
        this.items = new ArrayList<>();
    }

    public void addItem(PayPlanBalanceItem item) {
        this.items.add(item);
    }

    public List<PayPlanBalanceItem> getItems() {
        return items;
    }

    public void setItems(List<PayPlanBalanceItem> items) {
        this.items = items;
    }
}
