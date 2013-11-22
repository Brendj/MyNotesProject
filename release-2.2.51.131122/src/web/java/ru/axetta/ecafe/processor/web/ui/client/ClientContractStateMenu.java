/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.persistence.Client;

import javax.faces.model.SelectItem;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 26.06.2009
 * Time: 11:12:14
 * To change this template use File | Settings | File Templates.
 */
public class ClientContractStateMenu {

    private SelectItem[] items = readAllItems();

    private static SelectItem[] readAllItems() {
        SelectItem[] items = new SelectItem[Client.CONTRACT_STATE_NAMES.length];
        for (int i = 0; i < items.length; ++i) {
            items[i] = new SelectItem(i, Client.CONTRACT_STATE_NAMES[i]);
        }
        return items;
    }

    public SelectItem[] getItems() {
        return items;
    }

    public void setItems(SelectItem[] items) {
        this.items = items;
    }
}