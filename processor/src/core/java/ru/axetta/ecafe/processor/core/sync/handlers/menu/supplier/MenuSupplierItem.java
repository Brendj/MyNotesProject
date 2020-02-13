/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menu.supplier;

import ru.axetta.ecafe.processor.core.persistence.webTechnologist.*;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static Map<String, Long> versions = new HashMap<>();

    private static List<WtOrgGroup> orgGroups;
    private static List<WtCategoryItem> categoryItems;
    private static List<WtTypeOfProductionItem> typeProductions;
    private static List<WtAgeGroupItem> ageGroupItems;
    private static List<WtDietType> dietTypes;
    private static List<WtComplexGroupItem> complexGroupItems;
    private static List<WtGroupItem> groupItems;
    private static List<WtDish> dishes;
    private static List<WtMenuGroup> menuGroups;
    private static List<WtMenu> menus;
    private static List<WtComplex> complexes;

    private Integer resCode;
    private String errorMessage;

    public static MenuSupplierItem build(Node itemNode) {

        versions = new HashMap<>();

        orgGroups = new ArrayList<>();
        categoryItems = new ArrayList<>();
        typeProductions = new ArrayList<>();
        ageGroupItems = new ArrayList<>();
        complexGroupItems = new ArrayList<>();
        groupItems = new ArrayList<>();
        dishes = new ArrayList<>();
        menuGroups = new ArrayList<>();
        menus = new ArrayList<>();

        StringBuilder errorMessage = new StringBuilder();

        String strVersion = XMLUtils.getAttributeValue(itemNode, "V");
        if (StringUtils.isNotEmpty(strVersion)) {
            try {
                versions.put(itemNode.getNodeName(), Long.parseLong(strVersion));
            } catch (NumberFormatException e) {
                errorMessage.append("NumberFormatException Version not found");
            }
        } else {
            errorMessage.append("Attribute Version not found");
        }

        return new MenuSupplierItem(versions, errorMessage.toString());
    }

    private MenuSupplierItem(Map<String, Long> versions, String errorMessage) {

        // TO DO!

        this.setErrorMessage(errorMessage);
        if (errorMessage.equals("")) {
            this.setResCode(ERROR_CODE_ALL_OK);
        } else {
            this.setResCode(ERROR_CODE_NOT_VALID_ATTRIBUTE);
        }
    }

    public static Map<String, Long> getVersions() {
        return versions;
    }

    public static void setVersions(Map<String, Long> versions) {
        MenuSupplierItem.versions = versions;
    }

    public static List<WtOrgGroup> getOrgGroups() {
        return orgGroups;
    }

    public static void setOrgGroups(List<WtOrgGroup> orgGroups) {
        MenuSupplierItem.orgGroups = orgGroups;
    }

    public static List<WtCategoryItem> getCategoryItems() {
        return categoryItems;
    }

    public static void setCategoryItems(List<WtCategoryItem> categoryItems) {
        MenuSupplierItem.categoryItems = categoryItems;
    }

    public static List<WtTypeOfProductionItem> getTypeProductions() {
        return typeProductions;
    }

    public static void setTypeProductions(List<WtTypeOfProductionItem> typeProductions) {
        MenuSupplierItem.typeProductions = typeProductions;
    }

    public static List<WtAgeGroupItem> getAgeGroupItems() {
        return ageGroupItems;
    }

    public static void setAgeGroupItems(List<WtAgeGroupItem> ageGroupItems) {
        MenuSupplierItem.ageGroupItems = ageGroupItems;
    }

    public static List<WtDietType> getDietTypes() {
        return dietTypes;
    }

    public static void setDietTypes(List<WtDietType> dietTypes) {
        MenuSupplierItem.dietTypes = dietTypes;
    }

    public static List<WtComplexGroupItem> getComplexGroupItems() {
        return complexGroupItems;
    }

    public static void setComplexGroupItems(List<WtComplexGroupItem> complexGroupItems) {
        MenuSupplierItem.complexGroupItems = complexGroupItems;
    }

    public static List<WtGroupItem> getGroupItems() {
        return groupItems;
    }

    public static void setGroupItems(List<WtGroupItem> groupItems) {
        MenuSupplierItem.groupItems = groupItems;
    }

    public static List<WtDish> getDishes() {
        return dishes;
    }

    public static void setDishes(List<WtDish> dishes) {
        MenuSupplierItem.dishes = dishes;
    }

    public static List<WtMenuGroup> getMenuGroups() {
        return menuGroups;
    }

    public static void setMenuGroups(List<WtMenuGroup> menuGroups) {
        MenuSupplierItem.menuGroups = menuGroups;
    }

    public static List<WtMenu> getMenus() {
        return menus;
    }

    public static void setMenus(List<WtMenu> menus) {
        MenuSupplierItem.menus = menus;
    }

    public static List<WtComplex> getComplexes() {
        return complexes;
    }

    public static void setComplexes(List<WtComplex> complexes) {
        MenuSupplierItem.complexes = complexes;
    }

    public Integer getResCode() {
        return resCode;
    }

    public void setResCode(Integer resCode) {
        this.resCode = resCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}