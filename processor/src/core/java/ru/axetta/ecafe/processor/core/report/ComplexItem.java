/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import java.math.BigDecimal;

public class ComplexItem {
    private String idOfComplex;
    private String idOfComplexGroupItem;
    private String idOfDietType;
    private String idOfAgeGroupItem;
    private BigDecimal price;
    private String isPortal;
    private String date;
    private String complexName;
    private String cycle;

    public ComplexItem(String idOfComplex, String idOfComplexGroupItem, String idOfDietType, String idOfAgeGroupItem,
            BigDecimal price, String isPortal, String date, String complexName, String cycle) {
        this.idOfComplex = idOfComplex;
        this.idOfComplexGroupItem = idOfComplexGroupItem;
        this.idOfDietType = idOfDietType;
        this.idOfAgeGroupItem = idOfAgeGroupItem;
        this.price = price;
        this.isPortal = isPortal;
        this.date = date;
        this.complexName = complexName;
        this.cycle = cycle;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIdOfComplex() {
        return idOfComplex;
    }

    public void setIdOfComplex(String idOfComplex) {
        this.idOfComplex = idOfComplex;
    }

    public String getIdOfComplexGroupItem() {
        return idOfComplexGroupItem;
    }

    public void setIdOfComplexGroupItem(String idOfComplexGroupItem) {
        this.idOfComplexGroupItem = idOfComplexGroupItem;
    }

    public String getIdOfDietType() {
        return idOfDietType;
    }

    public void setIdOfDietType(String idOfDietType) {
        this.idOfDietType = idOfDietType;
    }

    public String getIdOfAgeGroupItem() {
        return idOfAgeGroupItem;
    }

    public void setIdOfAgeGroupItem(String idOfAgeGroupItem) {
        this.idOfAgeGroupItem = idOfAgeGroupItem;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getIsPortal() {
        return isPortal;
    }

    public void setIsPortal(String isPortal) {
        this.isPortal = isPortal;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }
}
