/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
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
public class CardLifeStateMenu {

    private SelectItem[] items = readAllItems();

    private static SelectItem[] readAllItems() {
        SelectItem[] items = new SelectItem[Card.LIFE_STATE_NAMES.length];
        for (int i = 0; i < items.length; ++i) {
            items[i] = new SelectItem(i, Card.LIFE_STATE_NAMES[i]);
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