/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto;

import java.io.Serializable;

public class MiddleGroupRequest implements Serializable {

    private Long id;
    private String name;
    private Long bindingOrgId;
    private String parentGroupName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

}
