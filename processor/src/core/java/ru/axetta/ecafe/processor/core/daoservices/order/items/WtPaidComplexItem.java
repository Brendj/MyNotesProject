/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.order.items;

import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtAgeGroupItem;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtDietType;

import java.util.Objects;

public class WtPaidComplexItem {

    private Long idOfComplex;
    private WtDietType dietType;
    private WtAgeGroupItem ageGroup;
    private Long price;

    public WtPaidComplexItem() {
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

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WtPaidComplexItem that = (WtPaidComplexItem) o;
        return idOfComplex.equals(that.idOfComplex) && dietType.equals(that.dietType) && ageGroup.equals(that.ageGroup)
                && Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfComplex, dietType, ageGroup, price);
    }
}
