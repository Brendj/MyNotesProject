/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.Response;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.HashSet;
import java.util.Set;

public class ResponseDiscounts extends Result {
    @JsonProperty("Discounts")
    private Set<ResponseDiscountItem> items;

    public void addItem(ResponseDiscountItem item) {
        if (this.items == null) this.items = new HashSet<>();
        items.add(item);
    }

    public Set<ResponseDiscountItem> getItems() {
        return items;
    }

    public void setItems(Set<ResponseDiscountItem> items) {
        this.items = items;
    }
}
