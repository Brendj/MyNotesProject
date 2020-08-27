/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.requests.supplier;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

public class ResRequestsSupplier implements AbstractToElement {
    private List<ResRequestsSupplierItem> items;

    public ResRequestsSupplier() {
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ResRequestsSupplier");
        for (ResRequestsSupplierItem item : this.getItems()) {
            element.appendChild(item.toElement(document, "RRS"));
        }
        return element;
    }

    public List<ResRequestsSupplierItem> getItems() {
        return items;
    }

    public void setItems(List<ResRequestsSupplierItem> items) {
        this.items = items;
    }
}
