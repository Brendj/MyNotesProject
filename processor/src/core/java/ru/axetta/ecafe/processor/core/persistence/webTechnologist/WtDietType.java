/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import java.util.Objects;

public class WtDietType {

    private Long idOfDietType;
    private String description;

    public Long getIdOfDietType() {
        return idOfDietType;
    }

    public void setIdOfDietType(Long idOfDietType) {
        this.idOfDietType = idOfDietType;
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
        WtDietType that = (WtDietType) o;
        return Objects.equals(idOfDietType, that.idOfDietType) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfDietType, description);
    }
}
