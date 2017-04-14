/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.menu;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 11.01.12
 * Time: 11:46
 * To change this template use File | Settings | File Templates.
 */
public class MenuViewPage extends BasicWorkspacePage {

    private static Long idOfOrg;

    public static Long getIdOfOrg() {
        return idOfOrg;
    }

    public static Integer getMenuDays() {
        return DAOReadonlyService.getInstance().getMenuCountDays(idOfOrg);
    }

    public static void setIdOfOrg(Long idOfOrg) {
        MenuViewPage.idOfOrg = idOfOrg;
    }

    public String getPageFilename() {
        return "org/menu/view";
    }
}