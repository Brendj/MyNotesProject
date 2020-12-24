/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.models;

import org.apache.commons.collections4.CollectionUtils;

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

    @OneToOne(mappedBy = "categoryDiscount", fetch = FetchType.EAGER)
    private CategoryDiscountDTSZN categoryDiscountDTSZN;

    @OneToMany(mappedBy = "categoryDiscount", fetch = FetchType.EAGER)
    private List<CodeMSP> codeMSPs;

    @Column(name = "categorytype")
    private Integer categoryType;

    public Integer getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(Integer categoryType) {
        this.categoryType = categoryType;
    }

    public List<CodeMSP> getCodeMSPs() {
        return codeMSPs;
    }

    public void setCodeMSPs(List<CodeMSP> codeMSPs) {
        this.codeMSPs = codeMSPs;
    }

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

    public CodeMSP getMSPByClient(Client client) {
        if(CollectionUtils.isEmpty(codeMSPs)){
            return null;
        }
        for(CodeMSP msp : codeMSPs){
            for(CodeMspAgeTypeGroup group : msp.getAgeTypeGroupList()){
                if(group.getAgeTypeGroup().equals(client.getAgeGroup())){
                    return msp;
                }
            }
        }
        return  null;
    }
}
