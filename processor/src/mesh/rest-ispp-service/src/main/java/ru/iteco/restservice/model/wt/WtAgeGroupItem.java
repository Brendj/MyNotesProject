/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model.wt;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cf_wt_agegroup_items")
public class WtAgeGroupItem {

    @Id
    @Column(name = "idOfAgeGroupItem")
    private Long idOfAgeGroupItem;

    @Column(name = "description")
    private String description;

    @Column(name = "version")
    private Long version;

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
        WtAgeGroupItem that = (WtAgeGroupItem) o;
        return Objects.equals(idOfAgeGroupItem, that.idOfAgeGroupItem) && Objects.equals(description, that.description)
                && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfAgeGroupItem, description, version);
    }
}
