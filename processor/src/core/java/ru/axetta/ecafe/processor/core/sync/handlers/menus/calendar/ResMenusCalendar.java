/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menus.calendar;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created by baloun on 15.06.2018.
 */
public class ResMenusCalendar implements AbstractToElement {
    private List<ResMenusCalendarItem> items;

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ResMenusCalendar");
        for (ResMenusCalendarItem item : this.getItems()) {
            element.appendChild(item.toResElement(document, "RMCI"));
        }
        return element;
    }

    public List<ResMenusCalendarItem> getItems() {
        return items;
    }

    public void setItems(List<ResMenusCalendarItem> items) {
        this.items = items;
    }
}
