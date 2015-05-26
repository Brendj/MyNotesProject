/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
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
public class CardStateMenu {

    private List<SelectItem> items = readAllItems();

    private static List<SelectItem> readAllItems() {
        //SelectItem[] items = new SelectItem[Card.STATE_NAMES.length];
        //for (int i = 0; i < items.length; ++i) {
        //    items[i] = new SelectItem(i, Card.STATE_NAMES[i]);
        //}
        List<SelectItem> items = new LinkedList<SelectItem>();

        SelectItem item =  new SelectItem(CardState.BLOCKED.getValue(),CardState.BLOCKED.getDescription());
        items.add(item);
        item = new SelectItem(CardState.ISSUED.getValue(),CardState.ISSUED.getDescription());
        items.add(item);
        item = new SelectItem(CardState.ISSUEDTEMP.getValue(),CardState.ISSUEDTEMP.getDescription());
        item.setDisabled(true);
        items.add(item);
        item = new SelectItem(CardState.BLOCKEDANDRESET.getValue(),CardState.BLOCKEDANDRESET.getDescription());
        item.setDisabled(true);
        items.add(item);
        item = new SelectItem(CardState.FREE.getValue(),CardState.FREE.getDescription());
        item.setDisabled(true);
        items.add(item);
        item = new SelectItem(CardState.UNKNOWN.getValue(),CardState.UNKNOWN.getDescription());
        item.setDisabled(true);
        items.add(item);



        return items;
    }

    public List<SelectItem> getItems() {
        return items;
    }
}