/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.orgequipment.request;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

public class ResOrgEquipmentRequest implements AbstractToElement {

    private List<ResOrgEquipmentRequestItem> items;

    public ResOrgEquipmentRequest() {
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ResOrgEquipmentRequest");
        for(ResOrgEquipmentRequestItem item : items) {
            element.appendChild(item.toElement(document));
        }
        return element;
    }

    public List<ResOrgEquipmentRequestItem> getItems() {
        return items;
    }

    public void setItems(List<ResOrgEquipmentRequestItem> items) {
        this.items = items;
    }

}
