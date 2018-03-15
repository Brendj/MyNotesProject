/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

public class PreOrdersFeeding implements AbstractToElement {
    private List<PreOrdersFeedingItem> items;

    public PreOrdersFeeding() {

    }

    public List<PreOrdersFeedingItem> getItems() {
        return items;
    }

    public void setItems(List<PreOrdersFeedingItem> items) {
        this.items = items;
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("PreOrdersFeeding");
        for (PreOrdersFeedingItem item : this.getItems()) {
            element.appendChild(item.toElement(document));
        }
        return element;
    }
}
