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
        /* tnd debug*/
        Set<CategoryOrg> categoryOrgSet = org.getCategories();
        List results = entityManager.createQuery("from DiscountRule").getResultList();
        for(Object object: results){
            DiscountRule discountRule = (DiscountRule) object;
            Set<CategoryOrg> categoryOrgsDR = discountRule.getCategoryOrgs();
            Set<CategoryOrg> categoryOrgs =new HashSet<CategoryOrg>(categoryOrgsDR);
            /* преобразуем множество categoryOrgs с учетом объединения
           * true если множество не поменялось после объединения
           * */

                /*
            long count=0;
            for (CategoryOrg categoryOrg: discountRule.getCategoryOrgs()){
                for (CategoryOrg categoryOrg1: categoryOrgSet){
                    if(categoryOrg.getIdOfCategoryOrg()==categoryOrg1.getIdOfCategoryOrg())
                        count++;
                }
            }
           // System.out.println("count="+count+" categoryOrgSet.size()="+categoryOrgSet.size());
           /* if(categoryOrgSet.size()>=count){
                discountRuleList.add(discountRule);
            }*/

            if(categoryOrgSet.containsAll(discountRule.getCategoryOrgs())){
                discountRuleList.add(discountRule);
                categoryOrgs.addAll(discountRule.getCategoryOrgs());
                System.out.println(discountRule.getCategoryOrgs().size());
                if(!discountRule.getCategoryOrgs().isEmpty()) {
                    System.out.println(discountRule.getCategoryOrgs().toString());
                }
            }

            if(discountRule.getCategoryOrgs().containsAll(categoryOrgSet)){
                discountRuleList.add(discountRule);
                categoryOrgs.addAll(discountRule.getCategoryOrgs());
                System.out.println(discountRule.getCategoryOrgs().size());
                if(!discountRule.getCategoryOrgs().isEmpty()) {
                    System.out.println(discountRule.getCategoryOrgs().toString());
                }
            }
        }
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
