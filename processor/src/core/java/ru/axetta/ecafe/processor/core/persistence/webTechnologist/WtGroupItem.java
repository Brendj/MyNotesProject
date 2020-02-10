/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import ru.axetta.ecafe.processor.core.persistence.MenuSupplier;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cf_wt_group_items")
public class WtGroupItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idOfGroupItem")
    private Long idOfGroupItem;

    @Column(name = "description")
    private String description;

    @ManyToMany
    @JoinTable(name = "cf_wt_dish_groupitem_relationships",
            joinColumns = @JoinColumn(name = "idOfGroupItem"),
            inverseJoinColumns = @JoinColumn(name = "idOfDish"))
    private Set<WtDish> dishes = new HashSet<>();

    @OneToMany(mappedBy = "groupItem")
    private List<MenuSupplier> menuSupplierList;

    public Long getIdOfGroupItem() {
        return idOfGroupItem;
    }

    public void setIdOfGroupItem(Long idOfGroupItem) {
        this.idOfGroupItem = idOfGroupItem;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<WtDish> getDishes() {
        return dishes;
    }

    public void setDishes(Set<WtDish> dishes) {
        this.dishes = dishes;
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
        WtGroupItem that = (WtGroupItem) o;
        return Objects.equals(idOfGroupItem, that.idOfGroupItem) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfGroupItem, description);
    }
}
