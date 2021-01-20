/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.transit.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "cf_categorydiscounts")
public class CategoryDiscount {
    @Id
    @Column(name = "idofcategorydiscount")
    private Long idOfCategoryDiscount;

    @Column(name = "categoryname")
    private String categoryName;


    @Column(name = "categorytype")
    private Integer categoryType;

    public Integer getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(Integer categoryType) {
        this.categoryType = categoryType;
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
