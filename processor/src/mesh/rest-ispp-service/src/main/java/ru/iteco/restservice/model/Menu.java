/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cf_menu")
public class Menu {
    @Id
    @Column(name = "idofmenu")
    private Long idOfMenu;

    @Column(name = "menudate")
    private Long menuDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idoforg", insertable = false, updatable = false)
    private Org org;

    @OneToMany(mappedBy = "menu", fetch = FetchType.EAGER)
    private Set<MenuDetail> menuDetails;

    public Long getIdOfMenu() {
        return idOfMenu;
    }

    public void setIdOfMenu(Long idOfMenu) {
        this.idOfMenu = idOfMenu;
    }

    public Long getMenuDate() {
        return menuDate;
    }

    public void setMenuDate(Long menuDate) {
        this.menuDate = menuDate;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public Set<MenuDetail> getMenuDetails() {
        return menuDetails;
    }

    public void setMenuDetails(Set<MenuDetail> menuDetails) {
        this.menuDetails = menuDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Menu menu = (Menu) o;
        return Objects.equals(idOfMenu, menu.idOfMenu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfMenu);
    }
}
