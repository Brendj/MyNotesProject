/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.category;

import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 06.02.12
 * Time: 13:11
 * To change this template use File | Settings | File Templates.
 */
@Component

public class CategoryOrgCreate extends BasicWorkspacePage {

    private CategoryOrg categoryOrg = new CategoryOrg();

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void onShow() throws Exception {

    }

    @Transactional
    public Object save() {
        DAOUtils.saveFromCategoryOrg(entityManager, categoryOrg);
        printMessage("Настройки сохранены.");
        return null;
    }

    public String getPageFilename() {
        return "option/orgcategories/create";
    }

    public Object cancel() throws Exception {
        onShow();
        return null;
    }

    public CategoryOrg getCategoryOrg() {
        return categoryOrg;
    }

    public void setCategoryOrg(CategoryOrg categoryOrg) {
        this.categoryOrg = categoryOrg;
    }
}
