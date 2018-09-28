/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.balance.hold;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

public class ClientBalanceHoldFeeding implements AbstractToElement {
    private List<ClientBalanceHoldItem> items;

    public ClientBalanceHoldFeeding() {

    }

    public List<ClientBalanceHoldItem> getItems() {
        return items;
    }

    public void setItems(List<ClientBalanceHoldItem> items) {
        this.items = items;
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ClientBalanceHold");
        for (ClientBalanceHoldItem item : this.getItems()) {
            element.appendChild(item.toElement(document));
        }
        return element;
    }
}
