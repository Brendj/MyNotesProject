/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscountEnumType;
import ru.axetta.ecafe.processor.core.persistence.User;

import javax.faces.model.SelectItem;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 25.12.12
 * Time: 16:08
 * To change this template use File | Settings | File Templates.
 */
public class UserRoleEnumTypeMenu {

    private SelectItem[] items = readAllItems();

    private static SelectItem[] readAllItems() {
        User.DefaultRole[] defaultRoles = User.DefaultRole.values();
        SelectItem[] items = new SelectItem[defaultRoles.length];
        for (int i = 0; i < items.length; ++i) {
            items[i] = new SelectItem(i, defaultRoles[i].toString());
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
