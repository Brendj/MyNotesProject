/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.category;

import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 06.02.12
 * Time: 13:11
 * To change this template use File | Settings | File Templates.
 */
@Component

public class CategoryOrgCreatePage extends BasicWorkspacePage implements OrgListSelectPage.CompleteHandlerList {
    
    private CategoryOrg currCategoryOrg = new CategoryOrg();

    @PersistenceContext
    EntityManager entityManager;

    public CategoryOrg getCurrCategoryOrg() {
        return currCategoryOrg;
    }

    public void setCurrCategoryOrg(CategoryOrg currCategoryOrg) {
        this.currCategoryOrg = currCategoryOrg;
    }

    private String filter = "Не выбрано";
    private List<Long> idOfOrgList = Collections.emptyList();

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

    @Override
    public void onShow() throws Exception {}

    @Transactional
    public Object save(){
        if (currCategoryOrg.getCategoryName().equals("")){
            printMessage("Введите название категории.");
            return null;
        }
        if (idOfOrgList.isEmpty()){
            printMessage("Выберите организацию для категории.");
            return null;
        }
        List<Org> orgList=DAOUtils.findOrgs(entityManager, idOfOrgList);
        for(Org org:  orgList){
            currCategoryOrg.getOrgs().add(org);
        }
        entityManager.persist(currCategoryOrg);
        printMessage("Данные успешно сохранены.");
        show();
        return null;
    }

}
