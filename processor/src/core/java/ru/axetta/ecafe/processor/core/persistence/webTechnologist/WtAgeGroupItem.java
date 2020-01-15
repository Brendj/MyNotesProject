/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import java.util.Objects;

public class WtAgeGroupItem {

    private Long idOfAgeGroupItem;
    private String description;

    public Long getIdOfAgeGroupItem() {
        return idOfAgeGroupItem;
    }

    public void setIdOfAgeGroupItem(Long idOfAgeGroupItem) {
        this.idOfAgeGroupItem = idOfAgeGroupItem;
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
        WtAgeGroupItem that = (WtAgeGroupItem) o;
        return Objects.equals(idOfAgeGroupItem, that.idOfAgeGroupItem) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfAgeGroupItem, description);
    }
}
