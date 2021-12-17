/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model.wt;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cf_wt_complex_group_items")
public class WtComplexGroupItem {

    @Id
    @Column(name = "idOfComplexGroupItem")
    private Long idOfComplexGroupItem;

    @Column(name = "description")
    private String description;

    @Column(name = "version")
    private Long version;

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
        WtComplexGroupItem that = (WtComplexGroupItem) o;
        return Objects.equals(idOfComplexGroupItem, that.idOfComplexGroupItem) && Objects
                .equals(description, that.description) && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfComplexGroupItem, description, version);
    }
}
