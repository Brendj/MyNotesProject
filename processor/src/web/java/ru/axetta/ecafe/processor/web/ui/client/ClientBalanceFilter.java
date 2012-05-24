/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import javax.faces.model.SelectItem;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 24.05.12
 * Time: 11:32
 * To change this template use File | Settings | File Templates.
 */
public class ClientBalanceFilter {

    public static final int NO_CONDITION = 0;
    public static final int LT_ZERO = 1;
    public static final int EQ_ZERO = 2;
    public static final int GT_ZERO = 3;

    private static final String[] ITEM_TEXT = {"Не задано", "Меньше 0", "Равен 0", "Больше 0"};

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
