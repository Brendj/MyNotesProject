/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client.rule;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.client.category.CategorySelectPage;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 13:56
 * To change this template use File | Settings | File Templates.
 */
public class RuleEditPage extends BasicWorkspacePage implements CategorySelectPage.CompleteHandler {

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

    private CategoryItem category = new CategoryItem();
    private String description;
    private int complex0;
    private int complex1;
    private int complex2;
    private int complex3;
    private int complex4;
    private int complex5;
    private int complex6;
    private int complex7;
    private int complex8;
    private int complex9;
    private int complexes;

    public CategoryItem getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getComplex0() {
        return complex0;
    }

    public void setComplex0(int complex0) {
        this.complex0 = complex0;
    }

    public int getComplex1() {
        return complex1;
    }

    public void setComplex1(int complex1) {
        this.complex1 = complex1;
    }

    public int getComplex2() {
        return complex2;
    }

    public void setComplex2(int complex2) {
        this.complex2 = complex2;
    }

    public int getComplex3() {
        return complex3;
    }

    public void setComplex3(int complex3) {
        this.complex3 = complex3;
    }

    public int getComplex4() {
        return complex4;
    }

    public void setComplex4(int complex4) {
        this.complex4 = complex4;
    }

    public int getComplex5() {
        return complex5;
    }

    public void setComplex5(int complex5) {
        this.complex5 = complex5;
    }

    public int getComplex6() {
        return complex6;
    }

    public void setComplex6(int complex6) {
        this.complex6 = complex6;
    }

    public int getComplex7() {
        return complex7;
    }

    public void setComplex7(int complex7) {
        this.complex7 = complex7;
    }

    public int getComplex8() {
        return complex8;
    }

    public void setComplex8(int complex8) {
        this.complex8 = complex8;
    }

    public int getComplex9() {
        return complex9;
    }

    public void setComplex9(int complex9) {
        this.complex9 = complex9;
    }

    public int getComplexes() {
        return complexes;
    }

    public void setComplexes(int complexes) {
        this.complexes = complexes;
    }

    public void completeCategorySelection(Session session, Long idOfCategory) throws Exception {
        if (null != idOfCategory) {
            CategoryDiscount category = (CategoryDiscount) session.load(CategoryDiscount.class, idOfCategory);
            this.category = new CategoryItem(category);
        }
    }
    public String getPageFilename() {
        return "client/rule/edit";
    }

    public void fill(Session session, Long idOfRule) throws Exception {
        DiscountRule discountRule = (DiscountRule) session.load(DiscountRule.class, idOfRule);
        fill(discountRule);
    }

    public void updateRule(Session persistenceSession, Long idOfRule) throws Exception {
        DiscountRule discountRule = (DiscountRule) persistenceSession.load(DiscountRule.class, idOfRule);
        CategoryDiscount categoryDiscount = (CategoryDiscount) persistenceSession.load(CategoryDiscount.class,
                this.category.getIdOfCategory());
        discountRule.setCategoryDiscount(categoryDiscount);
        discountRule.setDescription(description);
        discountRule.setComplex0(complex0);
        discountRule.setComplex1(complex1);
        discountRule.setComplex2(complex2);
        discountRule.setComplex3(complex3);
        discountRule.setComplex4(complex4);
        discountRule.setComplex5(complex5);
        discountRule.setComplex6(complex6);
        discountRule.setComplex7(complex7);
        discountRule.setComplex8(complex8);
        discountRule.setComplex9(complex9);
        discountRule.setComplexes(complexes);
        persistenceSession.update(discountRule);
        fill(discountRule);
    }

    private void fill(DiscountRule discountRule) throws Exception {
        this.category = new CategoryItem(discountRule.getCategoryDiscount());
        this.description = discountRule.getDescription();
        this.complex0 = discountRule.getComplex0();
        this.complex1 = discountRule.getComplex1();
        this.complex2 = discountRule.getComplex2();
        this.complex3 = discountRule.getComplex3();
        this.complex4 = discountRule.getComplex4();
        this.complex5 = discountRule.getComplex5();
        this.complex6 = discountRule.getComplex6();
        this.complex7 = discountRule.getComplex7();
        this.complex8 = discountRule.getComplex8();
        this.complex9 = discountRule.getComplex9();
        this.complexes = discountRule.getComplexes();
    }
}