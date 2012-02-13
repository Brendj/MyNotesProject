/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 06.02.12
 * Time: 11:09
 * To change this template use File | Settings | File Templates.
 */
public class CategoryOrg {

    private long idOfCategoryOrg;
    private String categoryName;
    private Set<Org> orgsInternal = new HashSet<Org>(0);
    private Set<CategoryDiscount> categoryDiscountInternal;
    private Set<DiscountRule> discountRulesInternal;

    public Set<DiscountRule> getDiscountRules() {
        return Collections.unmodifiableSet(getDiscountRulesInternal());
    }

    public void setDiscountRules(Set<DiscountRule> discountRules) {
        this.discountRulesInternal = discountRules;
    }

    private Set<DiscountRule> getDiscountRulesInternal() {
        return discountRulesInternal;
    }

    private void setDiscountRulesInternal(Set<DiscountRule> discountRulesInternal) {
        this.discountRulesInternal = discountRulesInternal;
    }

    public Set<CategoryDiscount> getCategoryDiscount() {
         return getCategoryDiscountInternal();
    }

    public void setCategoryDiscount(Set<CategoryDiscount> categoryDiscount) {
        this.categoryDiscountInternal = categoryDiscount;
    }

    private Set<CategoryDiscount> getCategoryDiscountInternal() {
        return categoryDiscountInternal;
    }

    private void setCategoryDiscountInternal(Set<CategoryDiscount> categoryDiscountInternal) {
        this.categoryDiscountInternal = categoryDiscountInternal;
    }

    public Set<Org> getOrgs() {
        return getOrgsInternal();
    }

    public void setOrgs(Set<Org> orgsInternal) {
        this.orgsInternal = orgsInternal;
    }

    private Set<Org> getOrgsInternal() {
        return orgsInternal;
    }

    private void setOrgsInternal(Set<Org> orgsInternal) {
        this.orgsInternal = orgsInternal;
    }


    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public long getIdOfCategoryOrg() {
        return idOfCategoryOrg;
    }

    public void setIdOfCategoryOrg(long idOfCategoryOrg) {
        this.idOfCategoryOrg = idOfCategoryOrg;
    }

    @Override
    public String toString() {
        return "CategoryOrg{" +
                "idOfCategoryOrg=" + idOfCategoryOrg +
                ", categoryName='" + categoryName + '\'' +
                ", orgsInternal=" + orgsInternal +
                '}';
    }
}
