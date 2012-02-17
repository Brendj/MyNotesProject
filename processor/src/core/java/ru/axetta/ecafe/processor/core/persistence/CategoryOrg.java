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
public class CategoryOrg implements Comparable {

    private long idOfCategoryOrg;
    private String categoryName;
    private Set<Org> orgsInternal = new HashSet<Org>();
    private Set<CategoryDiscount> categoryDiscountInternal = new HashSet<CategoryDiscount>();
    private Set<DiscountRule> discountRulesInternal = new HashSet<DiscountRule>();

    public Set<DiscountRule> getDiscountRules() {
        return getDiscountRulesInternal();
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

    private void setOrgs(Set<Org> orgsInternal) {
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
               // ", orgsInternal=" + orgsInternal +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CategoryOrg that = (CategoryOrg) o;

        if (idOfCategoryOrg != that.idOfCategoryOrg) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idOfCategoryOrg ^ (idOfCategoryOrg >>> 32));
        result = 31 * result + (categoryName != null ? categoryName.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Object otherCategoryOrg) {
        if(!(otherCategoryOrg instanceof CategoryOrg)){
            throw new ClassCastException("Invalid object");
        }
        Long otherIdOfCategoryOrg = ((CategoryOrg) otherCategoryOrg).getIdOfCategoryOrg();
        if(this.idOfCategoryOrg > otherIdOfCategoryOrg)
            return 1;
        else if ( this.idOfCategoryOrg < otherIdOfCategoryOrg )
            return -1;
        else
            return 0;
    }
}
