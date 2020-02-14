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
            orgGroupsElem.appendChild(toElement(document, "OGI", orgGroup));
        }

        Element categoryItemsElem = document.createElement("CategoryItems");
        for (WtCategoryItem categoryItem : categoryItems) {
            categoryItemsElem.appendChild(toElement(document, "CTI", categoryItem));
        }

        Element typeProductionsElem = document.createElement("TypeProductions");
        for (WtTypeOfProductionItem typeProduction : typeProductions) {
            typeProductionsElem.appendChild(toElement(document, "TPI", typeProduction));
        }

        Element ageGroupItemsElem = document.createElement("AgeGroupItems");
        for (WtAgeGroupItem ageGroupItem : ageGroupItems) {
            ageGroupItemsElem.appendChild(toElement(document, "AGI", ageGroupItem));
        }

        Element dietTypesElem = document.createElement("DietTypes");
        for (WtDietType dietType : dietTypes) {
            dietTypesElem.appendChild(toElement(document, "DTI", dietType));
        }

        Element complexGroupItemsElem = document.createElement("ComplexGroupItems");
        for (WtComplexGroupItem complexGroupItem : complexGroupItems) {
            complexGroupItemsElem.appendChild(toElement(document, "CGI", complexGroupItem));
        }

        Element groupItemsElem = document.createElement("GroupItems");
        for (WtGroupItem groupItem : groupItems) {
            groupItemsElem.appendChild(toElement(document, "GRI", groupItem));
        }

        Element dishesElem = document.createElement("Dishes");
        for (WtDish dish : dishes) {
            dishesElem.appendChild(toElement(document, "DSI", dish));
        }

        Element menuGroupsElem = document.createElement("MenuGroups");
        for (WtMenuGroup menuGroup : menuGroups) {
            menuGroupsElem.appendChild(toElement(document, "MGI", menuGroup));
        }

        Element menusElem = document.createElement("Menus");
        for (WtMenu menu : menus) {
            menusElem.appendChild(toElement(document, "MSI", menu));
        }

        Element complexesElem = document.createElement("Complexes");
        for (WtComplex complex : complexes) {
            complexesElem.appendChild(toElement(document, "CMI", complex));
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

    public Element toElement(Document document, String elementName, Object object)
            throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "V", object.getClass()); /// Интерфейс! TO_DO
        //XMLUtils.setAttributeIfNotNull(element, "D", deletedState);
        return element;
    }

    public Element toElement(Document document, String elementName)
            throws Exception {
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
