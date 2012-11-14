/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.discountrule;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategoryListSelectPage;
import ru.axetta.ecafe.processor.web.ui.option.categoryorg.CategoryOrgListSelectPage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 13:56
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class RuleEditPage extends BasicWorkspacePage implements CategoryListSelectPage.CompleteHandlerList, CategoryOrgListSelectPage.CompleteHandlerList{

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
    private int priority;
    private boolean operationor;
    private String categoryDiscounts;
    private List<Long> idOfCategoryList = new ArrayList<Long>();
    private String filter = "Не выбрано";
    private Set<CategoryDiscount> categoryDiscountSet;

    public String getIdOfCategoryOrgListString() {
        return idOfCategoryOrgList.toString().replaceAll("[^(0-9-),]","");
    }

    public String getIdOfCategoryListString() {
        return idOfCategoryList.toString().replaceAll("[^(0-9-),]","");
    }

    public Set<CategoryDiscount> getCategoryDiscountSet() {
        return categoryDiscountSet;
    }

    public void setCategoryDiscountSet(Set<CategoryDiscount> categoryDiscountSet) {
        this.categoryDiscountSet = categoryDiscountSet;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public List<Long> getIdOfCategoryList() {
        return idOfCategoryList;
    }

    public String getCategoryDiscounts() {
        return categoryDiscounts;
    }

    public void setCategoryDiscounts(String categoryDiscounts) {
        this.categoryDiscounts = categoryDiscounts;
    }

    public boolean isOperationor() {
        return operationor;
    }

    public void setOperationor(boolean operationor) {
        this.operationor = operationor;
    }

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

    public boolean getComplex0() {
        return complex0;
    }

    public void setComplex0(boolean complex0) {
        this.complex0 = complex0;
    }

    public boolean getComplex1() {
        return complex1;
    }

    public void setComplex1(boolean complex1) {
        this.complex1 = complex1;
    }

    public boolean getComplex2() {
        return complex2;
    }

    public void setComplex2(boolean complex2) {
        this.complex2 = complex2;
    }

    public boolean getComplex3() {
        return complex3;
    }

    public void setComplex3(boolean complex3) {
        this.complex3 = complex3;
    }

    public boolean getComplex4() {
        return complex4;
    }

    public void setComplex4(boolean complex4) {
        this.complex4 = complex4;
    }

    public boolean getComplex5() {
        return complex5;
    }

    public void setComplex5(boolean complex5) {
        this.complex5 = complex5;
    }

    public boolean getComplex6() {
        return complex6;
    }

    public void setComplex6(boolean complex6) {
        this.complex6 = complex6;
    }

    public boolean getComplex7() {
        return complex7;
    }

    public void setComplex7(boolean complex7) {
        this.complex7 = complex7;
    }

    public boolean getComplex8() {
        return complex8;
    }

    public void setComplex8(boolean complex8) {
        this.complex8 = complex8;
    }

    public boolean getComplex9() {
        return complex9;
    }

    public void setComplex9(boolean complex9) {
        this.complex9 = complex9;
    }

    public void completeCategoryListSelection(Map<Long, String> categoryMap) throws Exception {
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
                filter = filter.substring(0,filter.length()-1);
                categoryDiscounts=idOfCategoryList.toString();
                categoryDiscounts=categoryDiscounts.substring(1,categoryDiscounts.length()-1);
            }

        }
    }

    private String filterOrg = "Не выбрано";
    private List<Long> idOfCategoryOrgList = new ArrayList<Long>();
    private Set<CategoryOrg> categoryOrgs;

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
        return "option/discountrule/edit";
    }

    @Transactional
    public void updateRule() throws Exception {
        entity = (DiscountRule) em.merge(entity);
        //CategoryDiscount categoryDiscount = (CategoryDiscount) persistenceSession.load(CategoryDiscount.class, this.categorydiscount.getIdOfCategory());
       // entity.setCategoryDiscount(categoryDiscount);
        entity.setDescription(description);
        entity.setComplex0(complex0?1:0);
        entity.setComplex1(complex1?1:0);
        entity.setComplex2(complex2?1:0);
        entity.setComplex3(complex3?1:0);
        entity.setComplex4(complex4?1:0);
        entity.setComplex5(complex5?1:0);
        entity.setComplex6(complex6?1:0);
        entity.setComplex7(complex7?1:0);
        entity.setComplex8(complex8?1:0);
        entity.setComplex9(complex9?1:0);
        entity.setPriority(priority);
        entity.setOperationOr(operationor);
        //entity.setCategoryDiscounts(categoryDiscounts);
        this.categoryDiscountSet = new HashSet<CategoryDiscount>();
        entity.getCategoriesDiscounts().clear();
        //if (this.idOfCategoryList.isEmpty()) {
        //    for (CategoryDiscount categoryDiscount: entity.getCategoriesDiscounts()){
        //        this.idOfCategoryList.add(categoryDiscount.getIdOfCategoryDiscount());
        //    }
        //}
        if(!this.idOfCategoryList.isEmpty()){
            List categoryList = DAOUtils.getCategoryDiscountListWithIds(em, this.idOfCategoryList);
            StringBuilder stringBuilder = new StringBuilder();
            for (Object object: categoryList){
                this.categoryDiscountSet.add((CategoryDiscount) object);
                stringBuilder.append(((CategoryDiscount) object).getIdOfCategoryDiscount());
                stringBuilder.append(",");
            }
            entity.setCategoriesDiscounts(this.categoryDiscountSet);
            entity.setCategoryDiscounts(stringBuilder.substring(0, stringBuilder.length()-1));
        } else {
            entity.setCategoryDiscounts("");
        }

        entity.getCategoryOrgs().clear();
        if(!this.idOfCategoryOrgList.isEmpty()){
            entity.getCategoryOrgs().clear();
            List categoryOrgList = DAOUtils.getCategoryOrgWithIds(em, this.idOfCategoryOrgList);
            for (Object object: categoryOrgList){
                entity.getCategoryOrgs().add((CategoryOrg) object);
            }
        }
        em.persist(entity);
        fill(entity);
        printMessage("Данные обновлены.");
    }

    private void fill(DiscountRule discountRule) throws Exception {
        this.description = discountRule.getDescription();
        this.complex0 = discountRule.getComplex0()>0;
        this.complex1 = discountRule.getComplex1()>0;
        this.complex2 = discountRule.getComplex2()>0;
        this.complex3 = discountRule.getComplex3()>0;
        this.complex4 = discountRule.getComplex4()>0;
        this.complex5 = discountRule.getComplex5()>0;
        this.complex6 = discountRule.getComplex6()>0;
        this.complex7 = discountRule.getComplex7()>0;
        this.complex8 = discountRule.getComplex8()>0;
        this.complex9 = discountRule.getComplex9()>0;
        this.priority = discountRule.getPriority();

        this.idOfCategoryList.clear();
        if (!discountRule.getCategoriesDiscounts().isEmpty()){
            StringBuilder stringBuilder = new StringBuilder();
            for (CategoryDiscount categoryDiscount: discountRule.getCategoriesDiscounts()){
                stringBuilder.append(categoryDiscount.getCategoryName());
                stringBuilder.append("; ");
                this.idOfCategoryList.add(categoryDiscount.getIdOfCategoryDiscount());
            }
            this.categoryDiscounts=stringBuilder.toString();
        }
        this.idOfCategoryOrgList.clear();
        if(!discountRule.getCategoryOrgs().isEmpty()){
            StringBuilder stringBuilder = new StringBuilder();
            for (CategoryOrg categoryOrg: discountRule.getCategoryOrgs()){
                 stringBuilder.append(categoryOrg.getCategoryName());
                 stringBuilder.append("; ");
                 this.idOfCategoryOrgList.add(categoryOrg.getIdOfCategoryOrg());
            }
        }
        this.operationor=discountRule.isOperationOr();
    }

    public String getFilterOrg() {
        return filterOrg;
    }

    public void setFilterOrg(String filterOrg) {
        this.filterOrg = filterOrg;
    }

    public List<Long> getIdOfCategoryOrgList() {
        return idOfCategoryOrgList;
    }

    public void setIdOfCategoryOrgList(List<Long> idOfCategoryOrgList) {
        this.idOfCategoryOrgList = idOfCategoryOrgList;
    }

    public Set<CategoryOrg> getCategoryOrgs() {
        return categoryOrgs;
    }

    public void setCategoryOrgs(Set<CategoryOrg> categoryOrgs) {
        this.categoryOrgs = categoryOrgs;
    }

    DiscountRule entity;

    public DiscountRule getEntity() {
        return entity;
    }

    public void setEntity(DiscountRule entity) {
        this.entity = entity;
    }

    public String getEntityName() {
        return entity.getDescription();
    }

    @PersistenceContext
    EntityManager em;

    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(getClass()).reload();
    }
    public void reload() throws Exception {
        DiscountRule discountRule = em.merge(entity);

        StringBuilder categoryFilter = new StringBuilder();
        if(!discountRule.getCategoriesDiscounts().isEmpty()){
            for (CategoryDiscount categoryDiscount: discountRule.getCategoriesDiscounts()){
                this.idOfCategoryList.add(categoryDiscount.getIdOfCategoryDiscount());
                categoryFilter.append(categoryDiscount.getCategoryName());
                categoryFilter.append(";");
            }
            this.filter = categoryFilter.substring(0, categoryFilter.length()-1);
        } else {
            this.filter = "Не выбрано";
        }


        StringBuilder categoryOrgFilter = new StringBuilder();
        if(!discountRule.getCategoryOrgs().isEmpty()){
            for (CategoryOrg categoryOrg: discountRule.getCategoryOrgs()){
                this.idOfCategoryOrgList.add(categoryOrg.getIdOfCategoryOrg());
                categoryOrgFilter.append(categoryOrg.getCategoryName());
                categoryOrgFilter.append("; ");
            }
            this.filterOrg=categoryOrgFilter.substring(0, categoryOrgFilter.length()-1);
        } else{
            this.filterOrg="Не выбрано";
        }

        fill(discountRule);
    }
}