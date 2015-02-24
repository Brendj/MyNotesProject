/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.registry.operations.account;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: Shamil
 * Date: 19.02.15
 */
public class ResAccountOperationsRegistry {
    public static final String SYNC_NAME = "ResAccountOperationsRegistry";


    private final List<ResAccountOperationItem> items;

    public ResAccountOperationsRegistry() {
        items = new ArrayList<ResAccountOperationItem>();
    }

    public void addItem(ResAccountOperationItem item) throws Exception {
        this.items.add(item);
    }

    public Iterator<ResAccountOperationItem> getItems() {
        return items.iterator();
    }

    public List<ResAccountOperationItem> getItemsList() {
        return items;
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement(SYNC_NAME);
        for (ResAccountOperationItem item : this.items) {
            element.appendChild(item.toElement(document));
        }
        return element;
    }

    @Override
    public String toString() {
        return "ResAccountOperationsRegistry{" + "items=" + items + '}';
    }
}
