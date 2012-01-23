/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card;

import ru.axetta.ecafe.processor.core.persistence.Card;

import javax.faces.model.SelectItem;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 11.06.2009
 * Time: 17:01:58
 * To change this template use File | Settings | File Templates.
 */
public class CardStateFilterMenu {

    public static final int NO_CONDITION = -1;
    private SelectItem[] items = readAllItems();

    private static SelectItem[] readAllItems() {
        SelectItem[] items = new SelectItem[Card.STATE_NAMES.length + 1];
        items[0] = new SelectItem(NO_CONDITION, "Не имеет значения");
        for (int i = 1, j = 0; i != items.length; ++i, ++j) {
            items[i] = new SelectItem(j, Card.STATE_NAMES[j]);
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