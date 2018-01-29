/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.help.request;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

public class ResHelpRequest implements AbstractToElement {
    private List<ResHelpRequestItem> items;

    public ResHelpRequest() {

    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ResHelpRequests");
        for (ResHelpRequestItem item : this.getItems()) {
            element.appendChild(item.toElement(document, "RHR"));
        }
        return element;
    }

    public List<ResHelpRequestItem> getItems() {
        return items;
    }

    public void setItems(List<ResHelpRequestItem> items) {
        this.items = items;
    }
}
