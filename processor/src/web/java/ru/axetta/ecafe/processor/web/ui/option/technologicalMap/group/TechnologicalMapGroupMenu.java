/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.technologicalMap.group;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ProductGroup;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMapGroup;

import javax.faces.model.SelectItem;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 11.06.2009
 * Time: 17:01:58
 * To change this template use File | Settings | File Templates.
 */
public class TechnologicalMapGroupMenu {

    private SelectItem[] items;

    public void readAllItems(List<TechnologicalMapGroup> list) {
        items = new SelectItem[list.size()];
        for (int i = 0; i < items.length; ++i) {
            items[i] = new SelectItem(list.get(i).getGlobalId(),list.get(i).getNameOfGroup());
        }
    }

    public SelectItem[] getItems() {
        return items;
    }
}