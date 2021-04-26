/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model.wt;

import ru.iteco.restservice.model.Contragent;
import ru.iteco.restservice.model.Org;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cf_wt_menu")
public class WtMenu {

    @Id
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

    @Column(name = "version")
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idOfContragent")
    private Contragent contragent;

    @Column(name = "deleteState")
    private Integer deleteState;

    @OneToMany(mappedBy = "menu")
    private Set<WtMenuGroupMenu> menuGroupMenus = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cf_wt_menu_org", joinColumns = @JoinColumn(name = "idOfMenu"), inverseJoinColumns = @JoinColumn(name = "idOfOrg"))
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

    public Set<WtMenuGroupMenu> getMenuGroupMenus() {
        return menuGroupMenus;
    }

    public void setMenuGroupMenus(Set<WtMenuGroupMenu> menuGroupMenus) {
        this.menuGroupMenus = menuGroupMenus;
    }

    public Set<Org> getOrgs() {
        return orgs;
    }

    public void setOrgs(Set<Org> orgs) {
        this.orgs = orgs;
    }

    public WtOrgGroup getWtOrgGroup() {
        return wtOrgGroup;
    }

    public void setWtOrgGroup(WtOrgGroup wtOrgGroup) {
        this.wtOrgGroup = wtOrgGroup;
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
