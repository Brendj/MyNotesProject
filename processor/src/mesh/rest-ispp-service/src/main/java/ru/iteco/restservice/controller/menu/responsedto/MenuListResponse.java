package ru.iteco.restservice.controller.menu.responsedto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

public class MenuListResponse {
    @Schema(description = "Список категория блюд")
    private final List<CategoryItem> categoryItems;

    public MenuListResponse() {
        this.categoryItems = new ArrayList<>();
    }

    public List<CategoryItem> getCategoryItems() {
        return categoryItems;
    }

    //public void setMenuDetailList(List<MenuDetail> menuDetailList) {
    //    this.menuDetailList = menuDetailList;
    //}
}
