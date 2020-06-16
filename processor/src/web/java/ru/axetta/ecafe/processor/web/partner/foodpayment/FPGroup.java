/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nuc on 15.06.2020.
 */
public class FPGroup {
    @JsonProperty("GroupName")
    private String groupName;

    @JsonProperty("GroupId")
    private Long groupId;

    @JsonProperty("Clients")
    private List<FPClient> clients;

    public FPGroup() {
        this.clients = new ArrayList<>();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public List<FPClient> getClients() {
        return clients;
    }

    public void setClients(List<FPClient> clients) {
        this.clients = clients;
    }
}
