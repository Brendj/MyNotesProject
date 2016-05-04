/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.persistence.Client;

import javax.faces.model.SelectItem;

/**
 * Created with IntelliJ IDEA.
 * User: Anvarov
 */
public class ClientGenderMenu {

    private SelectItem[] items = readAllItems();

    private static SelectItem[] readAllItems() {
        SelectItem[] items = new SelectItem[Client.CLIENT_GENDER_COUNT];
        for (int i = 0; i < items.length; ++i) {
            items[i] = new SelectItem(i, Client.CLIENT_GENDER_NAMES[i]);
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
