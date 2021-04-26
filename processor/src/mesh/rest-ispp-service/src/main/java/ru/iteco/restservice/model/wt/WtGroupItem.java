/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model.wt;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cf_wt_group_items")
public class WtGroupItem {

    @Id
    @Column(name = "idOfGroupItem")
    private Long idOfGroupItem;

    @Column(name = "description")
    private String description;

    @Column(name = "version")
    private Long version;

    @ManyToMany
    @JoinTable(name = "cf_wt_dish_groupitem_relationships",
            joinColumns = @JoinColumn(name = "idOfGroupItem"),
            inverseJoinColumns = @JoinColumn(name = "idOfDish"))
    private Set<WtDish> dishes = new HashSet<>();

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
        WtGroupItem that = (WtGroupItem) o;
        return idOfGroupItem.equals(that.idOfGroupItem) && Objects.equals(description, that.description) && Objects
                .equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfGroupItem, description, version);
    }
}
