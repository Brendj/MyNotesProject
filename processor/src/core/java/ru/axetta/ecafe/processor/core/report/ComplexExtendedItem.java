/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import java.util.List;

public class ComplexExtendedItem {
    String contragent;
    String complexName;
    String dietType;
    String ageGroupItem;
    String complexType;
    String isPortal;
    String barCode;
    String complexDate;
    String dayInCycle;
    String cycleMotion;
    String startDay;
    String passDay;
    String note;
    String day;
    List<ComplexExtendedDishItem> dish;

    public ComplexExtendedItem(String contragent, String complexName, String dietType, String ageGroupItem,
            String complexType, String isPortal, String barCode, String complexDate, String dayInCycle,
            String cycleMotion, String startDay, String passDay, String note, String day, List<ComplexExtendedDishItem> dish) {
        this.contragent = contragent;
        this.complexName = complexName;
        this.dietType = dietType;
        this.ageGroupItem = ageGroupItem;
        this.complexType = complexType;
        this.isPortal = isPortal;
        this.barCode = barCode;
        this.complexDate = complexDate;
        this.dayInCycle = dayInCycle;
        this.cycleMotion = cycleMotion;
        this.startDay = startDay;
        this.passDay = passDay;
        this.note = note;
        this.day = day;
        this.dish = dish;
    }

    public String getContragent() {
        return contragent;
    }

    public void setContragent(String contragent) {
        this.contragent = contragent;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
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

    public String getComplexType() {
        return complexType;
    }

    public void setComplexType(String complexType) {
        this.complexType = complexType;
    }

    public String getIsPortal() {
        return isPortal;
    }

    public void setIsPortal(String isPortal) {
        this.isPortal = isPortal;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getComplexDate() {
        return complexDate;
    }

    public void setComplexDate(String complexDate) {
        this.complexDate = complexDate;
    }

    public String getDayInCycle() {
        return dayInCycle;
    }

    public void setDayInCycle(String dayInCycle) {
        this.dayInCycle = dayInCycle;
    }

    public String getCycleMotion() {
        return cycleMotion;
    }

    public void setCycleMotion(String cycleMotion) {
        this.cycleMotion = cycleMotion;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public String getPassDay() {
        return passDay;
    }

    public void setPassDay(String passDay) {
        this.passDay = passDay;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public List<ComplexExtendedDishItem> getDish() {
        return dish;
    }

    public void setDish(List<ComplexExtendedDishItem> dish) {
        this.dish = dish;
    }
}
