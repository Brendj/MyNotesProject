/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.special.dates;

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
public class SpecialDatesData implements AbstractToElement {
    private List<ResSpecialDatesItem> items;

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("SpecialDates");
        for (ResSpecialDatesItem item : this.getItems()) {
            element.appendChild(item.toElement(document, "SD"));
        }
        return element;
    }

    public SpecialDatesData() {
    }

    public List<ResSpecialDatesItem> getItems() {
        return items;
    }

    public void setItems(List<ResSpecialDatesItem> items) {
        this.items = items;
    }
}
