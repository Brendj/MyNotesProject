/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import ru.axetta.ecafe.processor.core.persistence.MenuSupplier;

import javax.persistence.*;
import java.util.List;
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

    @OneToMany(mappedBy = "dietType")
    private List<MenuSupplier> menuSupplierList;

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
        WtDietType that = (WtDietType) o;
        return Objects.equals(idOfDietType, that.idOfDietType) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfDietType, description);
    }
}
