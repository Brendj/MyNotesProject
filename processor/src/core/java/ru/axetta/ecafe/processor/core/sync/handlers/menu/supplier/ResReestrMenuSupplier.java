/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menu.supplier;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: o.petrova
 * Date: 10.02.20
 * Time: 13:02
 * To change this template use File | Settings | File Templates.
 */

public class ResReestrMenuSupplier implements AbstractToElement {
    private List<MenuSupplier> items;

    public ResReestrMenuSupplier() {

    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("MenuSupplier");
        for (MenuSupplier item : this.getItems()) {
            element.appendChild(item.toElement(document, "MenuSupplier"
                    + ""));
        }
        return element;
    }

    public List<MenuSupplier> getItems() {
        return items;
    }

    public void setItems(List<MenuSupplier> items) {
        this.items = items;
    }
}
