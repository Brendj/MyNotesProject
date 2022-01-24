/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.categoryorg;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
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
import java.util.LinkedList;
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
@Scope("session")
public class CategoryOrgEditPage extends BasicWorkspacePage implements OrgListSelectPage.CompleteHandlerList {

    private CategoryOrg entity;
    private String filter = "Не выбрано";
    private List<Long> idOfOrgList = new ArrayList<Long>(0);

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;


    public String getPageFilename() {
        return "option/categoryorg/edit";
    }

    public String getEntityName() {
        return entity ==null?null: entity.getCategoryName();
    }

    public String getFilter() {
        return filter;
    }

    @Override
    public void onShow() throws Exception {
        //reload();
        RuntimeContext.getAppContext().getBean(getClass()).reload();
    }

    @Transactional
    public void reload() {
        entity = entityManager.merge(entity);
        idOfOrgList = new LinkedList<Long>();
        if(!entity.getOrgs().isEmpty()){
            StringBuilder sb=new StringBuilder();
            for(Org org:  entity.getOrgs()){
                idOfOrgList.add(org.getIdOfOrg());
                sb.append(org.getShortName());
                sb.append("; ");
            }
            filter=sb.substring(0,sb.length()-2);
        }
    }

    @Transactional
    public Object save() throws Exception {
        entity.setCategoryName(entity.getCategoryName().trim());
        if (entity.getCategoryName().equals(""))
        {
            printError("Неверное название категории");
            return null;
        }
        List<CategoryOrg> categoryOrgs = DAOReadonlyService.getInstance().getCategoryOrgByCategoryName(entity.getCategoryName());
        for (CategoryOrg categoryOrg: categoryOrgs)
        {
            if (categoryOrg.getIdOfCategoryOrg() != entity.getIdOfCategoryOrg()) {
                printError("Категория с данным названием уже зарегистрирована");
                return null;
            }
        }
        entity =entityManager.merge(entity);
        entity.getOrgs().clear();
        if(!idOfOrgList.isEmpty()){
            entity.getOrgs().addAll(DAOUtils.findOrgs(entityManager, idOfOrgList));
        }
        printMessage("Данные обновлены.");
        return null;
    }

    public String getIdOfOrgList() {
        return idOfOrgList.toString().replaceAll("[^0-9,]","");
    }

    public CategoryOrg getEntity() {
        return entity;
    }

    public void setEntity(CategoryOrg entity) {
        this.entity = entity;
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
