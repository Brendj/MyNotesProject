/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class WtUserCatalogItem {

    private Long idOfUserCatalogItem;
    private String description;
    private String guid;
    private Long createDate;
    private Long lastUpdate;
    private Long version;
    private Integer deleteState;
    private Set<WtDish> dishes = new HashSet<>();

    public Long getIdOfUserCatalogItem() {
        return idOfUserCatalogItem;
    }

    public void setIdOfUserCatalogItem(Long idOfUserCatalogItem) {
        this.idOfUserCatalogItem = idOfUserCatalogItem;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Long lastUpdate) {
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

    public Set<WtDish> getDishes() {
        return dishes;
    }

    public void setDishes(Set<WtDish> dishes) {
        this.dishes = dishes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WtUserCatalogItem that = (WtUserCatalogItem) o;
        return Objects.equals(idOfUserCatalogItem, that.idOfUserCatalogItem) && Objects
                .equals(description, that.description) && Objects.equals(guid, that.guid) && Objects
                .equals(createDate, that.createDate) && Objects.equals(lastUpdate, that.lastUpdate) && Objects
                .equals(version, that.version) && Objects.equals(deleteState, that.deleteState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfUserCatalogItem, description, guid, createDate, lastUpdate, version, deleteState);
    }
}
