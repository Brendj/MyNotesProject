
/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.dataflow;

import ru.axetta.ecafe.processor.core.persistence.MenuDetail;



public class PreorderMenuItemExt {

    private String group;
    private String name;
    private String fullName;
    private Long price;
    private Double calories;
    private String output;
    private Integer availableNow;
    private Double protein;
    private Double fat;
    private Double carbohydrates;
    private Long idOfMenuDetail;
    private int amount;
    private Boolean selected;

    public PreorderMenuItemExt() {

    }

    public PreorderMenuItemExt(MenuDetail menuDetail) {
        this.setGroup(menuDetail.getGroupName());
        this.setName(menuDetail.getShortName());
        this.setFullName(menuDetail.getMenuDetailName());
        this.setPrice(menuDetail.getPrice());
        this.setCalories(menuDetail.getCalories());
        this.setOutput(menuDetail.getMenuDetailOutput());
        this.setAvailableNow(menuDetail.getAvailableNow());
        this.setProtein(menuDetail.getProtein());
        this.setCarbohydrates(menuDetail.getCarbohydrates());
        this.setFat(menuDetail.getFat());
        this.setIdOfMenuDetail(menuDetail.getLocalIdOfMenu());
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Double getCalories() {
        return calories;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public Integer getAvailableNow() {
        return availableNow;
    }

    public void setAvailableNow(Integer availableNow) {
        this.availableNow = availableNow;
    }

    public Double getProtein() {
        return protein;
    }

    public void setProtein(Double protein) {
        this.protein = protein;
    }

    public Double getFat() {
        return fat;
    }

    public void setFat(Double fat) {
        this.fat = fat;
    }

    public Double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(Double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public Long getIdOfMenuDetail() {
        return idOfMenuDetail;
    }

    public void setIdOfMenuDetail(Long idOfMenuDetail) {
        this.idOfMenuDetail = idOfMenuDetail;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
