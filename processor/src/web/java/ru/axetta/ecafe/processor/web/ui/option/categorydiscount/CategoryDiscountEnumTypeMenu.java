/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.categorydiscount;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscountEnumType;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

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
        List<SelectItem> list = new ArrayList<SelectItem>();
        for (int i = 0; i < categoryDiscountEnumTypes.length; ++i) {
            if (i != CategoryDiscountEnumType.NOT_SPECIFIED.getValue())
                list.add(new SelectItem(categoryDiscountEnumTypes[i].getValue(), categoryDiscountEnumTypes[i].toString()));
        }
        return list.toArray(new SelectItem[list.size()]);
    }

    public SelectItem[] getItems() {
        return items;
    }

    public void setItems(SelectItem[] items) {
        this.items = items;
    }

}
