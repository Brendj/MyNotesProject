/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

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
    private String discountRules;
    private Set<DiscountRule> discountRulesInternal = new HashSet<DiscountRule>();
    private Set<Client> clientsInternal = new HashSet<Client>();

    public Set<Client> getClients(){
        return getClientsInternal();
    }

    private Set<Client> getClientsInternal() {
        return clientsInternal;
    }

    private void setClientsInternal(Set<Client> clientsInternal) {
        this.clientsInternal = clientsInternal;
    }

    public Set<DiscountRule> getDiscountsRules() {
        return getDiscountRulesInternal();
    }

    public void setDiscountsRules(Set<DiscountRule> discountRulesInternal) {
        this.discountRulesInternal = discountRulesInternal;
    }

    private Set<DiscountRule> getDiscountRulesInternal() {
        return discountRulesInternal;
    }

    private void setDiscountRulesInternal(Set<DiscountRule> discountRulesInternal) {
        this.discountRulesInternal = discountRulesInternal;
    }

    public String getDiscountRules() {
        return discountRules;
    }

    public void setDiscountRules(String discountRules) {
        this.discountRules = discountRules;
    }


    public CategoryDiscount() {
    }

    public CategoryDiscount(long idOfCategoryDiscount, String categoryName, String discountRules, String description,
            Date createdDate, Date lastUpdate) {
        this.idOfCategoryDiscount = idOfCategoryDiscount;
        this.categoryName = categoryName;
        this.description = description;
        this.createdDate = createdDate;
        this.lastUpdate = lastUpdate;
        this.discountRules=discountRules;
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
                /*
    private Set<DiscountRule> getDiscountRulesInternal() {
        return discountRules;
    }

    private void setDiscountRulesInternal(Set<DiscountRule> discountRules) {
        this.discountRules = discountRules;
    }

    public Set<DiscountRule> getDiscountRules() {
        return Collections.unmodifiableSet(getDiscountRulesInternal());
    }
                  */

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
               + + '\'' + ", discountRules='" + discountRules + '\'' + '\'' + ", description='" + description + '\'' + ", createdDate=" + createdDate + ", lastUpdate="
                + lastUpdate + '}';
    }
}
