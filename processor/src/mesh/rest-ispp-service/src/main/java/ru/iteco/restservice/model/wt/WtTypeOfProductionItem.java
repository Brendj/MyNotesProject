/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model.wt;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cf_wt_typeofproduction_items")
public class WtTypeOfProductionItem {

    @Id
    @Column(name = "idOfTypeProductionItem")
    private Long idOfTypeProductionItem;

    @Column(name = "description")
    private String description;

    @Column(name = "version")
    private Long version;

    @OneToMany(mappedBy = "wtTypeProductionItem")
    private Set<WtDish> wtDishes = new HashSet<>();

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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Set<WtDish> getWtDishes() {
        return wtDishes;
    }

    public void setWtDishes(Set<WtDish> wtDishes) {
        this.wtDishes = wtDishes;
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
        return idOfTypeProductionItem.equals(that.idOfTypeProductionItem) && Objects
                .equals(description, that.description) && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfTypeProductionItem, description, version);
    }
}
