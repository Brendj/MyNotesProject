/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menu.supplier;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.*;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

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
            "MenusRequest", "ComplexesRequest", "ExcludeDaysRequest"};

    public static final String SECTION_NAME = "MenuSupplier";

    public static final Integer ERROR_CODE_ALL_OK = 0;
    public static final Integer ERROR_CODE_NOT_VALID_ATTRIBUTE = 100;

    private static Map<String, Long> versions = new HashMap<>();

    private Long idOfOrg;

    private Set<WtOrgGroup> orgGroups = new HashSet<>();
    private Set<WtOrgGroup> offlineOrgGroups = new HashSet<>();
    private Set<WtCategoryItem> categoryItems = new HashSet<>();
    private Set<WtTypeOfProductionItem> typeProductions = new HashSet<>();
    private Set<WtAgeGroupItem> ageGroupItems = new HashSet<>();
    private Set<WtDietType> dietTypes = new HashSet<>();
    private Set<WtComplexGroupItem> complexGroupItems = new HashSet<>();
    private Set<WtGroupItem> groupItems = new HashSet<>();
    private Set<WtDish> dishes = new HashSet<>();
    private Set<WtMenuGroup> menuGroups = new HashSet<>();
    private Set<WtMenu> menus = new HashSet<>();
    private Set<WtMenu> offlineMenus = new HashSet<>();
    private Set<WtComplex> complexes = new HashSet<>();
    private Set<WtComplex> offlineComplexes = new HashSet<>();
    private Set<WtComplexExcludeDays> excludeDays = new HashSet<>();

    private Integer resCode;
    private String errorMessage;

    public MenuSupplier() {
    }

    public MenuSupplier build(Node menuSupplierNode, Long idOfOrg) {

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
            itemNode = itemNode.getNextSibling();
        }
        return new MenuSupplier(idOfOrg, err.toString());
    }

    public MenuSupplier(Long idOfOrg, String errorMessage) {

        this.setIdOfOrg(idOfOrg);
        this.setErrorMessage(errorMessage);
        if (errorMessage.equals("")) {
            this.setResCode(ERROR_CODE_ALL_OK);
        } else {
            this.setResCode(ERROR_CODE_NOT_VALID_ATTRIBUTE);
        }

        DAOReadonlyService daoReadonlyService = DAOReadonlyService.getInstance();

        Org org = daoReadonlyService.findOrg(idOfOrg);
        Set<Org> friendlyOrgs = daoReadonlyService.findFriendlyOrgs(idOfOrg);
        Contragent contragent = daoReadonlyService.findDefaultSupplier(idOfOrg);

        Iterator<Map.Entry<String, Long>> iter = versions.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<String, Long> entry = iter.next();

            switch (entry.getKey()) {
                case "OrgGroupsRequest": {
                    orgGroups = daoReadonlyService.getOrgGroupsSetFromVersion(entry.getValue(), contragent, org);
                    for (Org item : friendlyOrgs) {
                        Contragent itemContragent = daoReadonlyService.findDefaultSupplier(item.getIdOfOrg());
                        Set<WtOrgGroup> friendlyItems = daoReadonlyService
                                .getOrgGroupsSetFromVersion(entry.getValue(), itemContragent, item);
                        orgGroups.addAll(friendlyItems);
                    }
                    offlineOrgGroups = DAOReadonlyService.getInstance()
                            .getOfflineOrgGroupsSetFromVersion(entry.getValue(), org);
                    break;
                }
                case "CategoryItemsRequest": {
                    categoryItems = daoReadonlyService.getCategoryItemsSetFromVersion(entry.getValue());
                    break;
                }
                case "TypeProductionsRequest": {
                    typeProductions = daoReadonlyService
                            .getTypeProductionsSetFromVersion(entry.getValue());
                    break;
                }
                case "AgeGroupItemsRequest": {
                    ageGroupItems = daoReadonlyService.getAgeGroupItemsSetFromVersion(entry.getValue());
                    break;
                }
                case "DietTypesRequest": {
                    dietTypes = daoReadonlyService.getDietTypesSetFromVersion(entry.getValue());
                    break;
                }
                case "ComplexGroupItemsRequest": {
                    complexGroupItems = daoReadonlyService.getComplexGroupItemsSetFromVersion(entry.getValue());
                    break;
                }
                case "GroupItemsRequest": {
                    groupItems = daoReadonlyService.getGroupItemsSetFromVersion(entry.getValue());
                    break;
                }
                case "DishesRequest": {
                    Set<Long> contragents = new HashSet<>();
                    contragents.add(contragent.getIdOfContragent());
                    dishes = daoReadonlyService.getDishesListFromVersion(entry.getValue(), contragent);
                    for (Org item : friendlyOrgs) {
                        Contragent itemContragent = daoReadonlyService.findDefaultSupplier(item.getIdOfOrg());
                        if (contragents.contains(itemContragent.getIdOfContragent())) continue;

                        Set<WtDish> friendlyItems = daoReadonlyService
                                .getDishesListFromVersion(entry.getValue(), itemContragent);
                        dishes.addAll(friendlyItems);
                        contragents.add(itemContragent.getIdOfContragent());
                    }
                    break;
                }
                case "MenuGroupsRequest": {
                    menuGroups = daoReadonlyService.getMenuGroupsSetFromVersion(entry.getValue(), contragent);
                    break;
                }
                case "MenusRequest": {
                    menus = daoReadonlyService.getMenusSetFromVersion(entry.getValue(), contragent, org);
                    offlineMenus = daoReadonlyService.getOfflineMenusSetFromVersion(entry.getValue(), org);
                    break;
                }
                case "ComplexesRequest": {
                    complexes = daoReadonlyService.getComplexesSetFromVersion(entry.getValue(), contragent, org);
                    for (Org item : friendlyOrgs) {
                        Contragent itemContragent = daoReadonlyService.findDefaultSupplier(item.getIdOfOrg());
                        Set<WtComplex> friendlyItems = daoReadonlyService.getComplexesSetFromVersion(entry.getValue(), itemContragent, item);
                        complexes.addAll(friendlyItems);
                    }
                    offlineComplexes = daoReadonlyService.getOfflineComplexesSetFromVersion(entry.getValue(), org);
                    break;
                }
                case "ExcludeDaysRequest": {
                    excludeDays = daoReadonlyService.getExcludeDaysSetFromVersion(entry.getValue(), contragent, org);
                    break;
                }
            }
        }
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
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

    public Set<WtComplexExcludeDays> getExcludeDays() {
        return excludeDays;
    }

    public void setExcludeDays(Set<WtComplexExcludeDays> excludeDays) {
        this.excludeDays = excludeDays;
    }

    public Integer getResCode() {
        return resCode;
    }

    public void setResCode(Integer resCode) {
        this.resCode = resCode;
    }

    public Set<WtComplex> getOfflineComplexes() {
        return offlineComplexes;
    }

    public void setOfflineComplexes(Set<WtComplex> offlineComplexes) {
        this.offlineComplexes = offlineComplexes;
    }

    public Set<WtMenu> getOfflineMenus() {
        return offlineMenus;
    }

    public void setOfflineMenus(Set<WtMenu> offlineMenus) {
        this.offlineMenus = offlineMenus;
    }

    public Set<WtOrgGroup> getOfflineOrgGroups() {
        return offlineOrgGroups;
    }

    public void setOfflineOrgGroups(Set<WtOrgGroup> offlineOrgGroups) {
        this.offlineOrgGroups = offlineOrgGroups;
    }
}
