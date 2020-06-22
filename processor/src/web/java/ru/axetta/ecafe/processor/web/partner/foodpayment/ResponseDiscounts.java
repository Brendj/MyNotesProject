/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ResponseDiscounts extends Result {
    @JsonProperty("Discounts")
    private List<ResponseDiscountItem> items;

    public void addItem(ResponseDiscountItem item) {
        if (this.items == null) this.items = new ArrayList<>();
        items.add(item);
    }

    public List<ResponseDiscountItem> getItems() {
        return items;
    }

    public void setItems(List<ResponseDiscountItem> items) {
        this.items = items;
    }
}
