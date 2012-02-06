/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.category;

import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 06.02.12
 * Time: 13:11
 * To change this template use File | Settings | File Templates.
 */

public class CategoryOrgCreatePage extends BasicWorkspacePage {
    private CategoryOrg categoryOrg = new CategoryOrg();

    public CategoryOrg getCategoryOrg() {
        return categoryOrg;
    }

    public void setCategoryOrg(CategoryOrg categoryOrg) {
        this.categoryOrg = categoryOrg;
    }

    private String filter = "Не выбрано";
    private List<Long> idOfOrgList = new ArrayList<Long>();

    public String getPageFilename() {
        return "option/orgcategories/create";
    }

    public String getFilter() {
        return filter;
    }

    public void completeOrgListSelection(Map<Long, String> orgMap) throws HibernateException {
        if (orgMap != null) {
            idOfOrgList = new ArrayList<Long>();
            if (orgMap.isEmpty())
                filter = "Не выбрано";
            else {
                filter = "";
                for(Long idOfOrg : orgMap.keySet()) {
                    idOfOrgList.add(idOfOrg);
                    filter = filter.concat(orgMap.get(idOfOrg) + "; ");
                }
                filter = filter.substring(0, filter.length() - 1);
            }
        }
    }

    public void fill(Session session) throws Exception {}

    public void createCategory(Session session) throws Exception {
           CategoryOrg categoryDiscount = new CategoryOrg();
           session.save(categoryDiscount);
    }

      /*
    private static Logger log= Logger.getLogger("CategoryOrgCreatePage");

    @Override
    public void onShow() throws Exception {

    }

    public Object save() {

    }



    public Object cancel() throws Exception {

    }

    public CategoryOrg getCategoryOrg() {
        return categoryOrg;
    }

    public void setCategoryOrg(CategoryOrg categoryOrg) {
        this.categoryOrg = categoryOrg;
    }

       */

}
