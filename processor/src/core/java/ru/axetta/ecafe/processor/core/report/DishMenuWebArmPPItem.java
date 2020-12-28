/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import java.util.Date;

public class DishMenuWebArmPPItem {
    private String codeISPP;
    private String dishname;
    private String componentsofdish;
    private String idsupplier;
    private String price;
    private Date dateFrom;
    private Date dateTo;
    private String agegroup;
    private String typeOfProduction;
    private String typefood;
    private String category;
    private String subcategory;
    private String calories;
    private String qty;
    private String protein;
    private String fat;
    private String carbohydrates;
    private String barcode;
    private String countInMenu;
    private String countInComplex;
    private Integer archived;

    public String getCodeISPP() {
        return codeISPP;
    }

    public void setCodeISPP(String codeISPP) {
        this.codeISPP = codeISPP;
    }

    public String getDishname() {
        return dishname;
    }

    public void setDishname(String dishname) {
        this.dishname = dishname;
    }

    public String getComponentsofdish() {
        return componentsofdish;
    }

    public void setComponentsofdish(String componentsofdish) {
        this.componentsofdish = componentsofdish;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAgegroup() {
        return agegroup;
    }

    public void setAgegroup(String agegroup) {
        this.agegroup = agegroup;
    }

    public String getTypeOfProduction() {
        return typeOfProduction;
    }

    public void setTypeOfProduction(String typeOfProduction) {
        this.typeOfProduction = typeOfProduction;
    }

    public String getTypefood() {
        return typefood;
    }

    public void setTypefood(String typefood) {
        this.typefood = typefood;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(String carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public String getCountInMenu() {
        return countInMenu;
    }

    public void setCountInMenu(String countInMenu) {
        this.countInMenu = countInMenu;
    }

    public String getCountInComplex() {
        return countInComplex;
    }

    public void setCountInComplex(String countInComplex) {
        this.countInComplex = countInComplex;
    }

    public String getIdsupplier() {
        return idsupplier;
    }

    public void setIdsupplier(String idsupplier) {
        this.idsupplier = idsupplier;
    }

    public Integer getArchived() {
        return archived;
    }

    public void setArchived(Integer archived) {
        this.archived = archived;
    }
}
