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

    public ProcessGroupsOrganizationDataItem(String name, long version, Long bindingToOrg) {
        this.name = name;
        this.version = version;
        this.bindingToOrg = bindingToOrg;
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

    public Node toElement(Document document) {
        Element element = document.createElement("CG");
        XMLUtils.setAttributeIfNotNull(element, "Name", name);
        XMLUtils.setAttributeIfNotNull(element, "V", Long.toString(version));
        if (bindingToOrg != null) {
            XMLUtils.setAttributeIfNotNull(element,"BindingToOrg",Long.toString(bindingToOrg));
        }
        return element;
    }
}
