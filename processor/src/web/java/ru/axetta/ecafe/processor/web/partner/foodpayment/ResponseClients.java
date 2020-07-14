/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ResponseClients extends Result {

    @JsonProperty("Groups")
    private List<FPGroup> groups;

    public ResponseClients() {
        this.groups = new ArrayList<>();
    }

    public List<FPGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<FPGroup> groups) {
        this.groups = groups;
    }
}
