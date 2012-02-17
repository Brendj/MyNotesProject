/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.category;

import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

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
        this.items = DAOUtils.findCategoryOrg(entityManager);
        /* для теста */
        Org org = entityManager.find(Org.class, 2L);
        Set<CategoryOrg> categoryOrgSet = org.getCategories();
        List results = entityManager.createQuery("from DiscountRule").getResultList();
        for(Object object: results){
            DiscountRule discountRule = (DiscountRule) object;
            System.out.println(discountRule.getCategoryOrgs().toString());
            System.out.println(org.getCategories().toString());
            System.out.println(org.getCategories().containsAll(discountRule.getCategoryOrgs()));
        }
        /* end debug*/
    }

    public List<CategoryOrg> getItems() {
        return items;
    }

    private List<DiscountRule> discountRuleList = new LinkedList<DiscountRule>();

    public List<DiscountRule> getDiscountRuleList() {
        return discountRuleList;
    }

    public void setDiscountRuleList(List<DiscountRule> discountRuleList) {
        this.discountRuleList = discountRuleList;
    }

    private List<CategoryOrg> categoryOrgList = new LinkedList<CategoryOrg>();

    public List<CategoryOrg> getCategoryOrgList() {
        return categoryOrgList;
    }

    public void setCategoryOrgList(List<CategoryOrg> categoryOrgList) {
        this.categoryOrgList = categoryOrgList;
    }
}
