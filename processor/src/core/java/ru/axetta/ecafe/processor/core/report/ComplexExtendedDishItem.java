/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

public class ComplexExtendedDishItem {

    private String dish;
    private String price;
    private String structure;
    private String category;
    private String subCategory;
    private String calories;
    private String proteins;
    private String fats;
    private String carbohydrates;
    private String code;
    private String weight;
    private String beginDate;
    private String endDate;
    private String idOfDish;

    public ComplexExtendedDishItem(String dish, String price, String structure, String category, String subCategory,
            String calories, String proteins, String fats, String carbohydrates, String code, String weight,
            String beginDate, String endDate, String idOfDish) {
        this.dish = dish;
        this.price = price;
        this.structure = structure;
        this.category = category;
        this.subCategory = subCategory;
        this.calories = calories;
        this.proteins = proteins;
        this.fats = fats;
        this.carbohydrates = carbohydrates;
        this.code = code;
        this.weight = weight;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.idOfDish = idOfDish;
    }

    public String getDish() {
        return dish;
    }

    public void setDish(String dish) {
        this.dish = dish;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getProteins() {
        return proteins;
    }

    public void setProteins(String proteins) {
        this.proteins = proteins;
    }

    public String getFats() {
        return fats;
    }

    public void setFats(String fats) {
        this.fats = fats;
    }

    public String getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(String carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getIdOfDish() {
        return idOfDish;
    }

    public void setIdOfDish(String idOfDish) {
        this.idOfDish = idOfDish;
    }
}
