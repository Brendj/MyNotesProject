/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card;

import javax.faces.model.SelectItem;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by anvarov on 24.07.2017.
 */
public class CardLockReasonMenu {

    private List<SelectItem> items = readAllItems();

    public static List<SelectItem> readAllItems() {
        List<SelectItem> items = new LinkedList<SelectItem>();

        SelectItem item = new SelectItem(CardLockReason.EMPTY.getValue(), CardLockReason.EMPTY.getDescription());
        items.add(item);
        item = new SelectItem(CardLockReason.NEW.getValue(), CardLockReason.NEW.getDescription());
        items.add(item);
        item = new SelectItem(CardLockReason.REISSUE_BROKEN.getValue(), CardLockReason.REISSUE_BROKEN.getDescription());
        items.add(item);
        item = new SelectItem(CardLockReason.REISSUE_LOSS.getValue(), CardLockReason.REISSUE_LOSS.getDescription());
        items.add(item);
        item = new SelectItem(CardLockReason.DEMAGNETIZED.getValue(), CardLockReason.DEMAGNETIZED.getDescription());
        items.add(item);
        item = new SelectItem(CardLockReason.OTHER.getValue(), CardLockReason.OTHER.getDescription());
        items.add(item);

        return items;
    }

    public List<SelectItem> getItems() {
        return items;
    }

    public void setItems(List<SelectItem> items) {
        this.items = items;
    }


}
