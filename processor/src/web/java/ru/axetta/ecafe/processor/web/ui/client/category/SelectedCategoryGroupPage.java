/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client.category;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 13:51
 * To change this template use File | Settings | File Templates.
 */
public class SelectedCategoryGroupPage extends BasicWorkspacePage {
    private String name;

    public String getName() {
        return name;
    }

    public void fill(Session session, Long idOfCategory) throws Exception {
        CategoryDiscount categoryDiscount = (CategoryDiscount) session.load(CategoryDiscount.class, idOfCategory);
        this.name = categoryDiscount.getCategoryName();
    }

}
