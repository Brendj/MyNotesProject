/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card;

import generated.rnip.roskazna.xsd.common.CardType;

import ru.axetta.ecafe.processor.core.persistence.Card;

import org.apache.commons.lang.ArrayUtils;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 11.06.2009
 * Time: 17:01:58
 * To change this template use File | Settings | File Templates.
 */
public class CardTypeMenu {

    private SelectItem[] items = readAllItems();

    private SelectItem[] itemsCardOperator = readCardOperatorItems();

    private static SelectItem[] readAllItems() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        for (int i = 0; i < Card.TYPE_NAMES.length; ++i) {
            if (ArrayUtils.contains(Card.DEPRECATED_TYPES, i)) continue;
            items.add(new SelectItem(i, Card.TYPE_NAMES[i]));
        }
        SelectItem[] itemsArray = new SelectItem[items.size()];
        itemsArray = items.toArray(itemsArray);
        return itemsArray;
    }

    private static SelectItem[] readCardOperatorItems() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        for(int i = 0; i < Card.TYPE_NAMES.length; i++) {
            if(Card.TYPE_NAMES[i].equals("Mifare")){
                items.add(new SelectItem(i, Card.TYPE_NAMES[i]));
            } else if(Card.TYPE_NAMES[i].equals("Браслет (Mifare)")) {
                items.add(new SelectItem(i, Card.TYPE_NAMES[i]));
            }
        }
        SelectItem[] itemsArray = new SelectItem[items.size()];
        itemsArray = items.toArray(itemsArray);
        return itemsArray;
    }

    public SelectItem[] getItems() {
        return items;
    }

    public void setItems(SelectItem[] items) {
        this.items = items;
    }

    public SelectItem[] getItemsCardOperator() {
        return itemsCardOperator;
    }

    public void setItemsCardOperator(SelectItem[] itemsCardOperator) {
        this.itemsCardOperator = itemsCardOperator;
    }
}