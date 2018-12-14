/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;

import javax.faces.model.SelectItem;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 25.12.12
 * Time: 16:08
 * To change this template use File | Settings | File Templates.
 */
public class UserRoleEnumTypeMenu {

    private SelectItem[] items = readAllItems();
    public static final Long OFFSET = 1000000L;

    private static SelectItem[] readAllItems() {
        User.DefaultRole[] defaultRoles = User.DefaultRole.values();
        List<User> roles = DAOReadonlyService.getInstance().getUserRoles();
        SelectItem[] items = new SelectItem[defaultRoles.length + roles.size()];
        for (int i = 0; i < defaultRoles.length; ++i) {
            items[i] = new SelectItem(i, defaultRoles[i].toString());
        }
        int i = defaultRoles.length;
        for (User role : roles) {
            items[i] = new SelectItem(OFFSET + role.getIdOfUser(), role.getUserName());
            i++;
        }
        return items;
    }

    public SelectItem[] getItems() {
        return readAllItems(); //items;
    }

    public void setItems(SelectItem[] items) {
        this.items = items;
    }

}
