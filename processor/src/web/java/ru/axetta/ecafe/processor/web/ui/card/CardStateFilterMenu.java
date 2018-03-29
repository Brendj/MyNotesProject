/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card;

import ru.axetta.ecafe.processor.core.persistence.CardState;

import javax.faces.model.SelectItem;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 11.06.2009
 * Time: 17:01:58
 * To change this template use File | Settings | File Templates.
 */
public class CardStateFilterMenu {

    public static final int NO_CONDITION = -1;
    private List<SelectItem> items = readAllItems();
    private List<SelectItem> itemsCarOperator = readAllItemsCardOperator();

    private static List<SelectItem> readAllItems() {
        List<SelectItem> items = new LinkedList<SelectItem>();
        items.add(new SelectItem(NO_CONDITION, "Не имеет значения"));
        for (CardState cardState : CardState.values()) {
            items.add(new SelectItem(cardState.getValue(), cardState.getDescription()));
        }


        return items;
    }

    private static List<SelectItem> readAllItemsCardOperator() {
        List<SelectItem> items = new LinkedList<SelectItem>();
        //items.add(new SelectItem(NO_CONDITION, "Не имеет значения"));
        for (CardState cardState : CardState.values()) {
            if (cardState.getDescription().equals("Выдана (активна)")) {
                items.add(new SelectItem(cardState.getValue(), cardState.getDescription()));
            } else if (cardState.getDescription().equals("Заблокирована")) {
                items.add(new SelectItem(cardState.getValue(), cardState.getDescription()));
            }
        }
        return items;
    }

    public List<SelectItem> getItems() {
        return items;
    }

    public List<SelectItem> getItemsCarOperator() {
        return itemsCarOperator;
    }
}