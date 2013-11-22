/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.categorydiscount;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscountEnumType;

import javax.faces.model.SelectItem;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 25.12.12
 * Time: 16:08
 * To change this template use File | Settings | File Templates.
 */
public class CategoryDiscountEnumTypeMenu {

    private SelectItem[] items = readAllItems();

    private static SelectItem[] readAllItems() {
        CategoryDiscountEnumType[] categoryDiscountEnumTypes = CategoryDiscountEnumType.values();
        SelectItem[] items = new SelectItem[categoryDiscountEnumTypes.length];
        for (int i = 0; i < items.length; ++i) {
            items[i] = new SelectItem(i, categoryDiscountEnumTypes[i].toString());
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
