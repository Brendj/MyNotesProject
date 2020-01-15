/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class WtComplexesItem {

    private Long idOfComplexItem;
    private Integer cycleDay;
    private Integer countDishes;
    private WtComplex wtComplex;
    private Set<WtDish> dishes = new HashSet<>();

    public Long getIdOfComplexItem() {
        return idOfComplexItem;
    }

    public void setIdOfComplexItem(Long idOfComplexItem) {
        this.idOfComplexItem = idOfComplexItem;
    }

    public Integer getCycleDay() {
        return cycleDay;
    }

    public void setCycleDay(Integer cycleDay) {
        this.cycleDay = cycleDay;
    }

    public Integer getCountDishes() {
        return countDishes;
    }

    public void setCountDishes(Integer countDishes) {
        this.countDishes = countDishes;
    }

    public WtComplex getWtComplex() {
        return wtComplex;
    }

    public void setWtComplex(WtComplex wtComplex) {
        this.wtComplex = wtComplex;
    }

    public Set<WtDish> getDishes() {
        return dishes;
    }

    public void setDishes(Set<WtDish> dishes) {
        this.dishes = dishes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WtComplexesItem that = (WtComplexesItem) o;
        return Objects.equals(idOfComplexItem, that.idOfComplexItem) && Objects.equals(cycleDay, that.cycleDay)
                && Objects.equals(countDishes, that.countDishes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfComplexItem, cycleDay, countDishes);
    }
}
