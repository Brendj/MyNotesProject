/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto;

import ru.axetta.ecafe.processor.core.persistence.GroupNamesToOrgs;

import java.io.Serializable;

public class MiddleGroupResponse implements Serializable {
    private String name;
    private Long id;
    private Long bindingOrgId;
    private String parentGroupName;

    public static MiddleGroupResponse from(GroupNamesToOrgs groupNamesToOrgs) {
        MiddleGroupResponse response = new MiddleGroupResponse();
        response.setId(groupNamesToOrgs.getIdOfGroupNameToOrg());
        response.setName(groupNamesToOrgs.getGroupName());
        response.setBindingOrgId(groupNamesToOrgs.getIdOfOrg());
        response.setParentGroupName(groupNamesToOrgs.getParentGroupName());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBindingOrgId() {
        return bindingOrgId;
    }

    public void setBindingOrgId(Long bindingOrgId) {
        this.bindingOrgId = bindingOrgId;
    }

    public String getParentGroupName() {
        return parentGroupName;
    }

    public void setParentGroupName(String parentGroupName) {
        this.parentGroupName = parentGroupName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
