/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import java.util.Objects;

public class WtComplexGroupItem {

    private Long idOfComplexGroupItem;
    private String description;

    public Long getIdOfComplexGroupItem() {
        return idOfComplexGroupItem;
    }

    public void setIdOfComplexGroupItem(Long idOfComplexGroupItem) {
        this.idOfComplexGroupItem = idOfComplexGroupItem;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WtComplexGroupItem that = (WtComplexGroupItem) o;
        return Objects.equals(idOfComplexGroupItem, that.idOfComplexGroupItem) && Objects
                .equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfComplexGroupItem, description);
    }
}
