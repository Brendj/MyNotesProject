package ru.iteco.restservice.model.wt;

import ru.iteco.restservice.model.CategoryDiscount;
import ru.iteco.restservice.model.CategoryOrg;
import ru.iteco.restservice.model.CodeMSP;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by nuc on 05.05.2021.
 */
@Entity
@Table(name = "cf_wt_discountrules")
public class WtDiscountRule {
    @Id
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

    @Column(name = "subCategory")
    private String subCategory;

    @Column(name = "deletedState")
    private Boolean deletedState;

    @ManyToMany
    @JoinTable(name = "cf_wt_discountrules_complexes",
            joinColumns = @JoinColumn(name = "idOfRule"),
            inverseJoinColumns = @JoinColumn(name = "idOfComplex"))
    private Set<WtComplex> complexes = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "cf_wt_discountrules_categoryorg",
            joinColumns = @JoinColumn(name = "idOfRule"),
            inverseJoinColumns = @JoinColumn(name = "idOfCategoryOrg"))
    private Set<CategoryOrg> categoryOrgs = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "cf_wt_discountrules_categorydiscount",
            joinColumns = @JoinColumn(name = "idOfRule"),
            inverseJoinColumns = @JoinColumn(name = "idOfCategoryDiscount"))
    private Set<CategoryDiscount> categoryDiscounts = new HashSet<>();

    /*@ManyToOne
    @JoinColumn(name = "idofcode")
    private CodeMSP codeMSP;*/

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

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public Set<WtComplex> getComplexes() {
        return complexes;
    }

    public void setComplexes(Set<WtComplex> complexes) {
        this.complexes = complexes;
    }

    public Set<CategoryOrg> getCategoryOrgs() {
        return categoryOrgs;
    }

    public void setCategoryOrgs(Set<CategoryOrg> categoryOrgs) {
        this.categoryOrgs = categoryOrgs;
    }

    public Set<CategoryDiscount> getCategoryDiscounts() {
        return categoryDiscounts;
    }

    public void setCategoryDiscounts(Set<CategoryDiscount> categoryDiscounts) {
        this.categoryDiscounts = categoryDiscounts;
    }

    /*public CodeMSP getCodeMSP() {
        return codeMSP;
    }

    public void setCodeMSP(CodeMSP codeMSP) {
        this.codeMSP = codeMSP;
    }*/
}
