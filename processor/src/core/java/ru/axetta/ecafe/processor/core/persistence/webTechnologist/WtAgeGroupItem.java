/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import ru.axetta.ecafe.processor.core.persistence.MenuSupplier;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cf_wt_agegroup_items")
public class WtAgeGroupItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idOfAgeGroupItem")
    private Long idOfAgeGroupItem;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "ageGroupItem")
    private List<MenuSupplier> menuSupplierList;

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

    public List<MenuSupplier> getMenuSupplierList() {
        return menuSupplierList;
    }

    public void setMenuSupplierList(List<MenuSupplier> menuSupplierList) {
        this.menuSupplierList = menuSupplierList;
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
