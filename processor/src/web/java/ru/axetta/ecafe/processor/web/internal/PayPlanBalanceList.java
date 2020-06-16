/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal;

import java.util.ArrayList;
import java.util.List;

public class PayPlanBalanceList {
    private List<PayPlanBalanceItem> items;

    public PayPlanBalanceList() {
        this.items = new ArrayList<>();
    }

    public List<PayPlanBalanceItem> getItems() {
        return items;
    }

    public void setItems(List<PayPlanBalanceItem> items) {
        this.items = items;
    }
}
