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
 * Time: 13:48
 */
public class ResSpecialDates implements AbstractToElement{

    private List<ResSpecialDatesItem> items;

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ResSpecialDates");
        for (ResSpecialDatesItem item : this.getItems()) {
            element.appendChild(item.toElement(document, "RSD"));
        }
        return element;
    }

    public ResSpecialDates() {
    }

    public List<ResSpecialDatesItem> getItems() {
        return items;
    }

    public void setItems(List<ResSpecialDatesItem> items) {
        this.items = items;
    }

}
