/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
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
    private Integer calories;

    @Column(name = "protein")
    private Integer protein;

    @Column(name = "fat")
    private Integer fat;

    @Column(name = "carbohydrates")
    private Integer carbohydrates;

    @ManyToOne(fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "idofmenu", updatable = false, insertable = false)
    private Menu menu;

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

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public Integer getProtein() {
        return protein;
    }

    public void setProtein(Integer protein) {
        this.protein = protein;
    }

    public Integer getFat() {
        return fat;
    }

    public void setFat(Integer fat) {
        this.fat = fat;
    }

    public Integer getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(Integer carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
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
