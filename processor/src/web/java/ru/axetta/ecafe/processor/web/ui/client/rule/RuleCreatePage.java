/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client.rule;

import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.client.category.CategoryListSelectPage;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 10:07
 * To change this template use File | Settings | File Templates.
 */
public class RuleCreatePage extends BasicWorkspacePage
        implements CategoryListSelectPage.CompleteHandlerList {
         /*
    public static class CategoryItem {

        private final Long idOfCategory;
        private final String categoryName;

        public CategoryItem(CategoryDiscount category) {
            this.idOfCategory = category.getIdOfCategoryDiscount();
            this.categoryName = category.getCategoryName();
        }

        public CategoryItem() {
            this.idOfCategory = null;
            this.categoryName = null;
        }

        public Long getIdOfCategory() {
            return idOfCategory;
        }

        public String getCategoryName() {
            return categoryName;
        }
    }
       */
    private List<Long> idOfCategoryList = new ArrayList<Long>();
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

    public List<Long> getIdOfCategoryList() {
        return idOfCategoryList;
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

    public String getPageFilename() {
        return "client/rule/create";
    }

    public void fill(Session session) throws Exception {
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
        this.filter="";
    }

    public void createRule(Session session) throws Exception {
//        CategoryDiscount category = (CategoryDiscount) session.load(CategoryDiscount.class, this.category.getIdOfCategory());
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
        session.save(discountRule);
    }
}
