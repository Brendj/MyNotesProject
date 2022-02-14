/*
 * Copyright (c) 2022. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.foodBox.FoodBoxPreorder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import java.util.ArrayList;
import java.util.List;

public class FoodBoxPreorderNew implements AbstractToElement {

    private List<FoodBoxPreorderNewItem> items;

    public FoodBoxPreorderNew() {
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("FoodBoxPreorder");
        for (FoodBoxPreorderNewItem item : this.getItems()) {
            element.appendChild(item.toElement(document,"FBP"));
        }
        return element;
    }

    public List<FoodBoxPreorderNewItem> getItems() {
        if (items == null)
            items = new ArrayList<>();
        return items;
    }

    public void setItems(List<FoodBoxPreorderNewItem> items) {
        this.items = items;
    }
}
