/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cf_wt_diet_type")
public class WtDietType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idOfDietType")
    private Long idOfDietType;

    @Column(name = "description")
    private String description;

    @Column(name = "version")
    private Long version;

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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
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
        return Objects.equals(idOfDietType, that.idOfDietType) && Objects.equals(description, that.description)
                && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfDietType, description, version);
    }
}
