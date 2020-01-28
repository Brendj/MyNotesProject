/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.planordersrestrictions;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created by i.semenov on 27.01.2020.
 */
public class PlanOrdersRestrictions implements AbstractToElement {
    private List<PlanOrdersRestrictionItem> items;

    public PlanOrdersRestrictions() {

    }

    public List<PlanOrdersRestrictionItem> getItems() {
        return items;
    }

    public void setItems(List<PlanOrdersRestrictionItem> items) {
        this.items = items;
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("PlanOrdersRestrictions");
        for (PlanOrdersRestrictionItem item : this.getItems()) {
            element.appendChild(item.toElement(document));
        }
        return element;
    }
}
