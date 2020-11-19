/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.dtszn.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "cf_categorydiscounts_dszn")
public class CategoryDiscountDTSZN {
    @Id
    @Column(name = "idofcategorydiscountdszn")
    private Long idOfCategoryDiscountDTSZN;

    @Column(name = "code")
    private Integer code;

    @Column(name = "description")
    private String description;

    @Column(name = "etpcode")
    private Long ETPCode;

    public Long getIdOfCategoryDiscountDTSZN() {
        return idOfCategoryDiscountDTSZN;
    }

    public void setIdOfCategoryDiscountDTSZN(Long idOfCategoryDiscountDTSZN) {
        this.idOfCategoryDiscountDTSZN = idOfCategoryDiscountDTSZN;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getETPCode() {
        return ETPCode;
    }

    public void setETPCode(Long ETPCode) {
        this.ETPCode = ETPCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CategoryDiscountDTSZN that = (CategoryDiscountDTSZN) o;
        return Objects.equals(idOfCategoryDiscountDTSZN, that.idOfCategoryDiscountDTSZN) && Objects
                .equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfCategoryDiscountDTSZN, code);
    }
}
