/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtDish;

import java.math.BigDecimal;

public class WtDishInfo {
    private Long idOfDish;
    private String dishName;
    private Long price;
    private Integer calories;
    private String qty;
    private Integer carbohydrates;
    private Integer fat;
    private Integer protein;
    private String componentsOfDish;

    public WtDishInfo(WtDish wtDish) {
        this.idOfDish = wtDish.getIdOfDish();
        this.dishName = wtDish.getDishName();
        this.price = wtDish.getPrice().multiply(new BigDecimal(100)).longValue();
        this.calories = wtDish.getCalories();
        this.qty = wtDish.getQty();
        this.carbohydrates = wtDish.getCarbohydrates();
        this.fat = wtDish.getFat();
        this.protein = wtDish.getProtein();
        this.componentsOfDish = wtDish.getComponentsOfDish();
    }

    public WtDishInfo(Long idOfDish, String dishName, Long price, Integer calories, String qty, Integer carbohydrates,
            Integer fat, Integer protein, String componentsOfDish) {
        this.idOfDish = idOfDish;
        this.dishName = dishName;
        this.price = price;
        this.calories = calories;
        this.qty = qty;
        this.carbohydrates = carbohydrates;
        this.fat = fat;
        this.protein = protein;
        this.componentsOfDish = componentsOfDish;
    }

    public Long getIdOfDish() {
        return idOfDish;
    }

    public void setIdOfDish(Long idOfDish) {
        this.idOfDish = idOfDish;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public Integer getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(Integer carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public Integer getFat() {
        return fat;
    }

    public void setFat(Integer fat) {
        this.fat = fat;
    }

    public Integer getProtein() {
        return protein;
    }

    public void setProtein(Integer protein) {
        this.protein = protein;
    }

    public String getComponentsOfDish() {
        return componentsOfDish;
    }

    public void setComponentsOfDish(String componentsOfDish) {
        this.componentsOfDish = componentsOfDish;
    }
}
