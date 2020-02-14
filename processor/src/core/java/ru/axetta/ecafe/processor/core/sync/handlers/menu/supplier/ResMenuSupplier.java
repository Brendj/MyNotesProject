/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menu.supplier;

import ru.axetta.ecafe.processor.core.persistence.webTechnologist.*;
import ru.axetta.ecafe.processor.core.sync.AbstractToElement;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: o.petrova
 * Date: 10.02.20
 * Time: 13:02
 * To change this template use File | Settings | File Templates.
 */

public class ResMenuSupplier implements AbstractToElement {

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


    public ResMenuSupplier() {

        orgGroups = new ArrayList<>();
        categoryItems = new ArrayList<>();
        typeProductions = new ArrayList<>();
        ageGroupItems = new ArrayList<>();
        dietTypes = new ArrayList<>();
        complexGroupItems = new ArrayList<>();
        groupItems = new ArrayList<>();
        dishes = new ArrayList<>();
        menuGroups = new ArrayList<>();
        menus = new ArrayList<>();
        complexes = new ArrayList<>();
    }

    @Override
    public Element toElement(Document document) throws Exception {

        Element element = document.createElement("MenuSupplier");

        Element orgGroupsElem = document.createElement("OrgGroups");
        for (WtOrgGroup orgGroup : orgGroups) {
            orgGroupsElem.appendChild(orgGroupToElement(document, orgGroup));
        }

        Element categoryItemsElem = document.createElement("CategoryItems");
        for (WtCategoryItem categoryItem : categoryItems) {
            categoryItemsElem.appendChild(categoryItemToElement(document, categoryItem));
        }

        Element typeProductionsElem = document.createElement("TypeProductions");
        for (WtTypeOfProductionItem typeProduction : typeProductions) {
            typeProductionsElem.appendChild(typeProductionToElement(document, typeProduction));
        }

        Element ageGroupItemsElem = document.createElement("AgeGroupItems");
        for (WtAgeGroupItem ageGroupItem : ageGroupItems) {
            ageGroupItemsElem.appendChild(ageGroupToElement(document, ageGroupItem));
        }

        Element dietTypesElem = document.createElement("DietTypes");
        for (WtDietType dietType : dietTypes) {
            dietTypesElem.appendChild(dietTypeToElement(document, dietType));
        }

        Element complexGroupItemsElem = document.createElement("ComplexGroupItems");
        for (WtComplexGroupItem complexGroupItem : complexGroupItems) {
            complexGroupItemsElem.appendChild(complexGroupItemToElement(document, complexGroupItem));
        }

        Element groupItemsElem = document.createElement("GroupItems");
        for (WtGroupItem groupItem : groupItems) {
            groupItemsElem.appendChild(groupItemToElement(document, groupItem));
        }

        Element dishesElem = document.createElement("Dishes");
        for (WtDish dish : dishes) {
            dishesElem.appendChild(dishToElement(document, dish));
        }

        Element menuGroupsElem = document.createElement("MenuGroups");
        for (WtMenuGroup menuGroup : menuGroups) {
            menuGroupsElem.appendChild(menuGroupToElement(document, menuGroup));
        }

        Element menusElem = document.createElement("Menus");
        for (WtMenu menu : menus) {
            menusElem.appendChild(menuToElement(document, menu));
        }

        Element complexesElem = document.createElement("Complexes");
        for (WtComplex complex : complexes) {
            complexesElem.appendChild(complexToElement(document, complex));
        }

        element.appendChild(orgGroupsElem);
        element.appendChild(categoryItemsElem);
        element.appendChild(typeProductionsElem);
        element.appendChild(ageGroupItemsElem);
        element.appendChild(dietTypesElem);
        element.appendChild(complexGroupItemsElem);
        element.appendChild(groupItemsElem);
        element.appendChild(dishesElem);
        element.appendChild(menuGroupsElem);
        element.appendChild(menusElem);
        element.appendChild(complexesElem);

        return element;
    }

    private Element orgGroupToElement(Document document, WtOrgGroup orgGroup) {
        Element element = document.createElement("OGI");
        XMLUtils.setAttributeIfNotNull(element, "Id", orgGroup.getIdOfOrgGroup());
        XMLUtils.setAttributeIfNotNull(element, "Name", orgGroup.getNameOfOrgGroup());
        //XMLUtils.setAttributeIfNotNull(element, "ContragentId", orgGroup.getOrgs().toString()); // !!!
        XMLUtils.setAttributeIfNotNull(element, "V", orgGroup.getVersion());
        XMLUtils.setAttributeIfNotNull(element, "D", orgGroup.getDeleteState());
        return element;
    }

    private Element categoryItemToElement(Document document, WtCategoryItem categoryItem) throws Exception {
        Element element = document.createElement("CTI");
        XMLUtils.setAttributeIfNotNull(element, "Id", categoryItem.getIdOfCategoryItem());
        XMLUtils.setAttributeIfNotNull(element, "Guid", categoryItem.getGuid());
        XMLUtils.setAttributeIfNotNull(element, "Name", categoryItem.getDescription());
        XMLUtils.setAttributeIfNotNull(element, "V", categoryItem.getVersion());
        //XMLUtils.setAttributeIfNotNull(element, "D", categoryItem.getDeleteState());
        return element;
    }

    private Element typeProductionToElement(Document document, WtTypeOfProductionItem typeProduction) {
        Element element = document.createElement("TPI");
        XMLUtils.setAttributeIfNotNull(element, "Id", typeProduction.getIdOfTypeProductionItem());
        XMLUtils.setAttributeIfNotNull(element, "Name", typeProduction.getDescription());
        XMLUtils.setAttributeIfNotNull(element, "V", typeProduction.getVersion());
        return element;
    }

    private Element ageGroupToElement(Document document, WtAgeGroupItem ageGroupItem) {
        Element element = document.createElement("AGI");
        XMLUtils.setAttributeIfNotNull(element, "Id", ageGroupItem.getIdOfAgeGroupItem());
        XMLUtils.setAttributeIfNotNull(element, "Name", ageGroupItem.getDescription());
        XMLUtils.setAttributeIfNotNull(element, "V", ageGroupItem.getVersion());
        return element;
    }

    private Element dietTypeToElement(Document document, WtDietType dietType) {
        Element element = document.createElement("DTI");
        XMLUtils.setAttributeIfNotNull(element, "Id", dietType.getIdOfDietType());
        XMLUtils.setAttributeIfNotNull(element, "Name", dietType.getDescription());
        XMLUtils.setAttributeIfNotNull(element, "V", dietType.getVersion());
        return element;
    }

    private Element complexGroupItemToElement(Document document, WtComplexGroupItem complexGroupItem) {
        Element element = document.createElement("CGI");
        XMLUtils.setAttributeIfNotNull(element, "Id", complexGroupItem.getIdOfComplexGroupItem());
        XMLUtils.setAttributeIfNotNull(element, "Name", complexGroupItem.getDescription());
        XMLUtils.setAttributeIfNotNull(element, "V", complexGroupItem.getVersion());
        return element;
    }

    private Element groupItemToElement(Document document, WtGroupItem groupItem) {
        Element element = document.createElement("GRI");
        XMLUtils.setAttributeIfNotNull(element, "Id", groupItem.getIdOfGroupItem());
        XMLUtils.setAttributeIfNotNull(element, "Name", groupItem.getDescription());
        XMLUtils.setAttributeIfNotNull(element, "V", groupItem.getVersion());
        return element;
    }

    private Element dishToElement(Document document, WtDish dish) {
        Element element = document.createElement("DSI");

        Element prop = document.createElement("Prop");
        XMLUtils.setAttributeIfNotNull(prop, "Id", dish.getIdOfDish());
        XMLUtils.setAttributeIfNotNull(prop, "Name", dish.getDishName());
        XMLUtils.setAttributeIfNotNull(prop, "Components", dish.getComponentsOfDish());
        XMLUtils.setAttributeIfNotNull(prop, "Code", dish.getDishName());
        XMLUtils.setAttributeIfNotNull(prop, "Price", dish.getPrice());
        XMLUtils.setAttributeIfNotNull(prop, "BeginDate", dish.getDateOfBeginMenuIncluding());
        XMLUtils.setAttributeIfNotNull(prop, "EndDate", dish.getDateOfEndMenuIncluding());
        XMLUtils.setAttributeIfNotNull(prop, "V", dish.getVersion());
        XMLUtils.setAttributeIfNotNull(element, "D", dish.getDeleteState());
        XMLUtils.setAttributeIfNotNull(element, "Guid", dish.getGuid());
        XMLUtils.setAttributeIfNotNull(element, "AgeGroupId", dish.getWtAgeGroupItem().getIdOfAgeGroupItem());
        //XMLUtils.setAttributeIfNotNull(element, "TypeProductionId", dish.);
        XMLUtils.setAttributeIfNotNull(element, "Protein", dish.getProtein());
        XMLUtils.setAttributeIfNotNull(element, "Fat", dish.getFat());
        XMLUtils.setAttributeIfNotNull(element, "Carbohydrates", dish.getCarbohydrates());
        XMLUtils.setAttributeIfNotNull(element, "Calories", dish.getCalories());
        XMLUtils.setAttributeIfNotNull(element, "Qty", dish.getQty());
        //XMLUtils.setAttributeIfNotNull(element, "ContragentId", dish.get);

        element.appendChild(prop);
        return element;
    }

    private Element menuGroupToElement(Document document, WtMenuGroup menuGroup) {
        Element element = document.createElement("MGI");
        return element;
    }

    private Element menuToElement(Document document, WtMenu menu) {
        Element element = document.createElement("MSI");
        return element;
    }

    private Element complexToElement(Document document, WtComplex complex) {
        Element element = document.createElement("CMI");
        return element;
    }

    public Element toElement(Document document, String elementName) throws Exception {
        return document.createElement(elementName);
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
}
