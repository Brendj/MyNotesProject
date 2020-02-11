/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import ru.axetta.ecafe.processor.core.persistence.MenuSupplier;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "cf_wt_category_items")
public class WtCategoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idOfCategoryItem")
    private Long idOfCategoryItem;

    @Column(name = "createDate")
    private Date createDate;

    @Column(name = "lastUpdate")
    private Date lastUpdate;

    @Column(name = "version")
    private Long version;

    @Column(name = "guid")
    private String guid;

    @Column(name = "description")
    private String description;

    @ManyToMany
    @JoinTable(name = "cf_wt_dish_categoryitem_relationships",
            joinColumns = @JoinColumn(name = "idOfCategoryItem"),
            inverseJoinColumns = @JoinColumn(name = "idOfDish"))
    private Set<WtDish> dishes = new HashSet<>();

    @OneToMany(mappedBy = "categoryItem")
    private List<MenuSupplier> menuSupplierList;

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
        WtCategoryItem that = (WtCategoryItem) o;
        return Objects.equals(idOfCategoryItem, that.idOfCategoryItem) && Objects.equals(createDate, that.createDate)
                && Objects.equals(lastUpdate, that.lastUpdate) && Objects.equals(version, that.version) && Objects
                .equals(guid, that.guid) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfCategoryItem, createDate, lastUpdate, version, guid, description);
    }
}
