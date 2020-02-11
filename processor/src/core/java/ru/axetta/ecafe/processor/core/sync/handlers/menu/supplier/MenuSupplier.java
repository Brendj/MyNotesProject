/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menu.supplier;

import ru.axetta.ecafe.processor.core.persistence.webTechnologist.*;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: o.petrova
 * Date: 03.02.2020
 * Time: 11:59
 * To change this template use File | Settings | File Templates.
 */

public class MenuSupplier {

    private List<WtOrgGroup> orgGroups;
    private List<WtCategoryItem> categoryItems;
    private List<WtTypeOfProductionItem> typeProductions;
    private List<WtAgeGroupItem> ageGroupItems;
    private List<WtDietType> dietTypes;
    private List<WtComplexGroupItem> complexGroupItems;
    private List<WtGroupItem> groupItems;
    private List<WtDish> dishes;
    private List<WtMenuGroup> menuGroups;
    private List<WtMenu> menus;
    private List<WtComplex> complexes;

    private Integer resultCode;
    private String errorMessage;

    public MenuSupplier() {

    }

    //public ResMenuSupplierItem(MenuSupplier menuSupplier) {
    //
    //    this.orgGroup = menuSupplier.getOrgGroup();
    //    this.categoryItem = menuSupplier.getCategoryItem();
    //    this.typeOfProduction = menuSupplier.getTypeOfProduction();
    //    this.ageGroupItem = menuSupplier.getAgeGroupItem();
    //    this.dietType = menuSupplier.getDietType();
    //    this.complexGroupItem = menuSupplier.getComplexGroupItem();
    //    this.groupItem = menuSupplier.getGroupItem();
    //    this.dish = menuSupplier.getDish();
    //    this.menuGroup = menuSupplier.getMenuGroup();
    //    this.menu = menuSupplier.getMenu();
    //    this.complex = menuSupplier.getComplex();
    //
    //}

    //public ResMenuSupplierItem(MenuSupplier menuSupplier, Integer resCode) {
    //
    //    this.orgGroup = menuSupplier.getOrgGroup();
    //    this.categoryItem = menuSupplier.getCategoryItem();
    //    this.typeOfProduction = menuSupplier.getTypeOfProduction();
    //    this.ageGroupItem = menuSupplier.getAgeGroupItem();
    //    this.dietType = menuSupplier.getDietType();
    //    this.complexGroupItem = menuSupplier.getComplexGroupItem();
    //    this.groupItem = menuSupplier.getGroupItem();
    //    this.dish = menuSupplier.getDish();
    //    this.menuGroup = menuSupplier.getMenuGroup();
    //    this.menu = menuSupplier.getMenu();
    //    this.complex = menuSupplier.getComplex();
    //
    //    this.resultCode = resCode;
    //}

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);

        //XMLUtils.setAttributeIfNotNull(element, "D", deletedState);
        XMLUtils.setAttributeIfNotNull(element, "Res", resultCode);

        if (resultCode != null && resultCode != 0) {
            XMLUtils.setAttributeIfNotNull(element, "Error", errorMessage);
        }
        return element;
    }

    public List<WtOrgGroup> getOrgGroups() {
        return orgGroups;
    }

    public void setOrgGroups(List<WtOrgGroup> orgGroups) {
        this.orgGroups = orgGroups;
    }

    public List<WtCategoryItem> getCategoryItems() {
        return categoryItems;
    }

    public void setCategoryItems(List<WtCategoryItem> categoryItems) {
        this.categoryItems = categoryItems;
    }

    public List<WtTypeOfProductionItem> getTypeProductions() {
        return typeProductions;
    }

    public void setTypeProductions(List<WtTypeOfProductionItem> typeProductions) {
        this.typeProductions = typeProductions;
    }

    public List<WtAgeGroupItem> getAgeGroupItems() {
        return ageGroupItems;
    }

    public void setAgeGroupItems(List<WtAgeGroupItem> ageGroupItems) {
        this.ageGroupItems = ageGroupItems;
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

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
