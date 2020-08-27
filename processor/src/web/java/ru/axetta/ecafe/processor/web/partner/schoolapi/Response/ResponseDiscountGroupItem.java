/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.Response;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nuc on 26.06.2020.
 */
public class ResponseDiscountGroupItem {
    @JsonProperty("GroupName")
    private String groupName;

    @JsonProperty("Clients")
    private List<ResponseDiscountClientsItem> items;

    public ResponseDiscountGroupItem(String groupName) {
        this.groupName = groupName;
    }

    public void addItem(ResponseDiscountClientsItem item) {
        if (this.items == null) this.items = new ArrayList<>();
        items.add(item);
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<ResponseDiscountClientsItem> getItems() {
        return items;
    }

    public void setItems(List<ResponseDiscountClientsItem> items) {
        this.items = items;
    }
}
