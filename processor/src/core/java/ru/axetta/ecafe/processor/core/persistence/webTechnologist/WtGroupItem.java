/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class WtGroupItem {

    private Long idOfGroupItem;
    private String description;
    private Set<WtDish> dishes = new HashSet<>();

    public Long getIdOfGroupItem() {
        return idOfGroupItem;
    }

    public void setIdOfGroupItem(Long idOfGroupItem) {
        this.idOfGroupItem = idOfGroupItem;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        WtGroupItem that = (WtGroupItem) o;
        return Objects.equals(idOfGroupItem, that.idOfGroupItem) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfGroupItem, description);
    }
}
