/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import ru.axetta.ecafe.processor.core.persistence.MenuSupplier;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "cf_wt_dishes")
public class WtDish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idOfDish")
    private Long idOfDish;

    @Column(name = "dishName")
    private String dishName;

    @Column(name = "componentsOfDish")
    private String componentsOfDish;

    @Column(name = "code")
    private Integer code;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "dateOfBeginMenuIncluding")
    private Date dateOfBeginMenuIncluding;

    @Column(name = "dateOfEndMenuIncluding")
    private Date dateOfEndMenuIncluding;

    @Column(name = "createDate")
    private Date createDate;

    @Column(name = "lastUpdate")
    private Date lastUpdate;

    @Column(name = "version")
    private Long version;

    @Column(name = "deleteState")
    private Integer deleteState;

    @Column(name = "guid")
    private String guid;

    @Column(name = "protein")
    private Integer protein;

    @Column(name = "fat")
    private Integer fat;

    @Column(name = "carbohydrates")
    private Integer carbohydrates;

    @Column(name = "calories")
    private Integer calories;

    @Column(name = "qty")
    private String qty;

    @ManyToOne
    @JoinColumn(name = "idOfAgeGroupItem")
    private WtAgeGroupItem wtAgeGroupItem;

    @ManyToMany
    @JoinTable(name = "cf_wt_complex_items_dish",
            joinColumns = @JoinColumn(name = "idOfDish"),
            inverseJoinColumns = @JoinColumn(name = "idOfComplexItem"))
    private Set<WtComplexesItem> complexItems = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "cf_wt_dish_categoryitem_relationships",
            joinColumns = @JoinColumn(name = "idOfDish"),
            inverseJoinColumns = @JoinColumn(name = "idOfCategoryItem"))
    private Set<WtCategoryItem> categoryItems = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "cf_wt_dishes_menu_relationships",
            joinColumns = @JoinColumn(name = "idOfDish"),
            inverseJoinColumns = @JoinColumn(name = "idOfMenu"))
    private Set<WtMenu> menus = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "cf_wt_dish_groupitem_relationships",
            joinColumns = @JoinColumn(name = "idOfDish"),
            inverseJoinColumns = @JoinColumn(name = "idOfGroupItem"))
    private Set<WtGroupItem> groupItems = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "cf_wt_menu_group_dish",
            joinColumns = @JoinColumn(name = "dish_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_group_id"))
    private Set<WtMenuGroup> menuGroups = new HashSet<>();

    @OneToMany(mappedBy = "dish")
    private List<MenuSupplier> menuSupplierList;

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

    public List<MenuSupplier> getMenuSupplierList() {
        return menuSupplierList;
    }

    public void setMenuSupplierList(List<MenuSupplier> menuSupplierList) {
        this.menuSupplierList = menuSupplierList;
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
