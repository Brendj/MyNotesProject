/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.order.items;

import java.util.Objects;

public class WtComplexItem {

    private Long idOfComplex;
    private Long idOfDietType;
    private Long idOfAgeGroup;
    private Integer orderType;

    public WtComplexItem() {
    }

    public Long getIdOfComplex() {
        return idOfComplex;
    }

    public void setIdOfComplex(Long idOfComplex) {
        this.idOfComplex = idOfComplex;
    }

    public Long getIdOfDietType() {
        return idOfDietType;
    }

    public void setIdOfDietType(Long idOfDietType) {
        this.idOfDietType = idOfDietType;
    }

    public Long getIdOfAgeGroup() {
        return idOfAgeGroup;
    }

    public void setIdOfAgeGroup(Long idOfAgeGroup) {
        this.idOfAgeGroup = idOfAgeGroup;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WtComplexItem that = (WtComplexItem) o;
        return idOfComplex.equals(that.idOfComplex) && idOfDietType.equals(that.idOfDietType) && idOfAgeGroup
                .equals(that.idOfAgeGroup) && Objects.equals(orderType, that.orderType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfComplex, idOfDietType, idOfAgeGroup, orderType);
    }
}
