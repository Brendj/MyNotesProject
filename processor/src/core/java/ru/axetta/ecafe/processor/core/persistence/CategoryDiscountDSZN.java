/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 18.02.17
 * Time: 10:37
 */
public class CategoryDiscountDSZN {
    private int idOfCategoryDiscountDSZN;
    private Integer code;
    private String description;
    private CategoryDiscount categoryDiscount;
    private Long version;
    private Boolean deleted;

    public CategoryDiscountDSZN() {
    }

    public CategoryDiscountDSZN(Integer code, String description, CategoryDiscount categoryDiscount, Long version) {
        this.code = code;
        this.description = description;
        this.categoryDiscount = categoryDiscount;
        this.version = version;
        this.deleted = false;
    }

    public int getIdOfCategoryDiscountDSZN() {
        return idOfCategoryDiscountDSZN;
    }

    public void setIdOfCategoryDiscountDSZN(int idOfCategoryDiscountDSZN) {
        this.idOfCategoryDiscountDSZN = idOfCategoryDiscountDSZN;
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

    public CategoryDiscount getCategoryDiscount() {
        return categoryDiscount;
    }

    public void setCategoryDiscount(CategoryDiscount categoryDiscount) {
        this.categoryDiscount = categoryDiscount;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CategoryDiscountDSZN that = (CategoryDiscountDSZN) o;

        return idOfCategoryDiscountDSZN == that.idOfCategoryDiscountDSZN;

    }

    @Override
    public int hashCode() {
        return (int) (idOfCategoryDiscountDSZN ^ (idOfCategoryDiscountDSZN >>> 32));
    }

    @Override
    public String toString() {
        return "CategoryDiscountDSZN{" +
                "idOfCategoryDiscountDSZN=" + idOfCategoryDiscountDSZN +
                ", code=" + code +
                ", description='" + description + '\'' +
                ", categoryDiscount=" + categoryDiscount +
                ", version=" + version +
                ", deleted=" + deleted +
                '}';
    }
}
