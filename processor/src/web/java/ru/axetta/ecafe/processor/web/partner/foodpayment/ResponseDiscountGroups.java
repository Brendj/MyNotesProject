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
    private List<ResponseDiscountClients> clients;

    public void addItem(ResponseDiscountClients item) {
        if (this.clients == null) this.clients = new ArrayList<>();
        clients.add(item);
    }

    public List<ResponseDiscountClients> getClients() {
        return clients;
    }

    public void setClients(List<ResponseDiscountClients> clients) {
        this.clients = clients;
    }
}
