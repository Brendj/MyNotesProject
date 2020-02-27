/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding.status;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created by nuc on 14.02.2020.
 */
public class ResPreorderFeedingStatus implements AbstractToElement {
    private List<ResPreorderFeedingStatusItem> items;

    public ResPreorderFeedingStatus() {

    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ResPreOrdersFeedingStatus");
        for (ResPreorderFeedingStatusItem item : this.getItems()) {
            element.appendChild(item.toElement(document, "RePS"));
        }
        return element;
    }

    public List<ResPreorderFeedingStatusItem> getItems() {
        return items;
    }

    public void setItems(List<ResPreorderFeedingStatusItem> items) {
        this.items = items;
    }
}
