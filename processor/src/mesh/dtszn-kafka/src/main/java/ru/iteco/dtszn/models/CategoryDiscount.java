/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.dtszn.models;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cf_categorydiscounts")
public class CategoryDiscount {
    @Id
    @Column(name = "idofcategorydiscount")
    private Long idOfCategoryDiscount;

    @Column(name = "categoryname")
    private String categoryName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "cf_discountrules_categorydiscounts",
            joinColumns = @JoinColumn(name = "idofcategorydiscount"),
            inverseJoinColumns = @JoinColumn(name = "idofrule")
    )
    private Set<DiscountRule> rules;

    @OneToMany
    @JoinColumn(name = "idofcategorydiscount")
    private Set<CategoryDiscountDTSZN> categoryDiscountDTSZN;

    public Long getIdOfCategoryDiscount() {
        return idOfCategoryDiscount;
    }

    public void setIdOfCategoryDiscount(Long idOfCategoryDiscount) {
        this.idOfCategoryDiscount = idOfCategoryDiscount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Set<DiscountRule> getRules() {
        return rules;
    }

    public void setRules(Set<DiscountRule> rules) {
        this.rules = rules;
    }

    public Set<CategoryDiscountDTSZN> getCategoryDiscountDTSZN() {
        return categoryDiscountDTSZN;
    }

    public void setCategoryDiscountDTSZN(Set<CategoryDiscountDTSZN> categoryDiscountDTSZN) {
        this.categoryDiscountDTSZN = categoryDiscountDTSZN;
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
        return Objects.equals(idOfCategoryDiscount, that.idOfCategoryDiscount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfCategoryDiscount);
    }
}
