/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import javax.faces.model.SelectItem;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 11.06.2009
 * Time: 17:01:58
 * To change this template use File | Settings | File Templates.
 */
public class ClientCardOwnMenu {

    public static final int NO_CONDITION = 0;
    public static final int HAS_CARD = 1;
    public static final int HA_NO_CARD = 2;

    private static final String[] ITEM_TEXT = {"Не задано", "Есть карты", "Нет ни одной карты"};

    private SelectItem[] items = readAllItems();

    private static SelectItem[] readAllItems() {
        SelectItem[] items = new SelectItem[ITEM_TEXT.length];
        for (int i = 0; i != items.length; ++i) {
            items[i] = new SelectItem(i, ITEM_TEXT[i]);
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