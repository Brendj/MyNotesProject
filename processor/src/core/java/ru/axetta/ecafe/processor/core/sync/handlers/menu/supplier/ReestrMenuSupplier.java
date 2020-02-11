/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menu.supplier;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;

import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: o.petrova
 * Date: 03.02.2020
 * Time: 11:40
 * To change this template use File | Settings | File Templates.
 */

public class ReestrMenuSupplier implements SectionRequest {

    public static final String SECTION_NAME = "MenuSupplier";
    private final Map<String, String> versions;

    public ReestrMenuSupplier(Node menuSupplierRequestNode) {
        this.versions = new HashMap<>();

        Node itemNode = menuSupplierRequestNode.getFirstChild();
        if (itemNode != null && Node.ELEMENT_NODE == itemNode.getNodeType()) {
            versions.put(itemNode.getNodeName(), itemNode.getAttributes().getNamedItem("V").getNodeValue());
        }
    }

    public Map<String, String> getVersions() {
        return versions;
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }
}
