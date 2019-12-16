/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.preorder;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: o.petrova
 * Date: 16.12.19
 * Time: 12:13
 * To change this template use File | Settings | File Templates.
 */
public class ResReestrTaloonPreorder implements AbstractToElement {
    private List<ResTaloonPreorderItem> items;

    public ResReestrTaloonPreorder() {
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ResReestrTaloonPreorders");
        for (ResTaloonPreorderItem item : this.getItems()) {
            element.appendChild(item.toElement(document, "RTPR"));
        }
        return element;
    }

    public List<ResTaloonPreorderItem> getItems() {
        return items;
    }

    public void setItems(List<ResTaloonPreorderItem> items) {
        this.items = items;
    }
}
