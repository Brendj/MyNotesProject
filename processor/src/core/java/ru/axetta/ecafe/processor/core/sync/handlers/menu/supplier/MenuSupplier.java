/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menu.supplier;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.*;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: o.petrova
 * Date: 03.02.2020
 * Time: 11:59
 * To change this template use File | Settings | File Templates.
 */

public class MenuSupplier implements SectionRequest {

    protected static final String[] CLIENT_SECTION_NAMES = new String[]{
            "OrgGroupsRequest", "CategoryItemsRequest", "TypeProductionsRequest", "AgeGroupItemsRequest",
            "DietTypesRequest", "ComplexGroupItemsRequest", "GroupItemsRequest", "DishesRequest", "MenuGroupsRequest",
            "MenusRequest", "ComplexesRequest"};

    public static final String SECTION_NAME = "MenuSupplier";

    public static final Integer ERROR_CODE_ALL_OK = 0;
    public static final Integer ERROR_CODE_NOT_VALID_ATTRIBUTE = 100;

    private static Map<String, Long> versions = new HashMap<>();

    private List<WtOrgGroup> orgGroups = new ArrayList<>();
    private List<WtCategoryItem> categoryItems = new ArrayList<>();
    private List<WtTypeOfProductionItem> typeProductions = new ArrayList<>();
    private List<WtAgeGroupItem> ageGroupItems = new ArrayList<>();
    private List<WtDietType> dietTypes = new ArrayList<>();
    private List<WtComplexGroupItem> complexGroupItems = new ArrayList<>();
    private List<WtGroupItem> groupItems = new ArrayList<>();
    private List<WtDish> dishes = new ArrayList<>();
    private List<WtMenuGroup> menuGroups = new ArrayList<>();
    private List<WtMenu> menus = new ArrayList<>();
    private List<WtComplex> complexes = new ArrayList<>();

    private Integer resCode;
    private String errorMessage;

    public MenuSupplier() {
    }

    public MenuSupplier(Node menuSupplierNode) {

        Node itemNode = menuSupplierNode.getFirstChild();
        int i = 0;
        StringBuilder err = new StringBuilder();

        while (null != itemNode) {

            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName()
                    .equals(CLIENT_SECTION_NAMES[i++])) {
                String strVersion = XMLUtils.getAttributeValue(itemNode, "V");
                if (StringUtils.isNotEmpty(strVersion)) {
                    try {
                        versions.put(itemNode.getNodeName(), Long.parseLong(strVersion));
                    } catch (NumberFormatException e) {
                        err.append("NumberFormatException Version for node ").append(itemNode.getNodeName())
                                .append(" not found\n");
                    }
                } else {
                    err.append("Attribute Version for node ").append(itemNode.getNodeName()).append(" not found\n");
                }
            }
            errorMessage = err.toString();
            itemNode = itemNode.getNextSibling();
        }
        build();
    }

    public void build() {

        Iterator<Map.Entry<String, Long>> iter = versions.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<String, Long> entry = iter.next();

            switch (entry.getKey()) {
                case "OrgGroupsRequest": {
                    //orgGroups = DAOService.getInstance().getOrgGroupsListFromVersion(entry.getValue());
                    orgGroups = DAOReadonlyService.getInstance().getOrgGroupsListFromVersion(entry.getValue());
                    break;
                }
                case "CategoryItemsRequest": {
                    categoryItems = DAOReadonlyService.getInstance().getCategoryItemsListFromVersion(entry.getValue());
                    break;
                }
                case "TypeProductionsRequest": {
                    typeProductions = DAOReadonlyService.getInstance().getTypeProductionsListFromVersion(entry.getValue());
                    break;
                }
                case "AgeGroupItemsRequest": {
                    ageGroupItems = DAOReadonlyService.getInstance().getAgeGroupItemsListFromVersion(entry.getValue());
                    break;
                }
                case "DietTypesRequest": {
                    dietTypes = DAOReadonlyService.getInstance().getDietTypesListFromVersion(entry.getValue());
                    break;
                }
                case "ComplexGroupItemsRequest": {
                    complexGroupItems = DAOReadonlyService.getInstance().getComplexGroupItemsListFromVersion(entry.getValue());
                    break;
                }
                case "GroupItemsRequest": {
                    groupItems = DAOReadonlyService.getInstance().getGroupItemsListFromVersion(entry.getValue());
                    break;
                }
                case "DishesRequest": {
                    dishes = DAOReadonlyService.getInstance().getDishesListFromVersion(entry.getValue());
                    break;
                }
                case "MenuGroupsRequest": {
                    menuGroups = DAOReadonlyService.getInstance().getMenuGroupsListFromVersion(entry.getValue());
                    break;
                }
                case "MenusRequest": {
                    menus = DAOReadonlyService.getInstance().getMenusListFromVersion(entry.getValue());
                    break;
                }
                case "ComplexesRequest": {
                    complexes = DAOReadonlyService.getInstance().getComplexesListFromVersion(entry.getValue());
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

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }

    public Map<String, Long> getVersions() {
        return versions;
    }

    public void setVersions(Map<String, Long> versions) {
        this.versions = versions;
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
