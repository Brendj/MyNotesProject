/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;
import ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items.ResHardwareSettingsRequestItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

public class ResHardwareSettingsRequest implements AbstractToElement {

    private List<ResHardwareSettingsRequestItem> items;

    public ResHardwareSettingsRequest() {
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ResHardwareSettingsRequest");
        for(ResHardwareSettingsRequestItem item : items) {
            element.appendChild(item.toElement(document));
        }
        return element;
    }

    public List<ResHardwareSettingsRequestItem> getItems() {
        return items;
    }

    public void setItems(List<ResHardwareSettingsRequestItem> items) {
        this.items = items;
    }
}
