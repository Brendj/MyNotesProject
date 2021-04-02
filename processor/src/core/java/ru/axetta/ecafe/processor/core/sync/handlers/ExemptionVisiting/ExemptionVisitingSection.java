/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.ExemptionVisiting;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.LinkedList;
import java.util.List;

public class ExemptionVisitingSection implements AbstractToElement {

    private List<ExemptionVisitingSyncPOJO> items;

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ExemptionVisiting");
        for (ExemptionVisitingSyncPOJO item : getItems()) {
            element.appendChild(item.toElement(document));
        }
        return element;
    }

    public List<ExemptionVisitingSyncPOJO> getItems() {
        if (items == null) {
            items = new LinkedList<>();
        }
        return items;
    }

    public void setItems(List<ExemptionVisitingSyncPOJO> items) {
        this.items = items;
    }
}