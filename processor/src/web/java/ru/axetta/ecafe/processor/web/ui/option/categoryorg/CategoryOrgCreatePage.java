/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.categoryorg;

import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 06.02.12
 * Time: 13:11
 * To change this template use File | Settings | File Templates.
 */

@Component
@Scope("session")
public class CategoryOrgCreatePage extends BasicWorkspacePage implements OrgListSelectPage.CompleteHandlerList {

    private String filter;
    private List<Long> idOfOrgList = new ArrayList<Long>();

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    private String categoryName;

    public String getPageFilename() {
        return "option/categoryorg/create";
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
    public void onShow() throws Exception {
        categoryName = "";
        this.filter = "Не выбрано";
    }

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    @Transactional
    public void createCategoryOrg(){
        categoryName = categoryName.trim();
        if (categoryName.equals(""))
        {
            printError("Неверное название категории");
            return;
        }
        if (!DAOService.getInstance().getCategoryOrgByCategoryName(categoryName).isEmpty() )
        {
            printError("Категория с данным названием уже зарегистрирована");
            return;
        }
        CategoryOrg currCategoryOrg = new CategoryOrg();
        currCategoryOrg.setCategoryName(categoryName);
        currCategoryOrg.getOrgs().clear();
        if(!this.idOfOrgList.isEmpty()){
            currCategoryOrg.getOrgs().addAll(DAOUtils.getOrgsByIdList(entityManager, idOfOrgList));
        }
        entityManager.persist(currCategoryOrg);
        printMessage("Категория зарегистрирована успешно");

    }

    public String getGetStringIdOfOrgList() {
        return idOfOrgList.toString().replaceAll("[^0-9,]","");
    }
}
