/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menu.supplier;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: o.petrova
 * Date: 03.02.2020
 * Time: 11:40
 * To change this template use File | Settings | File Templates.
 */

public class ReestrMenuSupplier implements SectionRequest {

    public static final String SECTION_NAME = "MenuSupplier";

    private final List<MenuSupplierItem> items;
    private final Long maxVersion;

    public ReestrMenuSupplier(Node menuSupplierRequestNode, Long orgOwner) {
        maxVersion = XMLUtils.getLongAttributeValue(menuSupplierRequestNode, "V");
        this.items = new ArrayList<>();

        Node itemNode = menuSupplierRequestNode.getFirstChild();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("MS")) {
                MenuSupplierItem item = MenuSupplierItem.build(itemNode);
                items.add(item);
            }
            itemNode = itemNode.getNextSibling();
        }
    }

    public List<MenuSupplierItem> getItems() {
        return items;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }
}
