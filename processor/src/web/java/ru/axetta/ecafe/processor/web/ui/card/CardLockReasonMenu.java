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

    public static String getDescriptionByValue(Integer value) {
        String descriptionString = null;
        if (value == CardLockReason.EMPTY.getValue()) {
            descriptionString = CardLockReason.EMPTY.getDescription();
        }else if (value == CardLockReason.NEW.getValue()) {
            descriptionString = CardLockReason.NEW.getDescription();
        } else if (value == CardLockReason.REISSUE_BROKEN.getValue()) {
            descriptionString = CardLockReason.REISSUE_BROKEN.getDescription();
        } else if (value == CardLockReason.REISSUE_LOSS.getValue()) {
            descriptionString = CardLockReason.REISSUE_LOSS.getDescription();
        } else if (value == CardLockReason.DEMAGNETIZED.getValue()) {
            descriptionString = CardLockReason.DEMAGNETIZED.getDescription();
        } else if (value == CardLockReason.OTHER.getValue()) {
            descriptionString = CardLockReason.OTHER.getDescription();
        }
        return descriptionString;
    }

    public static Integer getValueByDescription(String description) {
        Integer valueInt = null;
        if (description == null || description.equals(CardLockReason.EMPTY.getDescription())) {
            valueInt = CardLockReason.EMPTY.getValue();
        } else if (description.equals(CardLockReason.NEW.getDescription())) {
            valueInt = CardLockReason.NEW.getValue();
        } else if (description.equals(CardLockReason.REISSUE_BROKEN.getDescription())) {
            valueInt = CardLockReason.REISSUE_BROKEN.getValue();
        } else if (description.equals(CardLockReason.REISSUE_LOSS.getDescription())) {
            valueInt = CardLockReason.REISSUE_LOSS.getValue();
        } else if (description.equals(CardLockReason.DEMAGNETIZED.getDescription())) {
            valueInt = CardLockReason.DEMAGNETIZED.getValue();
        } else if (description.equals(CardLockReason.OTHER.getDescription())) {
            valueInt = CardLockReason.OTHER.getValue();
        }
        return valueInt;
    }
}
