/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nuc on 26.06.2020.
 */
public class ResponseDiscountGroups extends Result {
    @JsonProperty("Groups")
    private List<ResponseDiscountGroupItem> clients;

    public void addItem(ResponseDiscountGroupItem item) {
        if (this.clients == null) this.clients = new ArrayList<>();
        clients.add(item);
    }

    public List<ResponseDiscountGroupItem> getClients() {
        return clients;
    }

    public void setClients(List<ResponseDiscountGroupItem> clients) {
        this.clients = clients;
    }
}
