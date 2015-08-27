/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.finansional.settings;

import ru.axetta.ecafe.processor.core.persistence.LatePaymentDaysCountType;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 27.08.15
 * Time: 16:39
 */

public class LatePaymentDaysCountTypeMenu {

    private List<SelectItem> items = readAllItems();

    private static List<SelectItem> readAllItems() {
        LatePaymentDaysCountType[] latePaymentDaysCountTypes = LatePaymentDaysCountType.values();
        List<SelectItem> items = new ArrayList<SelectItem>(latePaymentDaysCountTypes.length);
        for (LatePaymentDaysCountType latePaymentDaysCountType: latePaymentDaysCountTypes){
            items.add(new SelectItem(latePaymentDaysCountType, latePaymentDaysCountType.toString()));
        }
        return items;
    }

    public List<SelectItem> getItems() {
        return items;
    }

    public void setItems(List<SelectItem> items) {
        this.items = items;
    }
}
