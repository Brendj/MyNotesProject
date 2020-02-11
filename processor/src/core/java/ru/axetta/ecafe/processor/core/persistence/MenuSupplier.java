/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.persistence.webTechnologist.*;

import javax.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "idOfOrgGroup")
    private WtOrgGroup orgGroup;

    @ManyToOne
    @JoinColumn(name = "idOfCategoryItem")
    private WtCategoryItem categoryItem;

    @ManyToOne
    @JoinColumn(name = "idOfTypeProduction")
    private WtTypeOfProductionItem typeOfProduction;

    @ManyToOne
    @JoinColumn(name = "idOfAgeGroupItem")
    private WtAgeGroupItem ageGroupItem;

    @ManyToOne
    @JoinColumn(name = "idOfDietType")
    private WtDietType dietType;

    @ManyToOne
    @JoinColumn(name = "idOfComplexGroupItem")
    private WtComplexGroupItem complexGroupItem;

    @ManyToOne
    @JoinColumn(name = "idOfGroupItem")
    private WtGroupItem groupItem;

    @ManyToOne
    @JoinColumn(name = "idOfDish")
    private WtDish dish;

    @ManyToOne
    @JoinColumn(name = "idOfMenuGroup")
    private WtMenuGroup menuGroup;

    @ManyToOne
    @JoinColumn(name = "idOfMenu")
    private WtMenu menu;

    @ManyToOne
    @JoinColumn(name = "idOfComplex")
    private WtComplex complex;

    public MenuSupplier() {
    }

    public MenuSupplier(WtOrgGroup orgGroup, WtCategoryItem categoryItem,
            WtTypeOfProductionItem typeOfProduction, WtAgeGroupItem ageGroupItem,
            WtDietType dietType, WtComplexGroupItem complexGroupItem, WtGroupItem groupItem,
            WtDish dish, WtMenuGroup menuGroup, WtMenu menu, WtComplex complex) {
        this.orgGroup = orgGroup;
        this.categoryItem = categoryItem;
        this.typeOfProduction = typeOfProduction;
        this.ageGroupItem = ageGroupItem;
        this.dietType = dietType;
        this.complexGroupItem = complexGroupItem;
        this.groupItem = groupItem;
        this.dish = dish;
        this.menuGroup = menuGroup;
        this.menu = menu;
        this.complex = complex;
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

    @Override
    public String toString() {
        return "MenuSupplier{" + "idOfMenuSupplier=" + idOfMenuSupplier + ", orgGroup=" + orgGroup + ", categoryItem="
                + categoryItem + ", typeOfProduction=" + typeOfProduction + ", ageGroupItem=" + ageGroupItem
                + ", dietType=" + dietType + ", complexGroupItem=" + complexGroupItem + ", groupItem=" + groupItem
                + ", dish=" + dish + ", menuGroup=" + menuGroup + ", menu=" + menu + ", complex=" + complex + '}';
    }

    public Long getIdOfMenuSupplier() {
        return idOfMenuSupplier;
    }

    public void setIdOfMenuSupplier(Long idOfMenuSupplier) {
        this.idOfMenuSupplier = idOfMenuSupplier;
    }

    public WtOrgGroup getOrgGroup() {
        return orgGroup;
    }

    public void setOrgGroup(WtOrgGroup orgGroup) {
        this.orgGroup = orgGroup;
    }

    public WtCategoryItem getCategoryItem() {
        return categoryItem;
    }

    public void setCategoryItem(WtCategoryItem categoryItem) {
        this.categoryItem = categoryItem;
    }

    public WtTypeOfProductionItem getTypeOfProduction() {
        return typeOfProduction;
    }

    public void setTypeOfProduction(WtTypeOfProductionItem typeOfProduction) {
        this.typeOfProduction = typeOfProduction;
    }

    public WtAgeGroupItem getAgeGroupItem() {
        return ageGroupItem;
    }

    public void setAgeGroupItem(WtAgeGroupItem ageGroupItem) {
        this.ageGroupItem = ageGroupItem;
    }

    public WtDietType getDietType() {
        return dietType;
    }

    public void setDietType(WtDietType dietType) {
        this.dietType = dietType;
    }

    public WtComplexGroupItem getComplexGroupItem() {
        return complexGroupItem;
    }

    public void setComplexGroupItem(WtComplexGroupItem complexGroupItem) {
        this.complexGroupItem = complexGroupItem;
    }

    public WtGroupItem getGroupItem() {
        return groupItem;
    }

    public void setGroupItem(WtGroupItem groupItem) {
        this.groupItem = groupItem;
    }

    public WtDish getDish() {
        return dish;
    }

    public void setDish(WtDish dish) {
        this.dish = dish;
    }

    public WtMenuGroup getMenuGroup() {
        return menuGroup;
    }

    public void setMenuGroup(WtMenuGroup menuGroup) {
        this.menuGroup = menuGroup;
    }

    public WtMenu getMenu() {
        return menu;
    }

    public void setMenu(WtMenu menu) {
        this.menu = menu;
    }

    public WtComplex getComplex() {
        return complex;
    }

    public void setComplex(WtComplex complex) {
        this.complex = complex;
    }
}
