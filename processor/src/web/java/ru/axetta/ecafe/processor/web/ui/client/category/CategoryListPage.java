/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client.category;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Order;

import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 9:56
 * To change this template use File | Settings | File Templates.
 */
public class CategoryListPage extends BasicWorkspacePage {

    private List<CategoryDiscount> items = Collections.emptyList();

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    public List<CategoryDiscount> getItems() {
        return items;
    }

    public String getPageFilename() {
        return "client/category/list";
    }

    public void fill(Session session) throws Exception {
        Criteria categoryCriteria = session.createCriteria(CategoryDiscount.class);
        categoryCriteria.addOrder(Order.asc("idOfCategoryDiscount"));
        items = categoryCriteria.list();
    }
}
