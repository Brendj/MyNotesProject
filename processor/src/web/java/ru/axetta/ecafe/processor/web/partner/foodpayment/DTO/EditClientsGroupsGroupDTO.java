/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment.DTO;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class  EditClientsGroupsGroupDTO {
    @JsonProperty("OldGroupName")
    private String oldGroupName;

    @JsonProperty("NewGroupName")
    private String newGroupName;

    @JsonProperty("OrgId")
    private Long orgId;

    @JsonProperty("Clients")
    private List<EditClientsGroupsClientDTO> clients;

    public String getOldGroupName() {
        return oldGroupName;
    }

    public void setOldGroupName(String oldGroupName) {
        this.oldGroupName = oldGroupName;
    }

    public String getNewGroupName() {
        return newGroupName;
    }

    public void setNewGroupName(String newGroupName) {
        this.newGroupName = newGroupName;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public List<EditClientsGroupsClientDTO> getClients() {
        return clients;
    }

    public void setClients(List<EditClientsGroupsClientDTO> clients) {
        this.clients = clients;
    }
}
