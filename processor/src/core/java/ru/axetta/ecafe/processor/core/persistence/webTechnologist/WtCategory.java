/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import ru.axetta.ecafe.processor.core.persistence.User;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

//@Entity
//@Table(name = "cf_wt_categories")
public class WtCategory {
    public static final int ACTIVE = 0;
    public static final int DELETE = 1;

   /*@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cf_wt_category_items_idofcategories_seq")
    //@Column(name = "idofcategory")*/
    private Long idOfCategory;

    //@Column(name = "createdate")
    private Date createDate;

    //@Column(name = "lastupdate")
    private Date lastUpdate;

    //@Column(name = "version")
    private Long version;

    //@ManyToOne
    //@JoinColumn(name = "idofuser")
    private User user;

    //@Column(name = "guid")
    private String guid;

    //@Column(name = "description")
    private String description;

    //@Column(name = "deletestate")
    private Integer deleteState;

    //@ManyToMany
    /*@JoinTable(name = "cf_wt_dish_categoryitem_relationships",
            joinColumns = @JoinColumn(name = "idOfCategoryItem"),
            inverseJoinColumns = @JoinColumn(name = "idOfDish"))*/
    private Set<WtDish> dishes = new HashSet<>();

    //@OneToMany
    private Set<WtCategoryItem> categoryItems = new HashSet<>();

    public Set<WtCategoryItem> getCategoryItems() {
        return categoryItems;
    }

    public void setCategoryItems(Set<WtCategoryItem> categoryItems) {
        this.categoryItems = categoryItems;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getDeleteState() {
        return deleteState;
    }

    public void setDeleteState(Integer deleteState) {
        this.deleteState = deleteState;
    }

    public Long getIdOfCategory() {
        return idOfCategory;
    }

    public void setIdOfCategory(Long idOfCategory) {
        this.idOfCategory = idOfCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WtCategory)) {
            return false;
        }
        WtCategory that = (WtCategory) o;
        return Objects.equals(getIdOfCategory(), that.getIdOfCategory());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdOfCategory());
    }
}
