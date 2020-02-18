/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idOfOrgGroup")
    private WtOrgGroup wtOrgGroup;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "create_by_id")
    private User createdUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "update_by_id")
    private User updatedUser;

    @Column(name = "version")
    private Long version;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idOfContragent")
    private Contragent contragent;

    @Column(name = "deleteState")
    private Integer deleteState;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "cf_wt_dishes_menu_relationships",
            joinColumns = @JoinColumn(name = "idOfMenu"),
            inverseJoinColumns = @JoinColumn(name = "idOfDish"))
    private List<WtDish> dishes = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "cf_wt_menu_org",
            joinColumns = @JoinColumn(name = "idOfMenu"),
            inverseJoinColumns = @JoinColumn(name = "idOfOrg"))
    private List<Org> orgs = new ArrayList<>();

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

    public List<WtMenuGroup> getMenuGroupList() {
        return menuGroupList;
    }

    public void setMenuGroupList(List<WtMenuGroup> menuGroupList) {
        this.menuGroupList = menuGroupList;
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

    public void setDishes(List<WtDish> dishes) {
        this.dishes = dishes;
    }

    public void setOrgs(List<Org> orgs) {
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
