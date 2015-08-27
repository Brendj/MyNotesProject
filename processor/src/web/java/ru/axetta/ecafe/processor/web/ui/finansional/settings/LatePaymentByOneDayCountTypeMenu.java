/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.finansional.settings;

import ru.axetta.ecafe.processor.core.persistence.LatePaymentByOneDayCountType;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 27.08.15
 * Time: 16:01
 */

public class LatePaymentByOneDayCountTypeMenu {

    private List<SelectItem> items = readAllItems();

    private static List<SelectItem> readAllItems() {
        LatePaymentByOneDayCountType[] latePaymentByOneDayCountTypes = LatePaymentByOneDayCountType.values();
        List<SelectItem> items = new ArrayList<SelectItem>(latePaymentByOneDayCountTypes.length);
        for (LatePaymentByOneDayCountType latePaymentByOneDayCountType : latePaymentByOneDayCountTypes) {
            items.add(new SelectItem(latePaymentByOneDayCountType, latePaymentByOneDayCountType.toString()));
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
