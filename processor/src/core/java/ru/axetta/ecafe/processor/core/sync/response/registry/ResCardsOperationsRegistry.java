/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response.registry;

import ru.axetta.ecafe.processor.core.sync.SyncRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.registry.cards.CardsOperationsRegistryItem;

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
public class ResCardsOperationsRegistry {
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

    public ResCardsOperationsRegistry handler(SyncRequest request, long idOfOrg) {
        if(request.getCardsOperationsRegistry() == null){
            return null;
        }


        ResCardsOperationsRegistry resCardsOperationsRegistry = new ResCardsOperationsRegistry();
        for (CardsOperationsRegistryItem o : request.getCardsOperationsRegistry().getItems()) {
            itemList.add(new ResCardsOperationsRegistryItem(o.getIdOfOperation(), 400, "Внутреняя ошибка"));
        }

        return resCardsOperationsRegistry;
    }
}
