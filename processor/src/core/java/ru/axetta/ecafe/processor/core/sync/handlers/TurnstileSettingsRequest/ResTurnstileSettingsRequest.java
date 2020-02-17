/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.TurnstileSettingsRequest;


import ru.axetta.ecafe.processor.core.sync.AbstractToElement;
import ru.axetta.ecafe.processor.core.sync.handlers.TurnstileSettingsRequest.items.ResTurnstileSettingsRequestItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

public class ResTurnstileSettingsRequest implements AbstractToElement {

    private List<ResTurnstileSettingsRequestItem> items;

    public ResTurnstileSettingsRequest() {

    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ResTurnstileSettingsRequest");
        for (ResTurnstileSettingsRequestItem item : this.getItems()) {
            element.appendChild(item.toElement(document));
        }
        return element;
    }

    public List<ResTurnstileSettingsRequestItem> getItems() {
        return items;
    }

    public void setItems(List<ResTurnstileSettingsRequestItem> items) {
        this.items = items;
    }
}
