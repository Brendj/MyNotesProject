/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto;

import ru.axetta.ecafe.processor.core.persistence.ClientGroup;

import java.io.Serializable;

public class GroupClientsUpdateResponse implements Serializable {
    private Long idOfGroupClients;
    private Long idOfOrg;
    private Long bindingOrgId;
    private String groupName;
    private Boolean disableFromPlanLP;

    public static GroupClientsUpdateResponse from(ClientGroup clientGroup) {
        GroupClientsUpdateResponse response = new GroupClientsUpdateResponse();
        response.setGroupName(clientGroup.getGroupName());
        response.setIdOfOrg(clientGroup.getCompositeIdOfClientGroup().getIdOfOrg());
        response.setIdOfGroupClients(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
        return response;
    }

    public Long getIdOfGroupClients() {
        return idOfGroupClients;
    }

    public void setIdOfGroupClients(Long idOfGroupClients) {
        this.idOfGroupClients = idOfGroupClients;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getBindingOrgId() {
        return bindingOrgId;
    }

    public void setBindingOrgId(Long bindingOrgId) {
        this.bindingOrgId = bindingOrgId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Boolean getDisableFromPlanLP() {
        return disableFromPlanLP;
    }

    public void setDisableFromPlanLP(Boolean disableFromPlanLP) {
        this.disableFromPlanLP = disableFromPlanLP;
    }
}
