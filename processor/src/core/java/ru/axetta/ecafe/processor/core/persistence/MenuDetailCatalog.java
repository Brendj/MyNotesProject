/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 27.06.2009
 * Time: 14:02:41
 * To change this template use File | Settings | File Templates.
 */
public class MenuDetailCatalog {

    public static final String DEFAULT_GROUP_NAME = "Прочие";

    private Long idOfMenuDetailsCatalog;
    private Menu menu;
    private String name;
    private String path;
    private Long localIdOfMenu;

    MenuDetailCatalog() {
        // For Hibernate only
    }

    public MenuDetailCatalog(Menu menu, String name, String path) {
        this.menu = menu;
        this.name = name;
        this.path = path;
    }

    public Long getIdOfMenuDetailsCatalog() {
        return idOfMenuDetailsCatalog;
    }

    public void setIdOfMenuDetailsCatalog(Long idOfMenuDetailsCatalog) {
        this.idOfMenuDetailsCatalog = idOfMenuDetailsCatalog;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getLocalIdOfMenu() {
        return localIdOfMenu;
    }

    public void setLocalIdOfMenu(Long localIdOfMenu) {
        this.localIdOfMenu = localIdOfMenu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MenuDetailCatalog)) {
            return false;
        }
        final MenuDetailCatalog that = (MenuDetailCatalog) o;
        return idOfMenuDetailsCatalog.equals(that.getIdOfMenuDetailsCatalog());
    }

    @Override
    public int hashCode() {
        return idOfMenuDetailsCatalog.hashCode();
    }

    @Override
    public String toString() {
        return "MenuDetailCatalog{" +
                "idOfMenuDetailsCatalog=" + idOfMenuDetailsCatalog +
                ", menu=" + menu +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

}