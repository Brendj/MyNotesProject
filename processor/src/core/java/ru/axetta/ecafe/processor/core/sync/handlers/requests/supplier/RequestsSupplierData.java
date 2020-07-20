/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.requests.supplier;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

public class RequestsSupplierData implements AbstractToElement {
    private List<ResRequestsSupplierItem> items;

    public RequestsSupplierData() {
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("RequestsSupplier");
        for (ResRequestsSupplierItem item : this.getItems()) {
            element.appendChild(item.toElement(document, "RI"));
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
