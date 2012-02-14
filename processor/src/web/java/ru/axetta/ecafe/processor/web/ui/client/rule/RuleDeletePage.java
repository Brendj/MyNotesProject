/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client.rule;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.web.ui.BasicPage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 11:35
 * To change this template use File | Settings | File Templates.
 */
public class RuleDeletePage extends BasicPage {
    public void removeRule(Session session, Long id) throws Exception {
        DiscountRule discountRule = (DiscountRule) session.get(DiscountRule.class, id);
        Set<CategoryDiscount> categoryDiscountSet= discountRule.getCategoriesDiscounts();
        List<Long> listIdOfCategoryDiscount = new LinkedList<Long>();
        for(CategoryDiscount categoryDiscount: categoryDiscountSet){
            listIdOfCategoryDiscount.add(categoryDiscount.getIdOfCategoryDiscount());
        }
        Criteria criteriaCategoryDiscount=session.createCriteria(CategoryDiscount.class);
        List categoryDiscountList = criteriaCategoryDiscount.add(Restrictions.in("idOfCategoryDiscount",listIdOfCategoryDiscount)).list();
        for(Object object: categoryDiscountList){
            discountRule.getCategoriesDiscounts().remove((CategoryDiscount) object);
        }
        //if(!discountRule.getCategoriesDiscounts().isEmpty()) discountRule.getCategoriesDiscounts().removeAll(categoryDiscountSet);
        Set<CategoryOrg> categoryOrgSet = discountRule.getCategoryOrgs();
        List<Long> listIdOfCategoryOrg = new LinkedList<Long>();
        for(CategoryOrg categoryOrg: categoryOrgSet){
            listIdOfCategoryOrg.add(categoryOrg.getIdOfCategoryOrg());
        }
        Criteria criteriaCategoryOrg=session.createCriteria(CategoryOrg.class);
        List categoryOrgList = criteriaCategoryDiscount.add(Restrictions.in("idOfCategoryOrg",listIdOfCategoryOrg)).list();
        for(Object object: categoryOrgList){
            discountRule.getCategoryOrgs().remove((CategoryOrg) object);
        }
        //if(!discountRule.getCategoryOrgs().isEmpty()) discountRule.getCategoryOrgs().removeAll(categoryOrgSet);
        session.delete(discountRule);
    }
}
