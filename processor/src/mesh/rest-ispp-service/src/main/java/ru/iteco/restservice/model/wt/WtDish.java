/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model.wt;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import ru.iteco.restservice.model.Contragent;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cf_wt_dishes")
public class WtDish {

    @Id
    @Column(name = "idOfDish")
    private Long idOfDish;

    @Column(name = "dishName")
    private String dishName;

    @Column(name = "componentsOfDish")
    private String componentsOfDish;

    @Column(name = "code")
    private String code;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idOfAgeGroupItem")
    @Fetch(value = FetchMode.JOIN)
    private WtAgeGroupItem wtAgeGroupItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idOfTypeOfProductionItem")
    @Fetch(value = FetchMode.JOIN)
    private WtTypeOfProductionItem wtTypeProductionItem;

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

    @ManyToOne(fetch = FetchType.LAZY)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idofcategory")
    private WtCategory category;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dish")
    private Set<ComplexDishRepeatable> repeatableComplex = new HashSet<>();


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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
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

    public WtTypeOfProductionItem getWtTypeProductionItem() {
        return wtTypeProductionItem;
    }

    public void setWtTypeProductionItem(WtTypeOfProductionItem wtTypeProductionItem) {
        this.wtTypeProductionItem = wtTypeProductionItem;
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
        return idOfDish.equals(wtDish.getIdOfDish());
    }

    @Override
    public int hashCode() {
        return idOfDish.hashCode();
    }

    public WtCategory getCategory() {
        return category;
    }

    public void setCategory(WtCategory category) {
        this.category = category;
    }

    public Set<ComplexDishRepeatable> getRepeatableComplex() {
        return repeatableComplex;
    }

    public void setRepeatableComplex(Set<ComplexDishRepeatable> repeatableComplex) {
        this.repeatableComplex = repeatableComplex;
    }
}
