/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model.wt;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "cf_wt_category_items")
public class WtCategoryItem {
    public static final int ACTIVE = 0;
    public static final int DELETE = 1;

    @Id
    @Column(name = "idofcategoryitem")
    private Long idOfCategoryItem;

    @Column(name = "createdate")
    private Date createDate;

    @Column(name = "lastupdate")
    private Date lastUpdate;

    @Column(name = "version")
    private Long version;

    @Column(name = "guid")
    private String guid;

    @Column(name = "description")
    private String description;

    @Column(name = "deletestate")
    private Integer deleteState;

    @ManyToMany
    @JoinTable(name = "cf_wt_dish_categoryitem_relationships",
            joinColumns = @JoinColumn(name = "idOfCategoryItem"),
            inverseJoinColumns = @JoinColumn(name = "idOfDish"))
    private Set<WtDish> dishes = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "idofcategory")
    private WtCategory wtCategory;

    public static WtCategoryItem build(String description, WtCategory selectedItem) {
        WtCategoryItem item = new WtCategoryItem();
        Date createdDate = new Date();

        item.setCreateDate(createdDate);
        item.setLastUpdate(createdDate);
        item.setDescription(description);
        item.setGuid(UUID.randomUUID().toString());
        item.setDeleteState(WtCategoryItem.ACTIVE);
        item.setWtCategory(selectedItem);

        return item;
    }

    public WtCategory getWtCategory() {
        return wtCategory;
    }

    public void setWtCategory(WtCategory wtCategory) {
        this.wtCategory = wtCategory;
    }

    public Long getIdOfCategoryItem() {
        return idOfCategoryItem;
    }

    public void setIdOfCategoryItem(Long idOfCategoryItem) {
        this.idOfCategoryItem = idOfCategoryItem;
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

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<WtDish> getDishes() {
        return dishes;
    }

    public void setDishes(Set<WtDish> dishes) {
        this.dishes = dishes;
    }

    public Integer getDeleteState() {
        return deleteState;
    }

    public void setDeleteState(Integer deleteState) {
        this.deleteState = deleteState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WtCategoryItem)) {
            return false;
        }
        WtCategoryItem that = (WtCategoryItem) o;
        return Objects.equals(getIdOfCategoryItem(), that.getIdOfCategoryItem());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdOfCategoryItem());
    }
}
