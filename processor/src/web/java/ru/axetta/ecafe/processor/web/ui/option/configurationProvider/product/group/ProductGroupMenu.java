/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.group;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.ProductGroup;

import javax.faces.model.SelectItem;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 11.06.2009
 * Time: 17:01:58
 * To change this template use File | Settings | File Templates.
 */
public class ProductGroupMenu {

    private SelectItem[] items;

    public void readAllItems(List<ProductGroup> list) {
        items = new SelectItem[list.size()];
        for (int i = 0; i < items.length; ++i) {
            items[i] = new SelectItem();
            items[i].setValue(list.get(i).getNameOfGroup());
            items[i].setLabel(String.valueOf(list.get(i).getGlobalId()));
        }
    }

    public SelectItem[] getItems() {
        return items;
    }
}