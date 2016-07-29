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
    private long bindingToOrg;

    public GroupOrganizationItem(String name, long idOfOrg, Long bindingToOrg) {

        this.name = name;
        this.idOfOrg = idOfOrg;
        this.bindingToOrg = bindingToOrg;
    }

    public static GroupOrganizationItem build(Node node, long idOfOrg) throws Exception {
        String name = XMLUtils.getAttributeValue(node, "Name");
        String bindingToOrgStr = XMLUtils.getAttributeValue(node, "BindingToOrg");
        Long bindingToOrg = null;
        if (StringUtils.isNotEmpty(bindingToOrgStr)) {
            bindingToOrg = Long.parseLong(bindingToOrgStr);
        }
        return new GroupOrganizationItem(name, idOfOrg, bindingToOrg);
    }

    public String getName() {
        return name;
    }

    public long getIdOfOrg() {
        return idOfOrg;
    }

    public long getBindingToOrg() {
        return bindingToOrg;
    }
}
