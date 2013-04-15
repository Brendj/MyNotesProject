/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.settings;

import javax.faces.model.SelectItem;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 08.04.13
 * Time: 13:46
 * To change this template use File | Settings | File Templates.
 */
public class SelectItemBuilder {

    private SelectItem[] items;

    public SelectItemBuilder(Integer[] values) {
        SelectItem[] items = new SelectItem[values.length];
        for (int i = 0; i < items.length; ++i) {
            items[i] = new SelectItem(values[i], String.valueOf(values[i]));
        }
    }

    public SelectItem[] getItems() {
        return items;
    }

    public void setItems(SelectItem[] items) {
        this.items = items;
    }
}
