/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.persistence.ClientSms;

import javax.faces.model.SelectItem;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 11.06.2009
 * Time: 17:01:58
 * To change this template use File | Settings | File Templates.
 */
public class SmsDeliveryFilterMenu {

    private SelectItem[] items = readAllItems();

    private static SelectItem[] readAllItems() {
        SelectItem[] items = new SelectItem[ClientSms.DELIVERY_STATUS_DESCRIPTION.length + 1];
        items[0] = new SelectItem(0, "Любой");
        for (int i = 1, j = 0; i < items.length; ++i, ++j) {
            items[i] = new SelectItem(i, ClientSms.DELIVERY_STATUS_DESCRIPTION[j]);
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