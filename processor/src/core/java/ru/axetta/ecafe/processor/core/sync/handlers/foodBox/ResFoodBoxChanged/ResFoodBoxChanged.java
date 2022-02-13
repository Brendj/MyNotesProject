/*
 * Copyright (c) 2022. Axetta LLC. All Rights Reserved.
 */


package ru.axetta.ecafe.processor.core.sync.handlers.foodBox.ResFoodBoxChanged;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import java.util.ArrayList;
import java.util.List;

public class ResFoodBoxChanged implements AbstractToElement {

    private List<ResFoodBoxChangedItem> items;

    public ResFoodBoxChanged() {
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ResFoodBoxPreorder");
        for (ResFoodBoxChangedItem item : this.getItems()) {
            element.appendChild(item.toElement(document,"RFBP"));
        }
        return element;
    }

    public List<ResFoodBoxChangedItem> getItems() {
        if (items == null)
            items = new ArrayList<>();
        return items;
    }

    public void setItems(List<ResFoodBoxChangedItem> items) {
        this.items = items;
    }
}
