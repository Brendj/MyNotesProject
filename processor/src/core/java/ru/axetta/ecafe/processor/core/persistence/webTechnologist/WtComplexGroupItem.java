/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import ru.axetta.ecafe.processor.core.persistence.MenuSupplier;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cf_wt_complex_group_items")
public class WtComplexGroupItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idOfComplexGroupItem")
    private Long idOfComplexGroupItem;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "complexGroupItem")
    private List<MenuSupplier> menuSupplierList;

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
        WtComplexGroupItem that = (WtComplexGroupItem) o;
        return Objects.equals(idOfComplexGroupItem, that.idOfComplexGroupItem) && Objects
                .equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfComplexGroupItem, description);
    }
}
