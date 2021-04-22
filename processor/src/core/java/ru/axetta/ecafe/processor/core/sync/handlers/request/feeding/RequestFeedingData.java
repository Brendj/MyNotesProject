/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.request.feeding;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

public class RequestFeedingData implements AbstractToElement {
    private List<RequestFeedingItem> items;

    public RequestFeedingData() {}
    public RequestFeedingData(List<RequestFeedingItem> items) { this.items = items; }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("RequestFeeding");
        for (RequestFeedingItem item : this.getItems()) {
            element.appendChild(item.toElement(document, "RF"));
        }
        return element;
    }

    public List<RequestFeedingItem> getItems() {
        return items;
    }

    public void setItems(List<RequestFeedingItem> items) {
        this.items = items;
    }
}
