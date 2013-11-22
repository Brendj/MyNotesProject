/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui;

import javax.faces.model.SelectItem;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 11.06.2009
 * Time: 17:01:58
 * To change this template use File | Settings | File Templates.
 */
public class CompareFilterMenu {

    private static final String[] TEXT = {"Не имеет значения", ">=", "<=", ">", "<", "==", "!="};
    public static final int NO_CONDITION = 0;
    public static final int GE = 1;
    public static final int LE = 2;
    public static final int GT = 3;
    public static final int LT = 4;
    public static final int EQ = 5;
    public static final int NE = 6;
    private SelectItem[] items = readAllItems();

    private static SelectItem[] readAllItems() {
        SelectItem[] items = new SelectItem[TEXT.length];
        for (int i = 0; i != items.length; ++i) {
            items[i] = new SelectItem(i, TEXT[i]);
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