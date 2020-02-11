/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import ru.axetta.ecafe.processor.core.persistence.MenuSupplier;
import ru.axetta.ecafe.processor.core.persistence.Org;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "cf_wt_menu")
public class WtMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idOfMenu")
    private Long idOfMenu;

    @Column(name = "menuName")
    private String menuName;

    @Column(name = "beginDate")
    private Date beginDate;

    @Column(name = "endDate")
    private Date endDate;

    @Column(name = "createDate")
    private Date createDate;

    @Column(name = "lastUpdate")
    private Date lastUpdate;

    @Column(name = "version")
    private Long version;

    @Column(name = "deleteState")
    private Integer deleteState;

    @ManyToMany
    @JoinTable(name = "cf_wt_dishes_menu_relationships",
            joinColumns = @JoinColumn(name = "idOfMenu"),
            inverseJoinColumns = @JoinColumn(name = "idOfDish"))
    private Set<WtDish> dishes = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "cf_wt_menu_org",
            joinColumns = @JoinColumn(name = "idOfMenu"),
            inverseJoinColumns = @JoinColumn(name = "idOfOrg"))
    private Set<Org> orgs = new HashSet<>();

    @OneToMany(mappedBy = "menu")
    private List<MenuSupplier> menuSupplierList;

    @OneToMany(mappedBy = "wtMenu")
    private List<WtMenuGroup> menuGroupList;

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

    public List<WtMenuGroup> getMenuGroupList() {
        return menuGroupList;
    }

    public void setMenuGroupList(List<WtMenuGroup> menuGroupList) {
        this.menuGroupList = menuGroupList;
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
