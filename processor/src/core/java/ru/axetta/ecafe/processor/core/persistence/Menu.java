/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 27.06.2009
 * Time: 14:02:41
 * To change this template use File | Settings | File Templates.
 */
public class Menu {
    public static final int FLAG_NONE=0, FLAG_ANCHOR_MENU=1;

    public static final int CONTRAGENT_MENU_SOURCE = 0;
    public static final int ORG_MENU_SOURCE = 1;

    private Long idOfMenu;
    private Org org;
    private Date menuDate;
    private Date createTime;
    private Integer menuSource;
    private int flags;
    private Integer detailsHashCode;
    private Set<MenuDetail> menuDetails = new HashSet<MenuDetail>();

    protected Menu() {
        // For Hibernate only
    }

    public Menu(Org org, Date menuDate, Date createTime, int menuSource, int flags, Integer detailsHashCode) throws Exception {
        this.org = org;
        this.menuDate = menuDate;
        this.createTime = createTime;
        this.menuSource = menuSource;
        this.flags = flags;
        this.detailsHashCode = detailsHashCode;
    }

    public Long getIdOfMenu() {
        return idOfMenu;
    }

    private void setIdOfMenu(Long idOfMenu) {
        // For Hibernate only
        this.idOfMenu = idOfMenu;
    }

    public Org getOrg() {
        return org;
    }

    private void setOrg(Org org) {
        // For Hibernate only
        this.org = org;
    }

    public Date getMenuDate() {
        return menuDate;
    }

    private void setMenuDate(Date menuDate) {
        // For Hibernate only
        this.menuDate = menuDate;
    }

    public Date getCreateTime() {
        return createTime;
    }

    private void setCreateTime(Date createTime) {
        // For Hibernate only
        this.createTime = createTime;
    }

    public Integer getMenuSource() {
        return menuSource;
    }

    private void setMenuSource(Integer menuSource) {
        // For Hibernate only
        this.menuSource = menuSource;
    }

    private Set<MenuDetail> getMenuDetailsInternal() {
        // For Hibernate only
        return menuDetails;
    }

    private void setMenuDetailsInternal(Set<MenuDetail> menuDetails) {
        // For Hibernate only
        this.menuDetails = menuDetails;
    }

    public Set<MenuDetail> getMenuDetails() {
        return Collections.unmodifiableSet(getMenuDetailsInternal());
    }

    public void addMenuDetail(MenuDetail menuDetail) {
        getMenuDetailsInternal().add(menuDetail);
    }

    public void removeMenuDetail(MenuDetail menuDetail) {
        getMenuDetailsInternal().remove(menuDetail);
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public Integer getDetailsHashCode() {
        return detailsHashCode;
    }

    public void setDetailsHashCode(Integer detailsHashCode) {
        this.detailsHashCode = detailsHashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Menu)) {
            return false;
        }
        final Menu menu = (Menu) o;
        return idOfMenu.equals(menu.getIdOfMenu());
    }

    @Override
    public int hashCode() {
        return idOfMenu.hashCode();
    }

    @Override
    public String toString() {
        return "Menu{" + "menuSource=" + menuSource + ", createTime=" + createTime + ", menuDate=" + menuDate + ", org="
                + org + ", idOfMenu=" + idOfMenu + '}';
    }
}