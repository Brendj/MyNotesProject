/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.category;

import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 06.02.12
 * Time: 12:07
 * To change this template use File | Settings | File Templates.
 */
@Component

public class CategoryViewPage extends BasicWorkspacePage {
    
    private List<CategoryOrg> categoryOrgList;

    @PersistenceContext
    EntityManager entityManager;

    public List<CategoryOrg> getCategories(){
        return categoryOrgList;
    }

    public String getPageFilename() {
        return "option/orgcategories/view";
    }

    @Override
    public void onShow() throws Exception {
        categoryOrgList = DAOUtils.fetchCategoryOrg(entityManager);
    }

    public Object save() {

        return null;
    }

}
