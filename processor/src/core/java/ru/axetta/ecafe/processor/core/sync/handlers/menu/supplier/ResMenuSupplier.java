/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menu.supplier;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.*;
import ru.axetta.ecafe.processor.core.sync.AbstractToElement;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.cxf.common.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: o.petrova
 * Date: 10.02.20
 * Time: 13:02
 * To change this template use File | Settings | File Templates.
 */

public class ResMenuSupplier implements AbstractToElement {

    private final String datePattern = "dd.MM.yyyy hh:mm:ss";

    private Set<WtOrgGroup> orgGroups;
    private Set<WtCategoryItem> categoryItems;
    private Set<WtTypeOfProductionItem> typeProductions;
    private Set<WtAgeGroupItem> ageGroupItems;
    private Set<WtDietType> dietTypes;
    private Set<WtComplexGroupItem> complexGroupItems;
    private Set<WtGroupItem> groupItems;
    private Set<WtDish> dishes;
    private Set<WtMenuGroup> menuGroups;
    private Set<WtMenu> menus;
    private Set<WtComplex> complexes;

    private Long idOfOrg;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);

    public ResMenuSupplier() {
        orgGroups = new HashSet<>();
        categoryItems = new HashSet<>();
        typeProductions = new HashSet<>();
        ageGroupItems = new HashSet<>();
        dietTypes = new HashSet<>();
        complexGroupItems = new HashSet<>();
        groupItems = new HashSet<>();
        dishes = new HashSet<>();
        menuGroups = new HashSet<>();
        menus = new HashSet<>();
        complexes = new HashSet<>();
        idOfOrg = null;
    }

    public ResMenuSupplier(MenuSupplier menuSupplier) {
        orgGroups = menuSupplier.getOrgGroups();
        categoryItems = menuSupplier.getCategoryItems();
        typeProductions = menuSupplier.getTypeProductions();
        ageGroupItems = menuSupplier.getAgeGroupItems();
        dietTypes = menuSupplier.getDietTypes();
        complexGroupItems = menuSupplier.getComplexGroupItems();
        groupItems = menuSupplier.getGroupItems();
        dishes = menuSupplier.getDishes();
        menuGroups = menuSupplier.getMenuGroups();
        menus = menuSupplier.getMenus();
        complexes = menuSupplier.getComplexes();
        idOfOrg = menuSupplier.getIdOfOrg();
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

        Element prop = document.createElement("Prop");
        XMLUtils.setAttributeIfNotNull(prop, "Id", orgGroup.getIdOfOrgGroup());
        XMLUtils.setAttributeIfNotNull(prop, "Name", orgGroup.getNameOfOrgGroup());
        XMLUtils.setAttributeIfNotNull(prop, "ContragentId", orgGroup.getContragent().getIdOfContragent());
        XMLUtils.setAttributeIfNotNull(prop, "D", orgGroup.getDeleteState());
        XMLUtils.setAttributeIfNotNull(prop, "V", orgGroup.getVersion());

        Element orgs = document.createElement("Orgs");
        for (Org item : orgGroup.getOrgs()) {
            Element elem = document.createElement("OGI");
            XMLUtils.setAttributeIfNotNull(elem, "OrgGroupId", orgGroup.getIdOfOrgGroup());
            XMLUtils.setAttributeIfNotNull(elem, "OrgId", item.getIdOfOrg());
            orgs.appendChild(elem);
        }

        element.appendChild(prop);
        element.appendChild(orgs);

        return element;
    }

    private Element categoryItemToElement(Document document, WtCategoryItem categoryItem) throws Exception {
        Element element = document.createElement("CTI");
        XMLUtils.setAttributeIfNotNull(element, "Id", categoryItem.getIdOfCategoryItem());
        XMLUtils.setAttributeIfNotNull(element, "Guid", categoryItem.getGuid());
        XMLUtils.setAttributeIfNotNull(element, "Name", categoryItem.getDescription());
        XMLUtils.setAttributeIfNotNull(element, "V", categoryItem.getVersion());
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
        XMLUtils.setAttributeIfNotNull(prop, "Code", dish.getCode());

        int price;
        if (dish.getPrice() != null) {
            price = (int) dish.getPrice().floatValue() * 100; // цена в копейках
        } else {
            price = 0;
        }
        XMLUtils.setAttributeIfNotNull(prop, "Price", price);

        Date beginDate = dish.getDateOfBeginMenuIncluding();
        if (beginDate != null) {
            XMLUtils.setAttributeIfNotNull(prop, "DateBeginIncludeMenu", simpleDateFormat.format(beginDate));
        }
        Date endDate = dish.getDateOfEndMenuIncluding();
        if (endDate != null) {
            XMLUtils.setAttributeIfNotNull(prop, "DateEndIncludeMenu", simpleDateFormat.format(endDate));
        }

        XMLUtils.setAttributeIfNotNull(prop, "V", dish.getVersion());
        XMLUtils.setAttributeIfNotNull(prop, "D", dish.getDeleteState());
        XMLUtils.setAttributeIfNotNull(prop, "Guid", dish.getGuid());
        XMLUtils.setAttributeIfNotNull(prop, "AgeGroupId", dish.getWtAgeGroupItem().getIdOfAgeGroupItem());
        XMLUtils.setAttributeIfNotNull(prop, "TypeProductionId",
                dish.getWtTypeProductionItem().getIdOfTypeProductionItem());
        if (StringUtils.isEmpty(dish.getBarcode())) {
            XMLUtils.setAttributeIfNotNull(prop, "BarCode", dish.getBarcode());
        }
        XMLUtils.setAttributeIfNotNull(prop, "Protein", dish.getProtein());
        XMLUtils.setAttributeIfNotNull(prop, "Fat", dish.getFat());
        XMLUtils.setAttributeIfNotNull(prop, "Carbohydrates", dish.getCarbohydrates());
        XMLUtils.setAttributeIfNotNull(prop, "Calories", dish.getCalories());
        XMLUtils.setAttributeIfNotNull(prop, "Qty", dish.getQty());
        XMLUtils.setAttributeIfNotNull(prop, "ContragentId", dish.getContragent().getIdOfContragent());

        Element categories = document.createElement("Categories");
        for (WtCategoryItem item : dish.getCategoryItems()) {
            Element elem = document.createElement("CGI");
            XMLUtils.setAttributeIfNotNull(elem, "DishId", dish.getIdOfDish());
            XMLUtils.setAttributeIfNotNull(elem, "CategoryId", item.getIdOfCategoryItem());
            categories.appendChild(elem);
        }

        Element groupItems = document.createElement("GroupItems");
        for (WtGroupItem item : dish.getGroupItems()) {
            Element elem = document.createElement("GII");
            XMLUtils.setAttributeIfNotNull(elem, "DishId", dish.getIdOfDish());
            XMLUtils.setAttributeIfNotNull(elem, "GroupItemId", item.getIdOfGroupItem());
            groupItems.appendChild(elem);
        }

        element.appendChild(prop);
        element.appendChild(categories);
        element.appendChild(groupItems);

        return element;
    }

    private Element menuGroupToElement(Document document, WtMenuGroup menuGroup) {
        Element element = document.createElement("MGI");

        XMLUtils.setAttributeIfNotNull(element, "Id", menuGroup.getId());
        XMLUtils.setAttributeIfNotNull(element, "Name", menuGroup.getName());
        if (!menuGroup.getMenuGroupMenus().isEmpty()) {
            List<WtMenuGroupMenu> menuGroupMenus = new ArrayList<>(menuGroup.getMenuGroupMenus());
            XMLUtils.setAttributeIfNotNull(element, "MenuId", menuGroupMenus.get(0));
        }
        XMLUtils.setAttributeIfNotNull(element, "V", menuGroup.getVersion());
        XMLUtils.setAttributeIfNotNull(element, "D", menuGroup.getDeleteState());

        return element;
    }

    private Element menuToElement(Document document, WtMenu menu) {
        Element element = document.createElement("MSI");

        Element prop = document.createElement("Prop");
        XMLUtils.setAttributeIfNotNull(prop, "Id", menu.getIdOfMenu());
        XMLUtils.setAttributeIfNotNull(prop, "Name", menu.getMenuName());

        Date beginDate = menu.getBeginDate();
        if (beginDate != null) {
            XMLUtils.setAttributeIfNotNull(prop, "BeginDate", simpleDateFormat.format(beginDate));
        }
        Date endDate = menu.getEndDate();
        if (endDate != null) {
            XMLUtils.setAttributeIfNotNull(prop, "EndDate", simpleDateFormat.format(endDate));
        }

        if (menu.getWtOrgGroup() != null) {
            XMLUtils.setAttributeIfNotNull(prop, "OrgGroupId", menu.getWtOrgGroup().getIdOfOrgGroup());
        }
        XMLUtils.setAttributeIfNotNull(prop, "ContragentId", menu.getContragent().getIdOfContragent());
        XMLUtils.setAttributeIfNotNull(prop, "V", menu.getVersion());
        XMLUtils.setAttributeIfNotNull(prop, "D", menu.getDeleteState());

        Element dishes = document.createElement("Dishes");
        List<WtDish> menuDishesList = DAOReadonlyService.getInstance().getMenuDishes(menu);
        if (menuDishesList != null && menuDishesList.size() > 0) {
            Set<WtDish> menuDishesSet = new HashSet<>(menuDishesList);
            for (WtDish dish : menuDishesSet) {
                Element elem = document.createElement("DSI");
                XMLUtils.setAttributeIfNotNull(elem, "MenuId", menu.getIdOfMenu());
                XMLUtils.setAttributeIfNotNull(element, "DishId", dish.getIdOfDish());
                if (dish.getMenuGroupMenus() != null && dish.getMenuGroupMenus().size() > 0) {
                    for (WtMenuGroupMenu wtMenuGroupMenu: dish.getMenuGroupMenus()) {
                        if (wtMenuGroupMenu.getMenu() != null) {
                            XMLUtils.setAttributeIfNotNull(element, "MenuGroupId", wtMenuGroupMenu.getMenu());
                            break;
                        }
                    }
                }
                dishes.appendChild(elem);
            }
        }

        Element orgs = document.createElement("Orgs");
        for (Org item : menu.getOrgs()) {
            Element elem = document.createElement("OGI");
            XMLUtils.setAttributeIfNotNull(elem, "MenuId", menu.getIdOfMenu());
            XMLUtils.setAttributeIfNotNull(elem, "OrgId", item.getIdOfOrg());
            orgs.appendChild(elem);
        }

        element.appendChild(prop);
        element.appendChild(dishes);
        element.appendChild(orgs);

        return element;
    }

    private Element complexToElement(Document document, WtComplex complex) {
        Element element = document.createElement("CMI");

        Element prop = document.createElement("Prop");
        XMLUtils.setAttributeIfNotNull(prop, "Id", complex.getIdOfComplex());
        XMLUtils.setAttributeIfNotNull(prop, "Name", complex.getName());

        int price;
        if (complex.getPrice() != null) {
            price = (int) complex.getPrice().floatValue() * 100; // цена в копейках
        } else {
            price = 0;
        }
        XMLUtils.setAttributeIfNotNull(prop, "Price", price);

        Date beginDate = complex.getBeginDate();
        if (beginDate != null) {
            XMLUtils.setAttributeIfNotNull(prop, "BeginDate", simpleDateFormat.format(beginDate));
        }
        Date endDate = complex.getEndDate();
        if (endDate != null) {
            XMLUtils.setAttributeIfNotNull(prop, "EndDate", simpleDateFormat.format(endDate));
        }

        XMLUtils.setAttributeIfNotNull(prop, "CycleMotion", complex.getCycleMotion());
        XMLUtils.setAttributeIfNotNull(prop, "DayInCycle", complex.getDayInCycle());
        XMLUtils.setAttributeIfNotNull(prop, "V", complex.getVersion());
        XMLUtils.setAttributeIfNotNull(prop, "Guid", complex.getGuid());
        XMLUtils.setAttributeIfNotNull(prop, "D", complex.getDeleteState());
        XMLUtils.setAttributeIfNotNull(prop, "ComplexGroupItemId",
                complex.getWtComplexGroupItem().getIdOfComplexGroupItem());
        XMLUtils.setAttributeIfNotNull(prop, "AgeGroupItemId", complex.getWtAgeGroupItem().getIdOfAgeGroupItem());
        XMLUtils.setAttributeIfNotNull(prop, "DietTypeId", complex.getWtDietType().getIdOfDietType());
        XMLUtils.setAttributeIfNotNull(prop, "ContragentId", complex.getContragent().getIdOfContragent());
        if (complex.getWtOrgGroup() != null) {
            XMLUtils.setAttributeIfNotNull(prop, "OrgGroupId", complex.getWtOrgGroup().getIdOfOrgGroup());
        }
        XMLUtils.setAttributeIfNotNull(prop, "Composite", complex.getComposite());
        XMLUtils.setAttributeIfNotNull(prop, "IsPortal", complex.getIsPortal());
        XMLUtils.setAttributeIfNotNull(prop, "StartCycleDay", complex.getStartCycleDay());

        Element items = document.createElement("Items");
        for (WtComplexesItem item : complex.getWtComplexesItems()) {
            Element elem = document.createElement("CII");
            XMLUtils.setAttributeIfNotNull(elem, "Id", item.getIdOfComplexItem());
            XMLUtils.setAttributeIfNotNull(elem, "ComplexId", complex.getIdOfComplex());
            XMLUtils.setAttributeIfNotNull(elem, "CycleDay", item.getCycleDay());
            XMLUtils.setAttributeIfNotNull(elem, "CountDishes", item.getCountDishes());
            items.appendChild(elem);
        }

        Element itemsDishes = document.createElement("ItemsDishes");
        for (WtComplexesItem item : complex.getWtComplexesItems()) {
            for (WtDish dish : item.getDishes()) {
                Element elem = document.createElement("CID");
                XMLUtils.setAttributeIfNotNull(elem, "ComplexItemId", item.getIdOfComplexItem());
                XMLUtils.setAttributeIfNotNull(elem, "DishId", dish.getIdOfDish());
                itemsDishes.appendChild(elem);
            }
        }

        Element orgs = document.createElement("Orgs");
        for (Org item : complex.getOrgs()) {
            Element elem = document.createElement("ORI");
            XMLUtils.setAttributeIfNotNull(elem, "ComplexId", complex.getIdOfComplex());
            XMLUtils.setAttributeIfNotNull(elem, "OrgId", item.getIdOfOrg());
            orgs.appendChild(elem);
        }

        element.appendChild(prop);
        element.appendChild(items);
        element.appendChild(itemsDishes);
        element.appendChild(orgs);

        return element;
    }

    public Element toElement(Document document, String elementName) throws Exception {
        return document.createElement(elementName);
    }

    public String getDatePattern() {
        return datePattern;
    }

    public Set<WtOrgGroup> getOrgGroups() {
        return orgGroups;
    }

    public void setOrgGroups(Set<WtOrgGroup> orgGroups) {
        this.orgGroups = orgGroups;
    }

    public Set<WtCategoryItem> getCategoryItems() {
        return categoryItems;
    }

    public void setCategoryItems(Set<WtCategoryItem> categoryItems) {
        this.categoryItems = categoryItems;
    }

    public Set<WtTypeOfProductionItem> getTypeProductions() {
        return typeProductions;
    }

    public void setTypeProductions(Set<WtTypeOfProductionItem> typeProductions) {
        this.typeProductions = typeProductions;
    }

    public Set<WtAgeGroupItem> getAgeGroupItems() {
        return ageGroupItems;
    }

    public void setAgeGroupItems(Set<WtAgeGroupItem> ageGroupItems) {
        this.ageGroupItems = ageGroupItems;
    }

    public Set<WtDietType> getDietTypes() {
        return dietTypes;
    }

    public void setDietTypes(Set<WtDietType> dietTypes) {
        this.dietTypes = dietTypes;
    }

    public Set<WtComplexGroupItem> getComplexGroupItems() {
        return complexGroupItems;
    }

    public void setComplexGroupItems(Set<WtComplexGroupItem> complexGroupItems) {
        this.complexGroupItems = complexGroupItems;
    }

    public Set<WtGroupItem> getGroupItems() {
        return groupItems;
    }

    public void setGroupItems(Set<WtGroupItem> groupItems) {
        this.groupItems = groupItems;
    }

    public Set<WtDish> getDishes() {
        return dishes;
    }

    public void setDishes(Set<WtDish> dishes) {
        this.dishes = dishes;
    }

    public Set<WtMenuGroup> getMenuGroups() {
        return menuGroups;
    }

    public void setMenuGroups(Set<WtMenuGroup> menuGroups) {
        this.menuGroups = menuGroups;
    }

    public Set<WtMenu> getMenus() {
        return menus;
    }

    public void setMenus(Set<WtMenu> menus) {
        this.menus = menus;
    }

    public Set<WtComplex> getComplexes() {
        return complexes;
    }

    public void setComplexes(Set<WtComplex> complexes) {
        this.complexes = complexes;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }
}
