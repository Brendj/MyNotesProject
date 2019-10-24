/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.goodrequestezd.request;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.DateFormat;
import java.util.LinkedList;
import java.util.List;

public class GoodRequestEZDSection implements AbstractToElement {

    private List<GoodRequestEZDSyncPOJO> items;

    public Element toElement(Document document,  DateFormat timeFormat) throws Exception {
        Element element = document.createElement("GoodRequestEZD");
        for (GoodRequestEZDSyncPOJO item : getItems()) {
            element.appendChild(item.toElement(document, timeFormat));
        }
        return element;
    }

    public List<GoodRequestEZDSyncPOJO> getItems() {
        if (items == null)
            items = new LinkedList<>();
        return items;
    }

    public void setItems(List<GoodRequestEZDSyncPOJO> items) {
        this.items = items;
    }

    @Override
    public Element toElement(Document document) throws Exception {
        return null;
    }
}