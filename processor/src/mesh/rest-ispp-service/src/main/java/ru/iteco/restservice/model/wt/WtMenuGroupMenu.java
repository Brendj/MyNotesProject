/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model.wt;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cf_wt_menu_group_relationships")
public class WtMenuGroupMenu {
    @Id
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idOfMenu")
    private WtMenu menu;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idOfMenuGroup")
    private WtMenuGroup menuGroup;

    @ManyToMany
    @JoinTable(name = "cf_wt_menu_group_dish_relationships",
            joinColumns = @JoinColumn(name = "idOfMenuMenuGroupRelation"),
            inverseJoinColumns = @JoinColumn(name = "idOfDish"))
    private Set<WtDish> dishes = new HashSet<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WtMenu getMenu() {
        return menu;
    }

    public void setMenu(WtMenu menu) {
        this.menu = menu;
    }

    public WtMenuGroup getMenuGroup() {
        return menuGroup;
    }

    public void setMenuGroup(WtMenuGroup menuGroup) {
        this.menuGroup = menuGroup;
    }

    public Set<WtDish> getDishes() {
        return dishes;
    }

    public void setDishes(Set<WtDish> dishes) {
        this.dishes = dishes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WtMenuGroupMenu that = (WtMenuGroupMenu) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "WtMenuGroupMenu{" + "id=" + id + ", menu=" + menu + ", menuGroup=" + menuGroup + '}';
    }
}




