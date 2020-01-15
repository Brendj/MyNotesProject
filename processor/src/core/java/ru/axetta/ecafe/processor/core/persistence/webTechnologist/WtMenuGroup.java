/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class WtMenuGroup {

    private Long id;
    private String name;
    private Date createDate;
    private Date lastUpdate;
    private Long version;
    private Integer deleteState;
    private WtMenu wtMenu;
    private Set<WtDish> dishes = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public WtMenu getWtMenu() {
        return wtMenu;
    }

    public void setWtMenu(WtMenu wtMenu) {
        this.wtMenu = wtMenu;
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
        WtMenuGroup that = (WtMenuGroup) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects
                .equals(createDate, that.createDate) && Objects.equals(lastUpdate, that.lastUpdate) && Objects
                .equals(version, that.version) && Objects.equals(deleteState, that.deleteState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, createDate, lastUpdate, version, deleteState);
    }

}
