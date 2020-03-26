/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.User;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    @ManyToOne
    @JoinColumn(name = "idOfOrgGroup")
    private WtOrgGroup wtOrgGroup;

    @ManyToOne
    @JoinColumn(name = "create_by_id")
    private User createdUser;

    @ManyToOne
    @JoinColumn(name = "update_by_id")
    private User updatedUser;

    @Column(name = "version")
    private Long version;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idOfContragent")
    private Contragent contragent;

    @Column(name = "deleteState")
    private Integer deleteState;

    @ManyToMany
    @JoinTable(name = "cf_wt_dishes_menu_relationships",
            joinColumns = @JoinColumn(name = "idOfMenu"),
            inverseJoinColumns = @JoinColumn(name = "idOfDish"))
    private Set<WtDish> dishes = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "cf_wt_menu_org",
            joinColumns = @JoinColumn(name = "idOfMenu"),
            inverseJoinColumns = @JoinColumn(name = "idOfOrg"))
    private Set<Org> orgs = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "cf_wt_dishes_menu_relationships",
            joinColumns = @JoinColumn(name = "idOfMenu"),
            inverseJoinColumns = @JoinColumn(name = "idOfMenuGroup"))
    private Set<WtMenuGroup> menuGroups = new HashSet<>();

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

    public Set<WtMenuGroup> getMenuGroups() {
        return menuGroups;
    }

    public void setMenuGroups(Set<WtMenuGroup> menuGroups) {
        this.menuGroups = menuGroups;
    }

    public WtOrgGroup getWtOrgGroup() {
        return wtOrgGroup;
    }

    public void setWtOrgGroup(WtOrgGroup wtOrgGroup) {
        this.wtOrgGroup = wtOrgGroup;
    }

    public User getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(User createdUser) {
        this.createdUser = createdUser;
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
