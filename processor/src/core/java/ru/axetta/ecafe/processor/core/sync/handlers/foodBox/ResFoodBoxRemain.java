/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.foodBox;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.axetta.ecafe.processor.core.sync.AbstractToElement;
import ru.axetta.ecafe.processor.core.sync.handlers.TurnstileSettingsRequest.items.ResTurnstileSettingsRequestItem;

import java.util.List;

public class ResFoodBoxRemain implements AbstractToElement {

    private List<ResFoodBoxRemainItem> items;

    public ResFoodBoxRemain() {
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("FoodBoxPreorder");
        for (ResFoodBoxRemainItem item : this.getItems()) {
            element.appendChild(item.toElement(document,"FBP"));
        }
        return element;
    }

    public List<ResFoodBoxRemainItem> getItems() {
        return items;
    }

    public void setItems(List<ResFoodBoxRemainItem> items) {
        this.items = items;
    }
}
