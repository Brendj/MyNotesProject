/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.category;

import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 07.02.12
 * Time: 12:31
 * To change this template use File | Settings | File Templates.
 */
@Component

public class CategoryOrgListPage extends BasicWorkspacePage {

    private List<CategoryOrg> items = Collections.emptyList();

    @PersistenceContext
    EntityManager entityManager;

    public String getPageFilename() {
        return "option/orgcategories/list";
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    @Override
    public void onShow() throws Exception {
        fill();

    }

    private void fill() throws Exception {
        this.items = DAOUtils.fetchCategoryOrg(entityManager);
    }

    public List<CategoryOrg> getItems() {
        return items;
    }
}
