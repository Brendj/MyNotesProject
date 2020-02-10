/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menu.supplier;

import ru.axetta.ecafe.processor.core.persistence.MenuSupplier;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.*;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: o.petrova
 * Date: 03.02.2020
 * Time: 11:59
 * To change this template use File | Settings | File Templates.
 */

public class ResMenuSupplierItem {

    private Long idOfMenuSupplier;

    WtOrgGroup orgGroup;
    WtCategoryItem categoryItem;
    WtTypeOfProductionItem typeOfProduction;
    WtAgeGroupItem ageGroupItem;
    WtDietType dietType;
    WtComplexGroupItem complexGroupItem;
    WtGroupItem groupItem;
    WtDish dish;
    WtMenuGroup menuGroup;
    WtMenu menu;
    WtComplex complex;

    private Integer resultCode;
    private String errorMessage;

    public ResMenuSupplierItem() {

    }

    public ResMenuSupplierItem(MenuSupplier menuSupplier) {

        this.orgGroup = menuSupplier.getOrgGroup();
        this.categoryItem = menuSupplier.getCategoryItem();
        this.typeOfProduction = menuSupplier.getTypeOfProduction();
        this.ageGroupItem = menuSupplier.getAgeGroupItem();
        this.dietType = menuSupplier.getDietType();
        this.complexGroupItem = menuSupplier.getComplexGroupItem();
        this.groupItem = menuSupplier.getGroupItem();
        this.dish = menuSupplier.getDish();
        this.menuGroup = menuSupplier.getMenuGroup();
        this.menu = menuSupplier.getMenu();
        this.complex = menuSupplier.getComplex();

    }

    public ResMenuSupplierItem(MenuSupplier menuSupplier, Integer resCode) {

        this.orgGroup = menuSupplier.getOrgGroup();
        this.categoryItem = menuSupplier.getCategoryItem();
        this.typeOfProduction = menuSupplier.getTypeOfProduction();
        this.ageGroupItem = menuSupplier.getAgeGroupItem();
        this.dietType = menuSupplier.getDietType();
        this.complexGroupItem = menuSupplier.getComplexGroupItem();
        this.groupItem = menuSupplier.getGroupItem();
        this.dish = menuSupplier.getDish();
        this.menuGroup = menuSupplier.getMenuGroup();
        this.menu = menuSupplier.getMenu();
        this.complex = menuSupplier.getComplex();

        this.resultCode = resCode;
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);

        //XMLUtils.setAttributeIfNotNull(element, "D", deletedState);
        XMLUtils.setAttributeIfNotNull(element, "Res", resultCode);

        if (resultCode != null && resultCode != 0) {
            XMLUtils.setAttributeIfNotNull(element, "Error", errorMessage);
        }
        return element;
    }

    public Long getIdOfMenuSupplier() {
        return idOfMenuSupplier;
    }

    public void setIdOfMenuSupplier(Long idOfMenuSupplier) {
        this.idOfMenuSupplier = idOfMenuSupplier;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
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
