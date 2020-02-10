/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menu.supplier;

import ru.axetta.ecafe.processor.core.persistence.webTechnologist.*;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * Created with IntelliJ IDEA.
 * User: o.petrova
 * Date: 03.02.2020
 * Time: 11:45
 * To change this template use File | Settings | File Templates.
 */

public class MenuSupplierItem {

    public static final Integer ERROR_CODE_ALL_OK = 0;
    public static final Integer ERROR_CODE_NOT_VALID_ATTRIBUTE = 100;

    private Integer resCode;
    private String errorMessage;

    @OneToOne
    @JoinColumn(name = "idOfOrgGroup")
    private WtOrgGroup orgGroup;

    @OneToOne
    @JoinColumn(name = "idOfCategoryItem")
    private WtCategoryItem categoryItem;

    @OneToOne
    @JoinColumn(name = "idOfTypeProduction")
    private WtTypeOfProductionItem typeOfProduction;

    @OneToOne
    @JoinColumn(name = "idOfAgeGroupItem")
    private WtAgeGroupItem ageGroupItem;

    @OneToOne
    @JoinColumn(name = "idOfDietType")
    private WtDietType dietType;

    @OneToOne
    @JoinColumn(name = "idOfComplexGroupItem")
    private WtComplexGroupItem complexGroupItem;

    @OneToOne
    @JoinColumn(name = "idOfGroupItem")
    private WtGroupItem groupItem;

    @OneToOne
    @JoinColumn(name = "idOfDish")
    private WtDish dish;

    @OneToOne
    @JoinColumn(name = "idOfMenuGroup")
    private WtMenuGroup menuGroup;

    @OneToOne
    @JoinColumn(name = "idOfMenu")
    private WtMenu menu;

    @OneToOne
    @JoinColumn(name = "idOfComplex")
    private WtComplex complex;

    public static MenuSupplierItem build(Node itemNode) {

        WtOrgGroup orgGroup = null;
        WtCategoryItem categoryItem = null;
        WtTypeOfProductionItem typeOfProduction = null;
        WtAgeGroupItem ageGroupItem = null;
        WtDietType dietType = null;
        WtComplexGroupItem complexGroupItem = null;
        WtGroupItem groupItem = null;
        WtDish dish = null;
        WtMenuGroup menuGroup = null;
        WtMenu menu = null;
        WtComplex complex = null;

        StringBuilder errorMessage = new StringBuilder();

        return new MenuSupplierItem(errorMessage.toString());
    }

    private static Integer readIntegerValue(Node itemNode, String nameAttr, StringBuilder errorMessage) {
        String strValue = XMLUtils.getAttributeValue(itemNode, nameAttr);
        if (StringUtils.isNotEmpty(strValue)) {
            try {
                return Integer.parseInt(strValue);
            } catch (NumberFormatException e) {
                errorMessage.append(String.format("NumberFormatException incorrect format %s", nameAttr));
            }
        } else {
            errorMessage.append(String.format("Attribute %s not found", nameAttr));
        }
        return null;
    }

    private MenuSupplierItem(String errorMessage) {
        this.setErrorMessage(errorMessage);
        if (errorMessage.equals("")) {
            this.setResCode(ERROR_CODE_ALL_OK);
        } else {
            this.setResCode(ERROR_CODE_NOT_VALID_ATTRIBUTE);
        }
    }

    public Integer getResCode() {
        return resCode;
    }

    public void setResCode(Integer resCode) {
        this.resCode = resCode;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
