/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ResponseDiscountClients extends Result {
    private String groupName;

    @JsonProperty("Clients")
    private List<ResponseDiscountClientsItem> items;

    public void addItem(ResponseDiscountClientsItem item) {
        if (this.items == null) this.items = new ArrayList<>();
        items.add(item);
    }

    public List<ResponseDiscountClientsItem> getItems() {
        return items;
    }

    public void setItems(List<ResponseDiscountClientsItem> items) {
        this.items = items;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
