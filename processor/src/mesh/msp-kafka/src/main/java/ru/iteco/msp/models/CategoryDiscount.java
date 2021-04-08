/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.models;

import org.apache.commons.collections4.CollectionUtils;

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

    @ManyToMany
    @JoinTable(
            name = "cf_discountrules_categorydiscounts",
            joinColumns = @JoinColumn(name = "idofcategorydiscount"),
            inverseJoinColumns = @JoinColumn(name = "idofrule")
    )
    private Set<DiscountRule> rules;

    @ManyToMany
    @JoinTable(
            name = "cf_wt_discountrules_categorydiscount",
            joinColumns = @JoinColumn(name = "idofcategorydiscount"),
            inverseJoinColumns = @JoinColumn(name = "idofrule")
    )
    private Set<WtDiscountRule> wtRules;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "cf_clients_categorydiscounts",
            joinColumns = @JoinColumn(name = "idofcategorydiscount"),
            inverseJoinColumns = @JoinColumn(name = "idofclient")
    )
    private Set<Client> clients;

    @OneToOne(mappedBy = "categoryDiscount", fetch = FetchType.EAGER)
    private CategoryDiscountDTSZN categoryDiscountDTSZN;

    @OneToMany(mappedBy = "categoryDiscount", fetch = FetchType.EAGER)
    private Set<CodeMSP> codeMSPs;

    @Column(name = "categorytype")
    private Integer categoryType;

    public Integer getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(Integer categoryType) {
        this.categoryType = categoryType;
    }

    public Set<CodeMSP> getCodeMSPs() {
        return codeMSPs;
    }

    public void setCodeMSPs(Set<CodeMSP> codeMSPs) {
        this.codeMSPs = codeMSPs;
    }

    public Set<WtDiscountRule> getWtRules() {
        return wtRules;
    }

    public void setWtRules(Set<WtDiscountRule> wtRules) {
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

    public Set<DiscountRule> getRules() {
        return rules;
    }

    public void setRules(Set<DiscountRule> rules) {
        this.rules = rules;
    }

    public CategoryDiscountDTSZN getCategoryDiscountDTSZN() {
        return categoryDiscountDTSZN;
    }

    public void setCategoryDiscountDTSZN(CategoryDiscountDTSZN categoryDiscountDTSZN) {
        this.categoryDiscountDTSZN = categoryDiscountDTSZN;
    }

    public Set<Client> getClients() {
        return clients;
    }

    public void setClients(Set<Client> clients) {
        this.clients = clients;
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
