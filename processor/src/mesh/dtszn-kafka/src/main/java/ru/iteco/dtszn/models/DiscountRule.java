/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.dtszn.models;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cf_discountrules")
@NamedEntityGraphs({
        @NamedEntityGraph(name = "rule_only"),
        @NamedEntityGraph(
                name = "rule.categoryDiscounts",
                attributeNodes = {
                       @NamedAttributeNode(value = "categoryDiscounts", subgraph = "categoryDiscounts.categoryDiscountDTSZN")
                },
                subgraphs = {
                        @NamedSubgraph(
                                name = "categoryDiscounts.categoryDiscountDTSZN",
                                attributeNodes = @NamedAttributeNode("categoryDiscountDTSZN")
                        )
                }
        )
})
public class DiscountRule {
    @Id
    @Column(name = "idofrule")
    private Long idOfRule;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "idofcode")
    private CodeMSP codeMSP;

    @ManyToMany
    @JoinTable(
            name = "cf_discountrules_categorydiscounts",
            joinColumns = @JoinColumn(name = "idofrule"),
            inverseJoinColumns = @JoinColumn(name = "idofcategorydiscount")
    )
    private List<CategoryDiscount> categoryDiscounts;

    public List<CategoryDiscount> getCategoryDiscounts() {
        return categoryDiscounts;
    }

    public void setCategoryDiscounts(List<CategoryDiscount> categoryDiscounts) {
        this.categoryDiscounts = categoryDiscounts;
    }

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

    public CodeMSP getCodeMSP() {
        return codeMSP;
    }

    public void setCodeMSP(CodeMSP codeMSP) {
        this.codeMSP = codeMSP;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DiscountRule that = (DiscountRule) o;
        return Objects.equals(idOfRule, that.idOfRule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfRule);
    }
}
