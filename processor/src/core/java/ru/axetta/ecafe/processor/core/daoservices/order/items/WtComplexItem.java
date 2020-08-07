/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.order.items;

import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtAgeGroupItem;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtDietType;

import java.util.Objects;

public class WtComplexItem {

    private Long idOfComplex;
    private WtDietType dietType;
    private WtAgeGroupItem ageGroup;
    private Integer orderType;

    public WtComplexItem() {
    }

    public Long getIdOfComplex() {
        return idOfComplex;
    }

    public void setIdOfComplex(Long idOfComplex) {
        this.idOfComplex = idOfComplex;
    }

    public WtDietType getDietType() {
        return dietType;
    }

    public void setDietType(WtDietType dietType) {
        this.dietType = dietType;
    }

    public WtAgeGroupItem getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(WtAgeGroupItem ageGroup) {
        this.ageGroup = ageGroup;
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
        return idOfComplex.equals(that.idOfComplex) && dietType.equals(that.dietType) && ageGroup.equals(that.ageGroup)
                && Objects.equals(orderType, that.orderType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfComplex, dietType, ageGroup, orderType);
    }
}
