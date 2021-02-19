/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.models;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cf_wt_discountrules")
public class WtDiscountRule {
    @Id
    @Column(name = "idofrule")
    private Long idOfRule;

    @Column(name = "description")
    private String description;

    @ManyToMany
    @JoinTable(
            name = "cf_discountrules_categorydiscounts",
            joinColumns = @JoinColumn(name = "idofrule"),
            inverseJoinColumns = @JoinColumn(name = "idofcategorydiscount")
    )
    Set<CategoryDiscount> categoryDiscountSet;

    public Long getIdOfRule() {
        return idOfRule;
    }

    public void setIdOfRule(Long idOfRule) {
        this.idOfRule = idOfRule;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<CategoryDiscount> getCategoryDiscountSet() {
        return categoryDiscountSet;
    }

    public void setCategoryDiscountSet(Set<CategoryDiscount> categoryDiscountSet) {
        this.categoryDiscountSet = categoryDiscountSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WtDiscountRule that = (WtDiscountRule) o;
        return Objects.equals(idOfRule, that.idOfRule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfRule);
    }
}
