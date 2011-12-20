/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 05.12.11
 * Time: 15:51
 * To change this template use File | Settings | File Templates.
 */
public class CategoryDiscount {
    private long idOfCategoryDiscount;
    private String categoryName;
    private String description;
    private Date createdDate;
    private Date lastUpdate;
    private Set<DiscountRule> discountRules = new HashSet<DiscountRule>();

    public CategoryDiscount() {
    }

    public CategoryDiscount(long idOfCategoryDiscount, String categoryName, String description, Date createdDate,
            Date lastUpdate) {
        this.idOfCategoryDiscount = idOfCategoryDiscount;
        this.categoryName = categoryName;
        this.description = description;
        this.createdDate = createdDate;
        this.lastUpdate = lastUpdate;
    }

    public long getIdOfCategoryDiscount() {
        return idOfCategoryDiscount;
    }

    public void setIdOfCategoryDiscount(long idOfCategoryDiscount) {
        this.idOfCategoryDiscount = idOfCategoryDiscount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    private Set<DiscountRule> getDiscountRulesInternal() {
        return discountRules;
    }

    private void setDiscountRulesInternal(Set<DiscountRule> discountRules) {
        this.discountRules = discountRules;
    }

    public Set<DiscountRule> getDiscountRules() {
        return Collections.unmodifiableSet(getDiscountRulesInternal());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CategoryDiscount that = (CategoryDiscount) o;

        if (idOfCategoryDiscount != that.idOfCategoryDiscount) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (idOfCategoryDiscount ^ (idOfCategoryDiscount >>> 32));
    }

    @Override
    public String toString() {
        return "CategoryDiscount{" + "idOfCategoryDiscount=" + idOfCategoryDiscount + ", categoryName='" + categoryName
                + '\'' + ", description='" + description + '\'' + ", createdDate=" + createdDate + ", lastUpdate="
                + lastUpdate + '}';
    }
}
