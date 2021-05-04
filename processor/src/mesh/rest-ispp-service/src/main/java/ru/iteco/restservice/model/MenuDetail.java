/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "cf_menudetails")
public class MenuDetail {
    @Id
    @Column(name = "idofmenudetail")
    private Long idOfMenuDetail;

    @Column(name = "menudetailname")
    private String menuDetailName;

    @Column(name = "menudetailoutput")
    private String menuDetailOutput;

    @Column(name = "calories")
    private BigDecimal calories;

    @Column(name = "protein")
    private BigDecimal protein;

    @Column(name = "fat")
    private BigDecimal fat;

    @Column(name = "carbohydrates")
    private BigDecimal carbohydrates;

    public Long getIdOfMenuDetail() {
        return idOfMenuDetail;
    }

    public void setIdOfMenuDetail(Long idOfMenuDetail) {
        this.idOfMenuDetail = idOfMenuDetail;
    }

    public String getMenuDetailName() {
        return menuDetailName;
    }

    public void setMenuDetailName(String menuDetailName) {
        this.menuDetailName = menuDetailName;
    }

    public String getMenuDetailOutput() {
        return menuDetailOutput;
    }

    public void setMenuDetailOutput(String menuDetailOutput) {
        this.menuDetailOutput = menuDetailOutput;
    }

    public BigDecimal getCalories() {
        return calories;
    }

    public void setCalories(BigDecimal calories) {
        this.calories = calories;
    }

    public BigDecimal getProtein() {
        return protein;
    }

    public void setProtein(BigDecimal protein) {
        this.protein = protein;
    }

    public BigDecimal getFat() {
        return fat;
    }

    public void setFat(BigDecimal fat) {
        this.fat = fat;
    }

    public BigDecimal getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(BigDecimal carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MenuDetail that = (MenuDetail) o;
        return Objects.equals(idOfMenuDetail, that.idOfMenuDetail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfMenuDetail);
    }
}
