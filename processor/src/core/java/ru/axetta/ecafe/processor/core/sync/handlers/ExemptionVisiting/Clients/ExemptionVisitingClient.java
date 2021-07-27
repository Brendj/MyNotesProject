/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.ExemptionVisiting.Clients;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.LinkedList;
import java.util.List;

public class ExemptionVisitingClient implements AbstractToElement {

    private List<ExemptionVisitingClientPOjO> items;

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ExemptionVisitingClient");
        for (ExemptionVisitingClientPOjO item : getItems()) {
            element.appendChild(item.toElement(document));
        }
        return element;
    }

    public List<ExemptionVisitingClientPOjO> getItems() {
        if (items == null) {
            items = new LinkedList<>();
        }
        return items;
    }

    public void setItems(List<ExemptionVisitingClientPOjO> items) {
        this.items = items;
    }
}