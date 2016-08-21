/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.clientphoto;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 13.04.16
 * Time: 12:05
 */
public class ClientPhotosData implements AbstractToElement {
    private List<ResClientPhotosItem> items;

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ClientsPhotos");
        for (ResClientPhotosItem item : this.getItems()) {
            element.appendChild(item.toElement(document, "CP"));
        }
        return element;
    }

    public ClientPhotosData() {
    }

    public List<ResClientPhotosItem> getItems() {
        return items;
    }

    public void setItems(List<ResClientPhotosItem> items) {
        this.items = items;
    }
}
