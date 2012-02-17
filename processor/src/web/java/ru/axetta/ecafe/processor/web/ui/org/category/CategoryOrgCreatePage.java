/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.category;

import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 06.02.12
 * Time: 13:11
 * To change this template use File | Settings | File Templates.
 */

public class CategoryOrgCreatePage extends BasicWorkspacePage implements OrgListSelectPage.CompleteHandlerList {

    private CategoryOrg currCategoryOrg = new CategoryOrg();

    public CategoryOrg getCurrCategoryOrg() {
        return currCategoryOrg;
    }

    public void setCurrCategoryOrg(CategoryOrg currCategoryOrg) {
        this.currCategoryOrg = currCategoryOrg;
    }

    private String filter = "Не выбрано";
    private List<Long> idOfOrgList = new LinkedList<Long>();

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

    public void fill(Session session){
        currCategoryOrg.setCategoryName("");
        this.filter = "Не выбрано";
    }

    public void createCategory(Session session){
        if(!currCategoryOrg.getCategoryName().isEmpty() && !this.idOfOrgList.isEmpty()){
            CategoryOrg categoryOrg = new CategoryOrg();
            categoryOrg.setCategoryName(currCategoryOrg.getCategoryName());
            Criteria categoryCriteria = session.createCriteria(Org.class);
            categoryCriteria.add(Restrictions.in("idOfOrg",this.idOfOrgList));
            for (Object object: categoryCriteria.list()){
                categoryOrg.getOrgs().add((Org) object);
            }
            session.save(categoryOrg);
            printMessage("Категория зарегистрирована успешно");
        }  else {
            logAndPrintMessage("Не ввели наименоание или организации которые пренадлежат данной категороии",
                    new Exception("Category name or org list is empty"));
        }

    }

}
