/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.persistence.webTechnologist.*;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 * User: o.petrova
 * Date: 07.02.20
 * Time: 10:24
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "cf_menu_suppliers")
public class MenuSupplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idOfMenuSupplier")
    private Long idOfMenuSupplier;

    @OneToMany(mappedBy = "idOfCategoryItem")
    private List<WtCategoryItem> categoryItems;

    @OneToMany(mappedBy = "idOfTypeProduction")
    private List<WtTypeOfProductionItem> typeOfProductions;

    @OneToMany(mappedBy = "idOfAgeGroupItem")
    private List<WtAgeGroupItem> ageGroupItems;

    @OneToMany(mappedBy = "idOfDietType")
    private List<WtDietType> dietTypes;

    @OneToMany(mappedBy = "idOfComplexGroupItem")
    private List<WtComplexGroupItem> complexGroupItems;

    @OneToMany(mappedBy = "idOfGroupItem")
    private List<WtGroupItem> groupItems;

    @OneToMany(mappedBy = "idOfDish")
    private List<WtDish> dishes;

    @OneToMany(mappedBy = "idOfMenuGroup")
    private List<WtMenuGroup> menuGroups;

    @OneToMany(mappedBy = "idOfMenu")
    private List<WtMenu> menus;

    @OneToMany(mappedBy = "idOfComplex")
    private List<WtComplex> complexes;

    public MenuSupplier() {
    }

    public MenuSupplier(Long idOfMenuSupplier, List<WtCategoryItem> categoryItems,
            List<WtTypeOfProductionItem> typeOfProductions, List<WtAgeGroupItem> ageGroupItems,
            List<WtDietType> dietTypes, List<WtComplexGroupItem> complexGroupItems, List<WtGroupItem> groupItems,
            List<WtDish> dishes, List<WtMenuGroup> menuGroups, List<WtMenu> menus, List<WtComplex> complexes) {
        this.idOfMenuSupplier = idOfMenuSupplier;
        this.categoryItems = categoryItems;
        this.typeOfProductions = typeOfProductions;
        this.ageGroupItems = ageGroupItems;
        this.dietTypes = dietTypes;
        this.complexGroupItems = complexGroupItems;
        this.groupItems = groupItems;
        this.dishes = dishes;
        this.menuGroups = menuGroups;
        this.menus = menus;
        this.complexes = complexes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MenuSupplier that = (MenuSupplier) o;
        return idOfMenuSupplier.equals(that.idOfMenuSupplier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfMenuSupplier);
    }

    public Long getIdOfMenuSupplier() {
        return idOfMenuSupplier;
    }

    @Override
    public String toString() {
        return "MenuSupplier{" + "idOfMenuSupplier=" + idOfMenuSupplier + ", categoryItems=" + categoryItems
                + ", typeOfProductions=" + typeOfProductions + ", ageGroupItems=" + ageGroupItems + ", dietTypes="
                + dietTypes + ", complexGroupItems=" + complexGroupItems + ", groupItems=" + groupItems + ", dishes="
                + dishes + ", menuGroups=" + menuGroups + ", menus=" + menus + ", complexes=" + complexes + '}';
    }

    public void setIdOfMenuSupplier(Long idOfMenuSupplier) {
        this.idOfMenuSupplier = idOfMenuSupplier;
    }

    public List<WtCategoryItem> getCategoryItems() {
        return categoryItems;
    }

    public void setCategoryItems(List<WtCategoryItem> categoryItems) {
        this.categoryItems = categoryItems;
    }

    public List<WtTypeOfProductionItem> getTypeOfProductions() {
        return typeOfProductions;
    }

    public void setTypeOfProductions(List<WtTypeOfProductionItem> typeOfProductions) {
        this.typeOfProductions = typeOfProductions;
    }

    public List<WtDietType> getDietTypes() {
        return dietTypes;
    }

    public void setDietTypes(List<WtDietType> dietTypes) {
        this.dietTypes = dietTypes;
    }

    public List<WtComplexGroupItem> getComplexGroupItems() {
        return complexGroupItems;
    }

    public void setComplexGroupItems(List<WtComplexGroupItem> complexGroupItems) {
        this.complexGroupItems = complexGroupItems;
    }

    public List<WtGroupItem> getGroupItems() {
        return groupItems;
    }

    public void setGroupItems(List<WtGroupItem> groupItems) {
        this.groupItems = groupItems;
    }

    public List<WtDish> getDishes() {
        return dishes;
    }

    public void setDishes(List<WtDish> dishes) {
        this.dishes = dishes;
    }

    public List<WtMenuGroup> getMenuGroups() {
        return menuGroups;
    }

    public void setMenuGroups(List<WtMenuGroup> menuGroups) {
        this.menuGroups = menuGroups;
    }

    public List<WtMenu> getMenus() {
        return menus;
    }

    public void setMenus(List<WtMenu> menus) {
        this.menus = menus;
    }

    public List<WtComplex> getComplexes() {
        return complexes;
    }

    public void setComplexes(List<WtComplex> complexes) {
        this.complexes = complexes;
    }

    public void setAgeGroupItems(List<WtAgeGroupItem> ageGroupItems) {
        this.ageGroupItems = ageGroupItems;
    }

    public List<WtAgeGroupItem> getAgeGroupItems() {
        return ageGroupItems;
    }
}
