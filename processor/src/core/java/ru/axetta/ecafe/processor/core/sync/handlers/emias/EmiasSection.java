/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.emias;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.LinkedList;
import java.util.List;

public class EmiasSection implements AbstractToElement {

    private List<EMIASSyncPOJO> items;

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("EMIAS");
        for (EMIASSyncPOJO item : getItems()) {
            element.appendChild(item.toElement(document));
        }
        return element;
    }

    public List<EMIASSyncPOJO> getItems() {
        if (items == null) {
            items = new LinkedList<>();
        }
        return items;
    }

    public void setItems(List<EMIASSyncPOJO> items) {
        this.items = items;
    }
}