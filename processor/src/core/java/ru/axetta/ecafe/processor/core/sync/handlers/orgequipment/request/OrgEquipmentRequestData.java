/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.orgequipment.request;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

public class OrgEquipmentRequestData implements AbstractToElement {

    private List<ResOrgEquipmentRequestItem> items;
    public OrgEquipmentRequestData() {

    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("HardwareSettings");
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
