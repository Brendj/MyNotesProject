/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.dtszn.models;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cf_categorydiscounts")
@NamedEntityGraphs({
        @NamedEntityGraph(name = "only_discount"),
        @NamedEntityGraph(
                name = "discount.categoryDiscountDTSZN",
                attributeNodes = {
                        @NamedAttributeNode(value = "categoryDiscountDTSZN")
                }
        )
})
public class CategoryDiscount {
    @Id
    @Column(name = "idofcategorydiscount")
    private Long idOfCategoryDiscount;

    @Column(name = "categoryname")
    private String categoryName;

    @ManyToMany
    @JoinTable(
            name = "cf_discountrules_categorydiscounts",
            joinColumns = @JoinColumn(name = "idofcategorydiscount"),
            inverseJoinColumns = @JoinColumn(name = "idofrule")
    )
    private List<DiscountRule> rules;

    @ManyToMany
    @JoinTable(
            name = "cf_wt_discountrules_categorydiscount",
            joinColumns = @JoinColumn(name = "idofcategorydiscount"),
            inverseJoinColumns = @JoinColumn(name = "idofrule")
    )
    private List<WtDiscountRule> wtRules;

    @OneToOne(mappedBy = "categoryDiscount")
    private CategoryDiscountDTSZN categoryDiscountDTSZN;

    public List<WtDiscountRule> getWtRules() {
        return wtRules;
    }

    public void setWtRules(List<WtDiscountRule> wtRules) {
        this.wtRules = wtRules;
    }

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

    public List<DiscountRule> getRules() {
        return rules;
    }

    public void setRules(List<DiscountRule> rules) {
        this.rules = rules;
    }

    public CategoryDiscountDTSZN getCategoryDiscountDTSZN() {
        return categoryDiscountDTSZN;
    }

    public void setCategoryDiscountDTSZN(CategoryDiscountDTSZN categoryDiscountDTSZN) {
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
