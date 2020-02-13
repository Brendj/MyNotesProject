/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menu.supplier;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: o.petrova
 * Date: 03.02.2020
 * Time: 11:59
 * To change this template use File | Settings | File Templates.
 */

public class MenuSupplier implements SectionRequest {

    protected static final String[] CLIENT_SECTION_NAMES = new String[] {
            "OrgGroupsRequest",
            "CategoryItemsRequest",
            "TypeProductionsRequest",
            "AgeGroupItemsRequest",
            "DietTypesRequest",
            "ComplexGroupItemsRequest",
            "GroupItemsRequest",
            "DishesRequest",
            "MenuGroupsRequest",
            "MenusRequest",
            "ComplexesRequest"};

    public static final String SECTION_NAME = "MenuSupplier";

    private List<Object> items;

    public MenuSupplier(Node menuSupplierNode) {

        this.items = new ArrayList<>();

        Node itemNode = menuSupplierNode.getFirstChild();
        int i = 0;
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals(CLIENT_SECTION_NAMES[i])) {
                MenuSupplierItem item = MenuSupplierItem.build(itemNode);
                items.add(item);
            }
            itemNode = itemNode.getNextSibling();
        }
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }

    public List<Object> getItems() {
        return items;
    }

    public void setItems(List<Object> items) {
        this.items = items;
    }
}
