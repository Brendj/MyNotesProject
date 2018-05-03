/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.card.request;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 28.03.16
 * Time: 12:56
 * To change this template use File | Settings | File Templates.
 */
public class CardRequestsData implements AbstractToElement {
    private List<ResCardRequestItem> items;

    public CardRequestsData() {

    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("CardRequests");
        for (ResCardRequestItem item : this.getItems()) {
            element.appendChild(item.toElement(document, "CRQ"));
        }
        return element;
    }

    public List<ResCardRequestItem> getItems() {
        return items;
    }

    public void setItems(List<ResCardRequestItem> items) {
        this.items = items;
    }
}
