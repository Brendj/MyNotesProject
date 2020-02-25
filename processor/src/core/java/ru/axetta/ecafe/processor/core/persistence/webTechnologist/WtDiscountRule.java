/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cf_wt_discountrules")
public class WtDiscountRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idOfRule")
    private Long idOfRule;

    @Column(name = "description")
    private String description;

    @Column(name = "priority")
    private int priority;

    @Column(name = "rate")
    private int rate;

    @Column(name = "operationOr")
    private boolean operationOr;

    @ManyToOne
    @JoinColumn(name = "idOfCategoryDiscount")
    private CategoryDiscount categoryDiscount;

    @Column(name = "idOfSuperCategory")
    private Long idOfSuperCategory;

    @ManyToMany
    @JoinTable(name = "cf_wt_discountrules_complexes",
            joinColumns = @JoinColumn(name = "idOfRule"),
            inverseJoinColumns = @JoinColumn(name = "idOfComplex"))
    private Set<WtComplex> complexes = new HashSet<>();

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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public boolean isOperationOr() {
        return operationOr;
    }

    public void setOperationOr(boolean operationOr) {
        this.operationOr = operationOr;
    }

    public CategoryDiscount getCategoryDiscount() {
        return categoryDiscount;
    }

    public void setCategoryDiscount(CategoryDiscount categoryDiscount) {
        this.categoryDiscount = categoryDiscount;
    }

    public Long getIdOfSuperCategory() {
        return idOfSuperCategory;
    }

    public void setIdOfSuperCategory(Long idOfSuperCategory) {
        this.idOfSuperCategory = idOfSuperCategory;
    }

    public Set<WtComplex> getComplexes() {
        return complexes;
    }

    public void setComplexes(Set<WtComplex> complexes) {
        this.complexes = complexes;
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
        return priority == that.priority && rate == that.rate && operationOr == that.operationOr && idOfRule
                .equals(that.idOfRule) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfRule, description, priority, rate, operationOr);
    }
}
