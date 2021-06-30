/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.request.feeding;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

public class ResRequestFeeding implements AbstractToElement {
    private List<ResRequestFeedingItem> items;
    private List<ResRequestFeedingETPStatuses> statuses;

    public ResRequestFeeding() { }
    public ResRequestFeeding(List<ResRequestFeedingItem> items, List<ResRequestFeedingETPStatuses> statuses) {
        this.items = items;
        this.statuses = statuses;
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ResRequestFeeding");
        for (ResRequestFeedingItem item : this.getItems()) {
            element.appendChild(item.toElement(document, "RF"));
        }
        return element;
    }

    public List<ResRequestFeedingItem> getItems() {
        return items;
    }

    public void setItems(List<ResRequestFeedingItem> items) {
        this.items = items;
    }

    public List<ResRequestFeedingETPStatuses> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<ResRequestFeedingETPStatuses> statuses) {
        this.statuses = statuses;
    }
}
