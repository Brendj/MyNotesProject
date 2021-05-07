package ru.iteco.restservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by nuc on 05.05.2021.
 */
@Entity
@Table(name = "cf_categoryorg")
public class CategoryOrg {
    @Id
    @Column(name = "idOfCategoryOrg")
    private long idOfCategoryOrg;

    @Column
    private String categoryName;

    public long getIdOfCategoryOrg() {
        return idOfCategoryOrg;
    }

    public void setIdOfCategoryOrg(long idOfCategoryOrg) {
        this.idOfCategoryOrg = idOfCategoryOrg;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /*private Set<Org> orgsInternal = new HashSet<Org>();
    private Set<CategoryDiscount> categoryDiscountInternal = new HashSet<CategoryDiscount>();
    private Set<DiscountRule> discountRulesInternal = new HashSet<DiscountRule>();*/
}
