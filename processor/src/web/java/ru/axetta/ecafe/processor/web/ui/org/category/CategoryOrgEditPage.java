/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.category;

import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

import org.hibernate.HibernateException;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 07.02.12
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
@Component

public class CategoryOrgEditPage extends BasicWorkspacePage implements OrgListSelectPage.CompleteHandlerList {

    private Long selectedIdOfCategoryOrg;
    private CategoryOrg currCategoryOrg;
    private String filter = "Не выбрано";
    private List<Long> idOfOrgList = new ArrayList<Long>();
    private List<Org> orgList;
    
    @PersistenceContext
    EntityManager entityManager;


    public String getPageFilename() {
        return "option/orgcategories/edit";
    }

    public String getFilter() {
        return filter;
    }

    @Override
    public void onShow() throws Exception {
        currCategoryOrg = DAOUtils.fetchCategoryOrgById(entityManager, selectedIdOfCategoryOrg);
        for(Org org:  currCategoryOrg.getOrgs()){
              idOfOrgList.add(org.getIdOfOrg());
        }
        if(!idOfOrgList.isEmpty()){

        }
    }

    public Object save() throws Exception {
        if(orgList == null && idOfOrgList.isEmpty()){
            printMessage("Выберите организацию для категории.");
        } else{
            orgList=DAOUtils.findOrgs(entityManager, idOfOrgList);
            for(Org org:  orgList){
                currCategoryOrg.getOrgs().add(org);
            }
            DAOUtils.saveFromCategoryOrg(entityManager, currCategoryOrg);
            printMessage("Данные обновлены.");
        }
        return null;
    }

    public Long getSelectedIdOfCategoryOrg() {
        return selectedIdOfCategoryOrg;
    }

    public void setSelectedIdOfCategoryOrg(Long selectedIdOfCategoryOrg) {
        this.selectedIdOfCategoryOrg = selectedIdOfCategoryOrg;
    }

    public CategoryOrg getCurrCategoryOrg() {
        return currCategoryOrg;
    }

    public void setCurrCategoryOrg(CategoryOrg currCategoryOrg) {
        this.currCategoryOrg = currCategoryOrg;
    }

    @Override
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
}
