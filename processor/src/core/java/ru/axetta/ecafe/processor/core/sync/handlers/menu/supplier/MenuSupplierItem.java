/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menu.supplier;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.*;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import java.util.*;

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

    private static List<WtOrgGroup> orgGroups = new ArrayList<>();
    private static List<WtCategoryItem> categoryItems = new ArrayList<>();
    private static List<WtTypeOfProductionItem> typeProductions = new ArrayList<>();
    private static List<WtAgeGroupItem> ageGroupItems = new ArrayList<>();
    private static List<WtDietType> dietTypes = new ArrayList<>();
    private static List<WtComplexGroupItem> complexGroupItems = new ArrayList<>();
    private static List<WtGroupItem> groupItems = new ArrayList<>();
    private static List<WtDish> dishes = new ArrayList<>();
    private static List<WtMenuGroup> menuGroups = new ArrayList<>();
    private static List<WtMenu> menus = new ArrayList<>();
    private static List<WtComplex> complexes = new ArrayList<>();

    private Integer resCode;
    private String errorMessage;

    public static MenuSupplierItem build(Node itemNode) {

        StringBuilder errorMessage = new StringBuilder();

        String strVersion = XMLUtils.getAttributeValue(itemNode, "V");
        if (StringUtils.isNotEmpty(strVersion)) {
            try {
                versions.put(itemNode.getNodeName(), Long.parseLong(strVersion));
            } catch (NumberFormatException e) {
                errorMessage.append("NumberFormatException Version for node ").append(itemNode.getNodeName())
                        .append(" not found\n");
            }
        } else {
            errorMessage.append("Attribute Version fir node ").append(itemNode.getNodeName()).append(" not found\n");
        }

        return new MenuSupplierItem(versions, errorMessage.toString());
    }

    private MenuSupplierItem(Map<String, Long> versions, String errorMessage) {

        Iterator<Map.Entry<String, Long>> iter = versions.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry<String, Long> entry = iter.next();

            switch(entry.getKey()) {
                case "OrgGroupsRequest" : {
                    orgGroups = DAOService.getInstance().getOrgGroupsListFromVersion(entry.getValue());
                    break;
                }
                case "CategoryItemsRequest" : {
                    categoryItems = DAOService.getInstance().getCategoryItemsListFromVersion(entry.getValue());
                    break;
                }
                case "TypeProductionsRequest" : {
                    typeProductions = DAOService.getInstance().getTypeProductionsListFromVersion(entry.getValue());
                    break;
                }
                case "AgeGroupItemsRequest" : {
                    ageGroupItems = DAOService.getInstance().getAgeGroupItemsListFromVersion(entry.getValue());
                    break;
                }
                case "DietTypesRequest" : {
                    dietTypes = DAOService.getInstance().getDietTypesListFromVersion(entry.getValue());
                    break;
                }
                case "ComplexGroupItemsRequest" : {
                    complexGroupItems = DAOService.getInstance().getComplexGroupItemsListFromVersion(entry.getValue());
                    break;
                }
                case "GroupItemsRequest" : {
                    groupItems = DAOService.getInstance().getGroupItemsListFromVersion(entry.getValue());
                    break;
                }
                case "DishesRequest" : {
                    dishes = DAOService.getInstance().getDishesListFromVersion(entry.getValue());
                    break;
                }
                case "MenuGroupsRequest" : {
                    menuGroups = DAOService.getInstance().getMenuGroupsListFromVersion(entry.getValue());
                    break;
                }
                case "MenusRequest" : {
                    menus = DAOService.getInstance().getMenusListFromVersion(entry.getValue());
                    break;
                }
                case "ComplexesRequest" : {
                    complexes = DAOService.getInstance().getComplexesListFromVersion(entry.getValue());
                    break;
                }
            }
        }

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