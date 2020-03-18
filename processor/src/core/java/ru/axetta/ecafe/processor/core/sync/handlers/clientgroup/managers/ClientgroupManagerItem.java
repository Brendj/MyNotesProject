/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */
package ru.axetta.ecafe.processor.core.sync.handlers.clientgroup.managers;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

/**
 * User: akmukov
 * Date: 04.04.2016
 */
public class ClientgroupManagerItem {

    private String clientGroupName;
    private Long idOfClient;
    private Long orgOwner;
    private Integer deleteState;

    public ClientgroupManagerItem(Long idOfClient, String clientGroupName, Long orgOwner,Integer delete) {
        this.idOfClient = idOfClient;
        this.clientGroupName = clientGroupName;
        this.orgOwner = orgOwner;
        this.deleteState = delete;
    }

    public boolean wrongItem() {
        return idOfClient == null || clientGroupName == null || orgOwner == null;
    }

    public Integer getDeleteState() {
        return deleteState;
    }

    public String getClientGroupName() {
        return clientGroupName;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public Long getOrgOwner() {
        return orgOwner;
    }

    public static ClientgroupManagerItem build(Node itemNode) {
        String groupName = XMLUtils.getStringAttributeValue(itemNode, "GroupName",256);
        Long idOfClient = XMLUtils.getLongAttributeValue(itemNode, "IdOfClient");
        Long orgOwner = XMLUtils.getLongAttributeValue(itemNode, "OrgOwner");
        Integer delete = XMLUtils.getIntegerValueZeroSafe(itemNode, "D");
        return new ClientgroupManagerItem(idOfClient, groupName, orgOwner, delete);
    }
}
