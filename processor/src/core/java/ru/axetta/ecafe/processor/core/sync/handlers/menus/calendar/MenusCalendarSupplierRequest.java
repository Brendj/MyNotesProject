/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menus.calendar;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baloun on 15.06.2018.
 */
public class MenusCalendarSupplierRequest implements SectionRequest {
    public static final String SECTION_NAME="MenusCalendar";
    private final List<MenusCalendarItem> items = new ArrayList<MenusCalendarItem>();

    public MenusCalendarSupplierRequest(Node menusCalendarRequestNode) {
        Node itemNode = menusCalendarRequestNode.getFirstChild();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("MCI")) {
                MenusCalendarItem item = MenusCalendarItem.build(itemNode);
                items.add(item);
            }
            itemNode = itemNode.getNextSibling();
        }
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }

    public List<MenusCalendarItem> getItems() {
        return items;
    }

}
