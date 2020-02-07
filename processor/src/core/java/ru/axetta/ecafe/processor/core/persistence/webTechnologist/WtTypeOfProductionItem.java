/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import ru.axetta.ecafe.processor.core.persistence.MenuSupplier;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cf_wt_typeofproduction_items")
public class WtTypeOfProductionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idOfTypeProductionItem")
    private Long idOfTypeProductionItem;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "typeOfProduction")
    private List<MenuSupplier> menuSupplierList;

    public Long getIdOfTypeProductionItem() {
        return idOfTypeProductionItem;
    }

    public void setIdOfTypeProductionItem(Long idOfTypeProductionItem) {
        this.idOfTypeProductionItem = idOfTypeProductionItem;
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
        WtTypeOfProductionItem that = (WtTypeOfProductionItem) o;
        return Objects.equals(idOfTypeProductionItem, that.idOfTypeProductionItem) && Objects
                .equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfTypeProductionItem, description);
    }
}
