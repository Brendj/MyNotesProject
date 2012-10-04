/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.categoryorg;

import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.ConfirmDeletePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;


@Component
@Scope("session")
public class CategoryOrgListPage extends BasicWorkspacePage implements ConfirmDeletePage.Listener {

    private List<CategoryOrg> items;
    
    @PersistenceContext
    EntityManager entityManager;

    public String getPageFilename() {
        return "option/categoryorg/list";
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    @Override
    public void onShow() throws Exception {
        reload();
    }

    void reload() {
        this.items = DAOUtils.findCategoryOrg(entityManager);
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

    @Override
    public void onConfirmDelete(ConfirmDeletePage confirmDeletePage) {
        DAOService.getInstance().deleteEntity(confirmDeletePage.getEntity());
        reload();
    }

}
