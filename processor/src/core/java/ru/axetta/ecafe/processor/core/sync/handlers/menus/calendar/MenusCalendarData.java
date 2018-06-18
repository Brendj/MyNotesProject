/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menus.calendar;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.List;

/**
 * Created by i.semenov on 14.06.2018.
 */
public class MenusCalendarData implements AbstractToElement {
    private List<MenusCalendarItem> items;

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("MenusCalendar");
        for (MenusCalendarItem item : this.getItems()) {
            Node node = element.appendChild(item.toElement(document, "MCI"));
            if (item.getItems() != null) {
                for (MenusCalendarDateItem item1 : item.getItems()) {
                    node.appendChild(item1.toElement(document, "CDI"));
                }
            }
        }
        return element;
    }

    public MenusCalendarData() {
    }

    public List<MenusCalendarItem> getItems() {
        return items;
    }

    public void setItems(List<MenusCalendarItem> items) {
        this.items = items;
    }
}
