/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.Response.DTO;

import ru.axetta.ecafe.processor.core.persistence.ClientGroup;

import org.apache.commons.lang.NullArgumentException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class PlanOrderGroupDTO {
    @JsonProperty("GroupName")
    private String groupName;
    @JsonProperty("OrgId")
    private Long orgId;
    @JsonProperty("Clients")
    private List<PlanOrderClientDTO> clients = new ArrayList<>();
    @JsonIgnore
    private ClientGroup clientGroup;

    public PlanOrderGroupDTO(){

    }

    public PlanOrderGroupDTO(ClientGroup clientGroup){
        if(clientGroup == null)
            throw new NullArgumentException("ClientGroup can not be null");
        this.groupName = clientGroup.getGroupName();
        this.orgId = clientGroup.getOrg().getIdOfOrg();
        this.clientGroup = clientGroup;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public List<PlanOrderClientDTO> getClients() {
        return clients;
    }

    public void setClients(List<PlanOrderClientDTO> clients) {
        this.clients = clients;
        if(clients == null)
            this.clients = new ArrayList<>();
    }

    public ClientGroup getClientGroup() {
        return clientGroup;
    }

    public void setClientGroup(ClientGroup clientGroup) {
        if(clientGroup == null)
            throw new NullArgumentException("ClientGroup can not be null");
        this.groupName = clientGroup.getGroupName();
        this.orgId = clientGroup.getOrg().getIdOfOrg();
        this.clientGroup = clientGroup;
    }
}
