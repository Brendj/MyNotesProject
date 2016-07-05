/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response.registry;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * 3.9 3.9.	Реестр обработанных операций по счетам
 * User: shamil
 * Date: 30.04.15
 * Time: 11:02
 */
public class ResCardsOperationsRegistry implements AbstractToElement{
    public static final String SYNC_NAME = "ResCardsOperationsRegistry";

    private List<ResCardsOperationsRegistryItem> itemList = new LinkedList<ResCardsOperationsRegistryItem>();

    public List<ResCardsOperationsRegistryItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<ResCardsOperationsRegistryItem> itemList) {
        this.itemList = itemList;
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement(SYNC_NAME);
        for (ResCardsOperationsRegistryItem item : this.itemList) {
            element.appendChild(item.toElement(document));
        }
        return element;
    }


}
