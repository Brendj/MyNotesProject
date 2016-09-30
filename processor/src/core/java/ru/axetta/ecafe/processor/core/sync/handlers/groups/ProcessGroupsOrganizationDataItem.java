/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.groups;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * User: akmukov
 * Date: 28.07.2016
 */
public class ProcessGroupsOrganizationDataItem {
    private String name;
    private long version;
    private Long bindingToOrg;
    private String parentGroupName;
    private Boolean isMiddleGroup;

    public ProcessGroupsOrganizationDataItem(String name, long version, Long bindingToOrg, String parentGroupName, Boolean isMiddleGroup) {
        this.name = name;
        this.version = version;
        this.bindingToOrg = bindingToOrg;
        this.parentGroupName = parentGroupName;
        this.isMiddleGroup = isMiddleGroup;
    }

    public long getVersion() {
        return version;
    }

    public Long getBindingToOrg() {
        return bindingToOrg;
    }

    public String getName() {

        return name;
    }

    public String getParentGroupName() {
        return parentGroupName;
    }

    public void setParentGroupName(String parentGroupName) {
        this.parentGroupName = parentGroupName;
    }

    public Boolean getMiddleGroup() {
        return isMiddleGroup;
    }

    public void setMiddleGroup(Boolean middleGroup) {
        isMiddleGroup = middleGroup;
    }

    public Node toElement(Document document) {
        Element element = document.createElement("CG");
        XMLUtils.setAttributeIfNotNull(element, "Name", name);
        XMLUtils.setAttributeIfNotNull(element, "V", Long.toString(version));
        if (bindingToOrg != null) {
            XMLUtils.setAttributeIfNotNull(element,"BindingToOrg",Long.toString(bindingToOrg));
        }
        return element;
    }

    public Node toSubGroupElement(Document document) {
        Element element = document.createElement("CMG");
        XMLUtils.setAttributeIfNotNull(element, "Name", name);
        XMLUtils.setAttributeIfNotNull(element, "V", Long.toString(version));
        if (bindingToOrg != null) {
            XMLUtils.setAttributeIfNotNull(element,"BindingToOrg",Long.toString(bindingToOrg));
        }
        return element;
    }
}
