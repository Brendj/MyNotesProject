package ru.iteco.restservice.controller.menu.responsedto;

import java.util.ArrayList;
import java.util.List;

public class MenuListResponse {
    private final List<MenuItem> menuItems;

    public MenuListResponse() {
        this.menuItems = new ArrayList<>();
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    //public void setMenuDetailList(List<MenuDetail> menuDetailList) {
    //    this.menuDetailList = menuDetailList;
    //}
}
