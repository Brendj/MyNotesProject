/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model.wt;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cf_wt_diet_type")
public class WtDietType {

    @Id
    @Column(name = "idOfDietType")
    private Long idOfDietType;

    @Column(name = "description")
    private String description;

    @Column(name = "version")
    private Long version;

    @OneToMany(mappedBy = "wtDietType")
    private Set<WtComplex> wtComplexes = new HashSet<>();

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

    public Set<WtComplex> getWtComplexes() {
        return wtComplexes;
    }

    public void setWtComplexes(Set<WtComplex> wtComplexes) {
        this.wtComplexes = wtComplexes;
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
