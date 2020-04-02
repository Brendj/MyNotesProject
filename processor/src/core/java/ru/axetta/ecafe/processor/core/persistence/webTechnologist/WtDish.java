/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    @ManyToOne
    @JoinColumn(name = "create_by_id")
    private User createdUser;

    @Column(name = "guid")
    private String guid;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idOfAgeGroupItem")
    private WtAgeGroupItem wtAgeGroupItem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idOfTypeOfProductionItem")
    private WtTypeOfProductionItem wtTypeProductionItem;

    @ManyToOne
    @JoinColumn(name = "update_by_id")
    private User updatedUser;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idOfContragent")
    private Contragent contragent;

    @Column(name = "barcode")
    private String barcode;

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
    @JoinTable(name = "cf_wt_dish_groupitem_relationships",
            joinColumns = @JoinColumn(name = "idOfDish"),
            inverseJoinColumns = @JoinColumn(name = "idOfGroupItem"))
    private Set<WtGroupItem> groupItems = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "cf_wt_menu_group_dish_relationships",
            joinColumns = @JoinColumn(name = "idOfDish"),
            inverseJoinColumns = @JoinColumn(name = "idOfMenuMenuGroupRelation"))
    private Set<WtMenuGroupMenu> menuGroupMenus = new HashSet<>();


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

    public User getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(User createdUser) {
        this.createdUser = createdUser;
    }

    public WtTypeOfProductionItem getWtTypeProductionItem() {
        return wtTypeProductionItem;
    }

    public void setWtTypeProductionItem(WtTypeOfProductionItem wtTypeProductionItem) {
        this.wtTypeProductionItem = wtTypeProductionItem;
    }

    public User getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(User updatedUser) {
        this.updatedUser = updatedUser;
    }

    public Contragent getContragent() {
        return contragent;
    }

    public void setContragent(Contragent contragent) {
        this.contragent = contragent;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
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

    public Set<WtMenuGroupMenu> getMenuGroupMenus() {
        return menuGroupMenus;
    }

    public void setMenuGroupMenus(Set<WtMenuGroupMenu> menuGroupMenus) {
        this.menuGroupMenus = menuGroupMenus;
    }

    public Set<WtGroupItem> getGroupItems() {
        return groupItems;
    }

    public void setGroupItems(Set<WtGroupItem> groupItems) {
        this.groupItems = groupItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WtDish wtDish = (WtDish) o;
        return idOfDish.equals(wtDish.idOfDish) && Objects.equals(dishName, wtDish.dishName) && Objects
                .equals(componentsOfDish, wtDish.componentsOfDish) && Objects.equals(code, wtDish.code) && Objects
                .equals(price, wtDish.price) && Objects
                .equals(dateOfBeginMenuIncluding, wtDish.dateOfBeginMenuIncluding) && Objects
                .equals(dateOfEndMenuIncluding, wtDish.dateOfEndMenuIncluding) && Objects
                .equals(createDate, wtDish.createDate) && Objects.equals(lastUpdate, wtDish.lastUpdate) && Objects
                .equals(version, wtDish.version) && Objects.equals(deleteState, wtDish.deleteState) && Objects
                .equals(guid, wtDish.guid) && Objects.equals(protein, wtDish.protein) && Objects.equals(fat, wtDish.fat)
                && Objects.equals(carbohydrates, wtDish.carbohydrates) && Objects.equals(calories, wtDish.calories)
                && Objects.equals(qty, wtDish.qty) && Objects.equals(barcode, wtDish.barcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfDish, dishName, componentsOfDish, code, price, dateOfBeginMenuIncluding,
                dateOfEndMenuIncluding, createDate, lastUpdate, version, deleteState, guid, protein, fat, carbohydrates,
                calories, qty, barcode);
    }
}
