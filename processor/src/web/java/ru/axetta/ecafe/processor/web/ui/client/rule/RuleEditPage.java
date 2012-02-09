/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client.rule;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.client.category.CategoryListSelectPage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 13:56
 * To change this template use File | Settings | File Templates.
 */
public class RuleEditPage extends BasicWorkspacePage implements CategoryListSelectPage.CompleteHandlerList{

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

    public void completeCategorySelection(Session session, Long idOfCategory) throws Exception {
        if (null != idOfCategory) {
            CategoryDiscount category = (CategoryDiscount) session.load(CategoryDiscount.class, idOfCategory);
            //this.category = new CategoryItem(category);
        }
    }
    public String getPageFilename() {
        return "client/rule/edit";
    }

    public void fill(Session session, Long idOfRule) throws Exception {
        DiscountRule discountRule = (DiscountRule) session.load(DiscountRule.class, idOfRule);
        if (null != discountRule.getCategoryDiscounts() && !discountRule.getCategoryDiscounts().equals("")) {
            String[] idOfCategoryDiscountsString=discountRule.getCategoryDiscounts().split(", ");
            Long[] numbs=new Long[idOfCategoryDiscountsString.length];
            for(int i=0; i<idOfCategoryDiscountsString.length;i++){
                numbs[i]=Long.parseLong(idOfCategoryDiscountsString[i]);
            }
            Criteria catCriteria = session.createCriteria(CategoryDiscount.class);
            catCriteria.add(Restrictions.in("idOfCategoryDiscount", numbs));
            //List<CategoryDiscount> categoryDiscountList = ;
            this.categoryDiscountSet = new HashSet<CategoryDiscount>();
            StringBuilder sb=new StringBuilder();
            for(Object object: catCriteria.list()){
                CategoryDiscount categoryDiscount = (CategoryDiscount)object;
                this.categoryDiscountSet.add(categoryDiscount);
                sb.append(categoryDiscount.getCategoryName());
                sb.append("; ");
            }
            String result=sb.toString();
            this.setFilter(result);
        }

        fill(discountRule);
    }

    public void updateRule(Session persistenceSession, Long idOfRule) throws Exception {
        DiscountRule discountRule = (DiscountRule) persistenceSession.load(DiscountRule.class, idOfRule);
        //CategoryDiscount categoryDiscount = (CategoryDiscount) persistenceSession.load(CategoryDiscount.class, this.category.getIdOfCategory());
       // discountRule.setCategoryDiscount(categoryDiscount);
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
        discountRule.setOperationOr(operationor);
        discountRule.setCategoryDiscounts(categoryDiscounts);
        this.categoryDiscountSet = new HashSet<CategoryDiscount>();
        Criteria categoryCrioteria = persistenceSession.createCriteria(CategoryDiscount.class);
        categoryCrioteria.add(Restrictions.in("idOfCategoryDiscount",this.idOfCategoryList));
        for (Object object: categoryCrioteria.list()){
            this.categoryDiscountSet.add((CategoryDiscount) object);
        }
        discountRule.setCategoriesDiscounts(this.categoryDiscountSet);
        persistenceSession.update(discountRule);
        fill(discountRule);
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
        this.categoryDiscounts = discountRule.getCategoryDiscounts();
       // this.filter= discountRule.getCategoryDiscounts();
        this.operationor=discountRule.isOperationOr();
    }
}