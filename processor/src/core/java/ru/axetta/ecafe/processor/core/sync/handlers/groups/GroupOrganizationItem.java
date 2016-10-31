/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.groups;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

/**
 * User: akmukov
 * Date: 27.07.2016
 */
public class GroupOrganizationItem {
    private String name;
    private final long idOfOrg;
    private Long bindingToOrg;
    private Boolean isMiddleGroup;
    private String parentGroupName;
    private Boolean isNeedDeleteMiddleGroups;

    public GroupOrganizationItem(String name, long idOfOrg, Long bindingToOrg, Boolean isNeedDeleteMiddleGroups) {

        this.name = name;
        this.idOfOrg = idOfOrg;
        this.bindingToOrg = bindingToOrg;
        this.isNeedDeleteMiddleGroups = isNeedDeleteMiddleGroups;
    }

    public GroupOrganizationItem(String name, long idOfOrg, Long bindingToOrg, Boolean middleGroup,
            String parentGroupName) {

        this.name = name;
        this.idOfOrg = idOfOrg;
        this.bindingToOrg = bindingToOrg;

        this.isMiddleGroup = middleGroup;
        this.parentGroupName = parentGroupName;
    }

    public static GroupOrganizationItem build(Node node, long idOfOrg) throws Exception {
        String name = XMLUtils.getAttributeValue(node, "Name");
        String bindingToOrgStr = XMLUtils.getAttributeValue(node, "BindingToOrg");
        Long bindingToOrg = null;
        if (StringUtils.isNotEmpty(bindingToOrgStr)) {
            bindingToOrg = Long.parseLong(bindingToOrgStr);
        }

        String isNeedDeleteMiddleGroupsString = XMLUtils.getAttributeValue(node, "IsNeedDeleteMiddleGroups");

        boolean isNeedDeleteMiddleGroups = false;

        if (isNeedDeleteMiddleGroupsString != null) {
            if (isNeedDeleteMiddleGroupsString.equals("1")) {
                isNeedDeleteMiddleGroups = true;
            }
        }

        return new GroupOrganizationItem(name, idOfOrg, bindingToOrg, isNeedDeleteMiddleGroups);
    }

    public static GroupOrganizationItem buildSubGroup(Node node, long idOfOrg, String parentGroupName) throws Exception {
        String name = XMLUtils.getAttributeValue(node, "Name");
        String bindingToOrgStr = XMLUtils.getAttributeValue(node, "BindingToOrg");
        Long bindingToOrg = null;
        if (StringUtils.isNotEmpty(bindingToOrgStr)) {
            bindingToOrg = Long.parseLong(bindingToOrgStr);
        }

        return new GroupOrganizationItem(name, idOfOrg, bindingToOrg, true, parentGroupName);
    }

    public String getName() {
        return name;
    }

    public long getIdOfOrg() {
        return idOfOrg;
    }

    public Long getBindingToOrg() {
        return bindingToOrg;
    }

    public Boolean getMiddleGroup() {
        return isMiddleGroup;
    }

    public void setMiddleGroup(Boolean middleGroup) {
        isMiddleGroup = middleGroup;
    }

    public String getParentGroupName() {
        return parentGroupName;
    }

    public void setParentGroupName(String parentGroupName) {
        this.parentGroupName = parentGroupName;
    }

    public Boolean getNeedDeleteMiddleGroups() {
        return isNeedDeleteMiddleGroups;
    }

    public void setNeedDeleteMiddleGroups(Boolean needDeleteMiddleGroups) {
        isNeedDeleteMiddleGroups = needDeleteMiddleGroups;
    }
}
