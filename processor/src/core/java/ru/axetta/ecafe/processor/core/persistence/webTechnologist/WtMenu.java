/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import ru.axetta.ecafe.processor.core.persistence.Org;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class WtMenu {

    private Long idOfMenu;
    private String menuName;
    private Date beginDate;
    private Date endDate;
    private Date createDate;
    private Date lastUpdate;
    private Long version;
    private Integer deleteState;
    private Set<WtDish> dishes = new HashSet<>();
    private Set<Org> orgs = new HashSet<>();

    public Long getIdOfMenu() {
        return idOfMenu;
    }

    public void setIdOfMenu(Long idOfMenu) {
        this.idOfMenu = idOfMenu;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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

    public Set<WtDish> getDishes() {
        return dishes;
    }

    public void setDishes(Set<WtDish> dishes) {
        this.dishes = dishes;
    }

    public Set<Org> getOrgs() {
        return orgs;
    }

    public void setOrgs(Set<Org> orgs) {
        this.orgs = orgs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WtMenu wtMenu = (WtMenu) o;
        return Objects.equals(idOfMenu, wtMenu.idOfMenu) && Objects.equals(menuName, wtMenu.menuName) && Objects
                .equals(beginDate, wtMenu.beginDate) && Objects.equals(endDate, wtMenu.endDate) && Objects
                .equals(createDate, wtMenu.createDate) && Objects.equals(lastUpdate, wtMenu.lastUpdate) && Objects
                .equals(version, wtMenu.version) && Objects.equals(deleteState, wtMenu.deleteState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfMenu, menuName, beginDate, endDate, createDate, lastUpdate, version, deleteState);
    }
}
