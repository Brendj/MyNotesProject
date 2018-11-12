/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.dtiszn;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

public class ClientDiscountDTSZN implements AbstractToElement {
    private List<ClientDiscountDTSZNItem> items;

    public ClientDiscountDTSZN() {

    }

    public List<ClientDiscountDTSZNItem> getItems() {
        return items;
    }

    public void setItems(List<ClientDiscountDTSZNItem> items) {
        this.items = items;
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ClientDiscountsDTSZN");
        for (ClientDiscountDTSZNItem item : this.getItems()) {
            element.appendChild(item.toElement(document));
        }
        return element;
    }
}
