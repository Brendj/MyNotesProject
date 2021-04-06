/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import java.math.BigDecimal;

public class ComplexItem {
    private String idOfComplex;
    private String complexGroupItem;
    private String dietType;
    private String ageGroupItem;
    private BigDecimal price;
    private String isPortal;
    private String startDate;
    private String endDate;
    private String complexName;
    private String cycle;

    public ComplexItem(String idOfComplex, String complexGroupItem, String dietType, String ageGroupItem,
            BigDecimal price, String isPortal, String startDate, String endDate, String complexName, String cycle) {
        this.idOfComplex = idOfComplex;
        this.complexGroupItem = complexGroupItem;
        this.dietType = dietType;
        this.ageGroupItem = ageGroupItem;
        this.price = price;
        this.isPortal = isPortal;
        this.startDate = startDate;
        this.endDate = endDate;
        this.complexName = complexName;
        this.cycle = cycle;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getIdOfComplex() {
        return idOfComplex;
    }

    public void setIdOfComplex(String idOfComplex) {
        this.idOfComplex = idOfComplex;
    }

    public String getComplexGroupItem() {
        return complexGroupItem;
    }

    public void setComplexGroupItem(String complexGroupItem) {
        this.complexGroupItem = complexGroupItem;
    }

    public String getDietType() {
        return dietType;
    }

    public void setDietType(String dietType) {
        this.dietType = dietType;
    }

    public String getAgeGroupItem() {
        return ageGroupItem;
    }

    public void setAgeGroupItem(String ageGroupItem) {
        this.ageGroupItem = ageGroupItem;
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
