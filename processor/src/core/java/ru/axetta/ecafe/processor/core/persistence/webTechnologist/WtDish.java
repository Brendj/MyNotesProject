/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class WtDish {

    private Long idOfDish;
    private String dishName;
    private String componentsOfDish;
    private Integer code;
    private BigDecimal price;
    private Date dateOfBeginMenuIncluding;
    private Date dateOfEndMenuIncluding;
    private Date createDate;
    private Date lastUpdate;
    private Long version;
    private Integer deleteState;
    private String guid;
    private Integer protein;
    private Integer fat;
    private Integer carbohydrates;
    private Integer calories;
    private String qty;
    private WtAgeGroupItem wtAgeGroupItem;
    private Set<WtComplexesItem> complexItems = new HashSet<>();
    private Set<WtCategoryItem> categoryItems = new HashSet<>();
    private Set<WtMenu> menus = new HashSet<>();
    private Set<WtGroupItem> groupItems = new HashSet<>();
    private Set<WtMenuGroup> menuGroups = new HashSet<>();

    public Long getIdOfDish() {
        return idOfDish;
    }

    public void setIdOfDish(Long idOfDish) {
        this.idOfDish = idOfDish;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getComponentsOfDish() {
        return componentsOfDish;
    }

    public void setComponentsOfDish(String componentsOfDish) {
        this.componentsOfDish = componentsOfDish;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getDateOfBeginMenuIncluding() {
        return dateOfBeginMenuIncluding;
    }

    public void setDateOfBeginMenuIncluding(Date dateOfBeginMenuIncluding) {
        this.dateOfBeginMenuIncluding = dateOfBeginMenuIncluding;
    }

    public Date getDateOfEndMenuIncluding() {
        return dateOfEndMenuIncluding;
    }

    public void setDateOfEndMenuIncluding(Date dateOfEndMenuIncluding) {
        this.dateOfEndMenuIncluding = dateOfEndMenuIncluding;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Integer getDeleteState() {
        return deleteState;
    }

    public void setDeleteState(Integer deleteState) {
        this.deleteState = deleteState;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Integer getProtein() {
        return protein;
    }

    public void setProtein(Integer protein) {
        this.protein = protein;
    }

    public Integer getFat() {
        return fat;
    }

    public void setFat(Integer fat) {
        this.fat = fat;
    }

    public Integer getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(Integer carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public WtAgeGroupItem getWtAgeGroupItem() {
        return wtAgeGroupItem;
    }

    public void setWtAgeGroupItem(WtAgeGroupItem wtAgeGroupItem) {
        this.wtAgeGroupItem = wtAgeGroupItem;
    }

    public Set<WtComplexesItem> getComplexItems() {
        return complexItems;
    }

    public void setComplexItems(Set<WtComplexesItem> complexItems) {
        this.complexItems = complexItems;
    }

    public Set<WtCategoryItem> getCategoryItems() {
        return categoryItems;
    }

    public void setCategoryItems(Set<WtCategoryItem> categoryItems) {
        this.categoryItems = categoryItems;
    }

    public Set<WtMenu> getMenus() {
        return menus;
    }

    public void setMenus(Set<WtMenu> menus) {
        this.menus = menus;
    }

    public Set<WtGroupItem> getGroupItems() {
        return groupItems;
    }

    public void setGroupItems(Set<WtGroupItem> groupItems) {
        this.groupItems = groupItems;
    }

    public Set<WtMenuGroup> getMenuGroups() {
        return menuGroups;
    }

    public void setMenuGroups(Set<WtMenuGroup> menuGroups) {
        this.menuGroups = menuGroups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WtDish that = (WtDish) o;
        return Objects.equals(idOfDish, that.idOfDish) && Objects.equals(dishName, that.dishName) && Objects
                .equals(componentsOfDish, that.componentsOfDish) && Objects.equals(code, that.code) && Objects
                .equals(price, that.price) && Objects.equals(dateOfBeginMenuIncluding, that.dateOfBeginMenuIncluding)
                && Objects.equals(dateOfEndMenuIncluding, that.dateOfEndMenuIncluding) && Objects
                .equals(createDate, that.createDate) && Objects.equals(lastUpdate, that.lastUpdate) && Objects
                .equals(version, that.version) && Objects.equals(deleteState, that.deleteState) && Objects
                .equals(guid, that.guid) && Objects.equals(protein, that.protein) && Objects.equals(fat, that.fat)
                && Objects.equals(carbohydrates, that.carbohydrates) && Objects.equals(calories, that.calories)
                && Objects.equals(qty, that.qty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfDish, dishName, componentsOfDish, code, price, dateOfBeginMenuIncluding,
                dateOfEndMenuIncluding, createDate, lastUpdate, version, deleteState, guid, protein, fat, carbohydrates,
                calories, qty);
    }
}
