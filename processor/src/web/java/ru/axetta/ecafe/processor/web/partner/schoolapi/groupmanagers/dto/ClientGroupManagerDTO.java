/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.groupmanagers.dto;

import ru.axetta.ecafe.processor.core.persistence.ClientGroupManager;

public class ClientGroupManagerDTO {
    private long idOfGroupManager;
    private long idOfClient;
    private String groupName;
    private Long idOfClientGroup;
    private Long idOfOrg;

    public static ClientGroupManagerDTO from(ClientGroupManager clientGroupManager) {
        if (clientGroupManager == null) return null;
        ClientGroupManagerDTO result = new ClientGroupManagerDTO();
        result.setIdOfGroupManager(clientGroupManager.getIdOfClientGroupManager());
        result.setGroupName(clientGroupManager.getClientGroupName());
        result.setIdOfClient(clientGroupManager.getIdOfClient());
        result.setIdOfOrg(clientGroupManager.getOrgOwner());
        return result;
    }

    public Long getIdOfGroupManager() {
        return idOfGroupManager;
    }

    public void setIdOfGroupManager(long idOfGroupManager) {
        this.idOfGroupManager = idOfGroupManager;
    }

    public long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getIdOfClientGroup() {
        return idOfClientGroup;
    }

    public void setIdOfClientGroup(Long idOfClientGroup) {
        this.idOfClientGroup = idOfClientGroup;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }
}
