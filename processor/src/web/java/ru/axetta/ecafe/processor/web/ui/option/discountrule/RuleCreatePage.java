/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.discountrule;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategoryListSelectPage;
import ru.axetta.ecafe.processor.web.ui.option.categoryorg.CategoryOrgListSelectPage;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 10:07
 * To change this template use File | Settings | File Templates.
 */
@Component
public class RuleCreatePage extends BasicWorkspacePage
        implements CategoryListSelectPage.CompleteHandlerList, CategoryOrgListSelectPage.CompleteHandlerList {

    private List<Long> idOfCategoryList = new ArrayList<Long>();
    private List<Long> idOfCategoryOrgList = new ArrayList<Long>();
    private String description;
    private boolean complex0;
    private boolean complex1;
    private boolean complex2;
    private boolean complex3;
    private boolean complex4;
    private boolean complex5;
    private boolean complex6;
    private boolean complex7;
    private boolean complex8;
    private boolean complex9;
    private Boolean operationOr;
    private String categoryDiscounts;
    private String filter = "Не выбрано";
    private String filterOrg = "Не выбрано";
    private Set<CategoryDiscount> categoryDiscountSet;
    private Set<CategoryOrg> categoryOrgs;

    public String getFilterOrg() {
        return filterOrg;
    }

    public void setFilterOrg(String filterOrg) {
        this.filterOrg = filterOrg;
    }

    public Set<CategoryOrg> getCategoryOrgs() {
        return categoryOrgs;
    }

    public void setCategoryOrgs(Set<CategoryOrg> categoryOrgs) {
        this.categoryOrgs = categoryOrgs;
    }

    public Set<CategoryDiscount> getCategoryDiscountSet() {
        return categoryDiscountSet;
    }

    public void setCategoryDiscountSet(Set<CategoryDiscount> categoryDiscountSet) {
        this.categoryDiscountSet = categoryDiscountSet;
    }

    public List<Long> getIdOfCategoryList() {
        return idOfCategoryList;
    }

    public List<Long> getIdOfCategoryOrgList(){
        return idOfCategoryOrgList;
    }

    public String getFilter() {
        return filter;
    }

    public String getCategoryDiscounts() {
        return categoryDiscounts;
    }

    public void setCategoryDiscounts(String categoryDiscounts) {
        this.categoryDiscounts = categoryDiscounts;
    }

    public Boolean getOperationOr() {
        return operationOr;
    }

    public void setOperationOr(Boolean operationOr) {
        this.operationOr = operationOr;
    }

    private int priority;
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isComplex0() {
        return complex0;
    }

    public void setComplex0(boolean complex0) {
        this.complex0 = complex0;
    }

    public boolean isComplex1() {
        return complex1;
    }

    public void setComplex1(boolean complex1) {
        this.complex1 = complex1;
    }

    public boolean isComplex2() {
        return complex2;
    }

    public void setComplex2(boolean complex2) {
        this.complex2 = complex2;
    }

    public boolean isComplex3() {
        return complex3;
    }

    public void setComplex3(boolean complex3) {
        this.complex3 = complex3;
    }

    public boolean isComplex4() {
        return complex4;
    }

    public void setComplex4(boolean complex4) {
        this.complex4 = complex4;
    }

    public boolean isComplex5() {
        return complex5;
    }

    public void setComplex5(boolean complex5) {
        this.complex5 = complex5;
    }

    public boolean isComplex6() {
        return complex6;
    }

    public void setComplex6(boolean complex6) {
        this.complex6 = complex6;
    }

    public boolean isComplex7() {
        return complex7;
    }

    public void setComplex7(boolean complex7) {
        this.complex7 = complex7;
    }

    public boolean isComplex8() {
        return complex8;
    }

    public void setComplex8(boolean complex8) {
        this.complex8 = complex8;
    }

    public boolean isComplex9() {
        return complex9;
    }

    public void setComplex9(boolean complex9) {
        this.complex9 = complex9;
    }

    public void completeCategoryListSelection(Map<Long, String> categoryMap) throws HibernateException {
        //To change body of implemented methods use File | Settings | File Templates.
         if(null != categoryMap) {
             idOfCategoryList = new ArrayList<Long>();
             if(categoryMap.isEmpty()){
                  filter = "Не выбрано";
             } else {
                 filter="";
                 for(Long idOfCategory: categoryMap.keySet()){
                     idOfCategoryList.add(idOfCategory);
                     filter=filter.concat(categoryMap.get(idOfCategory)+ "; ");
                 }
                 filter = filter.substring(0,filter.length()-2);
                 categoryDiscounts=idOfCategoryList.toString();
                 categoryDiscounts=categoryDiscounts.substring(1,categoryDiscounts.length()-1);

             }

         }
    }

    public void completeCategoryOrgListSelection(Map<Long, String> categoryOrgMap) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
        if(null != categoryOrgMap) {
            idOfCategoryOrgList = new ArrayList<Long>();
            if(categoryOrgMap.isEmpty()){
                filterOrg = "Не выбрано";
            } else {
                filterOrg="";
                for(Long idOfCategoryOrg: categoryOrgMap.keySet()){
                    idOfCategoryOrgList.add(idOfCategoryOrg);
                    filterOrg=filterOrg.concat(categoryOrgMap.get(idOfCategoryOrg)+ "; ");
                }
                filterOrg = filterOrg.substring(0,filterOrg.length()-1);
            }

        }
    }

    public String getPageFilename() {
        return "option/discountrule/create";
    }

    @Override
    public void onShow() throws Exception {
        this.description = "";
        this.complex0 = false;
        this.complex1 = false;
        this.complex2 = false;
        this.complex3 = false;
        this.complex4 = false;
        this.complex5 = false;
        this.complex6 = false;
        this.complex7 = false;
        this.complex8 = false;
        this.complex9 = false;
        this.priority = 0;
        this.categoryDiscounts = "";
        this.operationOr=false;
        this.filter="Не выбрано";
        this.filterOrg="Не выбрано";
    }

    @PersistenceContext
    EntityManager em;

    @Transactional
    public void createRule() throws Exception {
//        CategoryDiscount categorydiscount = (CategoryDiscount) session.load(CategoryDiscount.class, this.categorydiscount.getIdOfCategory());
        DiscountRule discountRule = new DiscountRule();
        discountRule.setDescription(description);
        discountRule.setComplex0(complex0?1:0);
        discountRule.setComplex1(complex1?1:0);
        discountRule.setComplex2(complex2?1:0);
        discountRule.setComplex3(complex3?1:0);
        discountRule.setComplex4(complex4?1:0);
        discountRule.setComplex5(complex5?1:0);
        discountRule.setComplex6(complex6?1:0);
        discountRule.setComplex7(complex7?1:0);
        discountRule.setComplex8(complex8?1:0);
        discountRule.setComplex9(complex9?1:0);
        discountRule.setPriority(priority);
        discountRule.setOperationOr(operationOr);
        discountRule.setCategoryDiscounts(categoryDiscounts);
        this.categoryDiscountSet = new HashSet<CategoryDiscount>();
        if (!this.idOfCategoryList.isEmpty()) {
            List categoryList = DAOUtils.getCategoryDiscountListWithIds(em, this.idOfCategoryList);
            for (Object object: categoryList){
                 this.categoryDiscountSet.add((CategoryDiscount) object);
            }
            discountRule.setCategoriesDiscounts(this.categoryDiscountSet);
        }
        if (!this.idOfCategoryOrgList.isEmpty()) {
            List categoryOrgList = DAOUtils.getCategoryOrgWithIds(em, this.idOfCategoryOrgList);
            for (Object object: categoryOrgList){
                discountRule.getCategoryOrgs().add((CategoryOrg) object);
            }
        }

        em.persist(discountRule);
        printMessage("Правило зарегистрировано успешно");
    }
}
