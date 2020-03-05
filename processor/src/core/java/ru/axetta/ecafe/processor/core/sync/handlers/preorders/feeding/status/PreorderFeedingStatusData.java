/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding.status;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created by nuc on 18.02.2020.
 */
public class PreorderFeedingStatusData implements AbstractToElement {
    private List<PreorderFeedingStatusItem> items;

    public PreorderFeedingStatusData() {

    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("PreOrdersFeedingStatus");
        for (PreorderFeedingStatusItem item : this.getItems()) {
            element.appendChild(item.toElement(document, "PSI"));
        }
        return element;
    }

    public List<PreorderFeedingStatusItem> getItems() {
        return items;
    }

    public void setItems(List<PreorderFeedingStatusItem> items) {
        this.items = items;
    }
}
